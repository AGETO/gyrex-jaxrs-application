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

import org.eclipse.gyrex.context.IRuntimeContext;

import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;

/**
 * {@link Context} Injector for {@link IRuntimeContext}.
 */
@Provider
public final class RuntimeContextInjectableProvider extends SingletonTypeInjectableProvider<Context, IRuntimeContext> {
	public RuntimeContextInjectableProvider(final IRuntimeContext instance) {
		super(IRuntimeContext.class, instance);
	}
}