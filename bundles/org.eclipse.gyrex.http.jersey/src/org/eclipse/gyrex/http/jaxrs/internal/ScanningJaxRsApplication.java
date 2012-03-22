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

import org.eclipse.gyrex.context.IRuntimeContext;
import org.eclipse.gyrex.http.jaxrs.JaxRsApplication;

import org.osgi.framework.Bundle;

import com.sun.jersey.api.core.ResourceConfig;

public final class ScanningJaxRsApplication extends JaxRsApplication {

	private final ResourceConfig resourceConfig;
	private ServiceInjectableProvider serviceInjector;
	private final Bundle bundle;

	public ScanningJaxRsApplication(final String id, final IRuntimeContext context, final Bundle bundle) {
		super(id, context);
		this.bundle = bundle;

		// create a scanning resource configuration which scans the bundle
		// for all possible resources
		resourceConfig = new BundleScanningResourceConfig(bundle);
	}

	@Override
	protected javax.ws.rs.core.Application createJaxRsApplication() {
		if (null != serviceInjector) {
			resourceConfig.getSingletons().add(serviceInjector);
		}

		return resourceConfig;
	}

	@Override
	protected void doDestroy() {
		if (null != serviceInjector) {
			serviceInjector.dispose();
		}
		try {
			// call super
			super.doDestroy();
		} finally {
			if (null != serviceInjector) {
				serviceInjector.dispose();
				serviceInjector = null;
			}
		}
	}

	@Override
	protected void doInit() throws IllegalStateException, Exception {
		// create service injector
		serviceInjector = new ServiceInjectableProvider(bundle.getBundleContext());
		// call super
		super.doInit();
	}
}