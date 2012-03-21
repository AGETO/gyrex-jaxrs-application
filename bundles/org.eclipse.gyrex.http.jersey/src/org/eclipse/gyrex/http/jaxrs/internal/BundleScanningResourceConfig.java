/**
 * Copyright (c) 2012 AGETO Service GmbH and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Gunnar Wagenknecht - initial API and implementation
 */
package org.eclipse.gyrex.http.jaxrs.internal;

import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.core.spi.scanning.ScannerException;
import com.sun.jersey.spi.container.ReloadListener;
import com.sun.jersey.spi.scanning.PathProviderScannerListener;

/**
 * A Jersey resource configuration which scans a bundle for annotated classes.
 */
public class BundleScanningResourceConfig extends DefaultResourceConfig implements ReloadListener {

	private static final Logger LOG = LoggerFactory.getLogger(BundleScanningResourceConfig.class);

	private final Bundle bundle;

	/**
	 * Creates a new instance.
	 */
	public BundleScanningResourceConfig(final Bundle bundle) {
		this.bundle = bundle;

		scan();
	}

	@Override
	public void onReload() {
		getClasses().clear();
		scan();
	}

	private void scan() {
		final BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
		if (null == bundleWiring)
			throw new ScannerException(String.format("No wiring available for bundle '%s'", bundle));

		final ClassLoader loader = bundleWiring.getClassLoader();
		if (null == loader)
			throw new ScannerException(String.format("No class loader available for bundle '%s'", bundle));

		final PathProviderScannerListener scannerListener = new PathProviderScannerListener(loader);
		new BundleScanner(bundle, bundleWiring, loader).scan(scannerListener);

		final Set<Class<?>> annotatedClasses = scannerListener.getAnnotatedClasses();
		for (final Class<?> annotatedClass : annotatedClasses) {
			if (LOG.isDebugEnabled())
				if (annotatedClass.isAnnotationPresent(Path.class))
					LOG.debug("Found resource: {}", annotatedClass.getName());
				else if (annotatedClass.isAnnotationPresent(Provider.class))
					LOG.debug("Found provider: {}", annotatedClass.getName());
			getClasses().add(annotatedClass);
		}
	}

}
