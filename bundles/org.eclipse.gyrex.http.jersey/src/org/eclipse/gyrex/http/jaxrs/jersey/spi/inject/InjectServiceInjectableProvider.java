/**
 * Copyright (c) 2012 Gunnar Wagenknecht and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Gunnar Wagenknecht - initial API and implementation
 */
package org.eclipse.gyrex.http.jaxrs.jersey.spi.inject;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import org.osgi.framework.BundleContext;

/**
 * {@link Inject} injector for OSGi services.
 * <p>
 * Clients must call {@link #dispose()} when this provider when no longer used!
 * </p>
 * <p>
 * This class may be instantiated by clients. However, they must be aware that
 * this code is based on 3rd party code which may have different evolution and
 * versioning guidelines.
 * </p>
 */
@Provider
public final class InjectServiceInjectableProvider extends BaseServiceInjectableProvider<Inject> {
	public InjectServiceInjectableProvider(final BundleContext bundleContext) {
		super(bundleContext);
	}
}
