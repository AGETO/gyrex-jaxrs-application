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

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.core.spi.scanning.Scanner;
import com.sun.jersey.core.spi.scanning.ScannerException;
import com.sun.jersey.core.spi.scanning.ScannerListener;

/**
 * A scanner that recursively scans a bundle for all its class files.
 */
public class BundleScanner implements Scanner {

	private static final Logger LOG = LoggerFactory
			.getLogger(BundleScanner.class);

	private final Bundle bundle;

	/**
	 * Creates a new instance.
	 * 
	 * @param bundle
	 *            the bundle to scan
	 */
	public BundleScanner(final Bundle bundle) {
		if (null == bundle) {
			throw new IllegalArgumentException("bundle must not be null");
		}
		this.bundle = bundle;
	}

	@Override
	public void scan(final ScannerListener sl) throws ScannerException {
		final BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
		if (null == bundleWiring) {
			throw new ScannerException(String.format(
					"No wiring available for bundle '%s'", bundle));
		}
		final Collection<String> resources = bundleWiring.listResources("/",
				"*.class", BundleWiring.LISTRESOURCES_LOCAL
						| BundleWiring.LISTRESOURCES_RECURSE);
		if (null == resources) {
			throw new ScannerException(String.format(
					"No resources available for bundle '%s'", bundle));
		}

		final ClassLoader loader = bundleWiring.getClassLoader();
		if (null == loader) {
			throw new ScannerException(String.format(
					"No class loader available for bundle '%s'", bundle));
		}
		for (final String resource : resources) {
			LOG.trace("Found resource: {}", resource);
			if (!sl.onAccept(resource)) {
				LOG.debug("Resource rejected: {}", resource);
				continue;
			}

			InputStream in = null;
			try {
				in = loader.getResourceAsStream(resource);
				sl.onProcess(resource, in);
			} catch (final IOException e) {
				throw new ScannerException(String.format(
						"Error scanning resource '%s': %s", resource,
						ExceptionUtils.getRootCauseMessage(e)), e);
			} finally {
				IOUtils.closeQuietly(in);
			}
		}
	}

}
