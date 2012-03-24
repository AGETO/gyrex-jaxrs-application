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
import org.eclipse.gyrex.http.jaxrs.JaxRsApplicationProviderComponent;
import org.eclipse.gyrex.http.jaxrs.jersey.spi.inject.ContextApplicationContextInjectableProvider;
import org.eclipse.gyrex.http.jaxrs.jersey.spi.inject.ContextRuntimeContextInjectableProvider;
import org.eclipse.gyrex.http.jaxrs.jersey.spi.inject.ContextServiceInjectableProvider;
import org.eclipse.gyrex.http.jaxrs.jersey.spi.inject.InjectApplicationContextInjectableProvider;
import org.eclipse.gyrex.http.jaxrs.jersey.spi.inject.InjectRuntimeContextInjectableProvider;
import org.eclipse.gyrex.http.jaxrs.jersey.spi.inject.InjectServiceInjectableProvider;

import org.osgi.framework.Bundle;

import com.sun.jersey.api.core.ResourceConfig;

/**
 * Extension of {@link JaxRsApplication} which is used by
 * {@link JaxRsApplicationProviderComponent} and automatically scans a bundle
 * for resources.
 */
public final class ScanningJaxRsApplication extends JaxRsApplication {

	private final ResourceConfig resourceConfig;
	private ContextServiceInjectableProvider contextServiceInjector;
	private InjectServiceInjectableProvider injectServiceInjector;
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
		// add service injector if available
		if (null != contextServiceInjector) {
			resourceConfig.getSingletons().add(contextServiceInjector);
		}
		if (null != injectServiceInjector) {
			resourceConfig.getSingletons().add(injectServiceInjector);
		}

		// add more interesting injectors
		resourceConfig.getSingletons().add(new ContextRuntimeContextInjectableProvider(getContext()));
		resourceConfig.getSingletons().add(new InjectRuntimeContextInjectableProvider(getContext()));
		resourceConfig.getSingletons().add(new ContextApplicationContextInjectableProvider(getApplicationContext()));
		resourceConfig.getSingletons().add(new InjectApplicationContextInjectableProvider(getApplicationContext()));

		// done
		return resourceConfig;
	}

	@Override
	protected void doDestroy() {
		try {
			// call super
			super.doDestroy();
		} finally {
			if (null != contextServiceInjector) {
				contextServiceInjector.dispose();
				contextServiceInjector = null;
			}
			if (null != injectServiceInjector) {
				injectServiceInjector.dispose();
				injectServiceInjector = null;
			}
		}
	}

	@Override
	protected void doInit() throws IllegalStateException, Exception {
		// create service injector
		contextServiceInjector = new ContextServiceInjectableProvider(bundle.getBundleContext());
		injectServiceInjector = new InjectServiceInjectableProvider(bundle.getBundleContext());
		// call super
		super.doInit();
	}
}