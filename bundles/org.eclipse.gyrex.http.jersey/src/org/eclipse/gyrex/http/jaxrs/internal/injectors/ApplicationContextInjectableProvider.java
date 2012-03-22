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
package org.eclipse.gyrex.http.jaxrs.internal.injectors;

import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.eclipse.gyrex.http.application.context.IApplicationContext;

import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;

/**
 * {@link Context} Injector for {@link IApplicationContext}.
 */
@Provider
public final class ApplicationContextInjectableProvider extends SingletonTypeInjectableProvider<Context, IApplicationContext> {
	public ApplicationContextInjectableProvider(final IApplicationContext instance) {
		super(IApplicationContext.class, instance);
	}
}