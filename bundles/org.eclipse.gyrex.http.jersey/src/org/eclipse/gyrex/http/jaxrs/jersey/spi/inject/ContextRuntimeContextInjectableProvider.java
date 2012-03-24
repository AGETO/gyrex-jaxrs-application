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
package org.eclipse.gyrex.http.jaxrs.jersey.spi.inject;

import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.eclipse.gyrex.context.IRuntimeContext;

/**
 * {@link Context} Injector for {@link IRuntimeContext}.
 * <p>
 * This class may be instantiated by clients. However, they must be aware that
 * this code is based on 3rd party code which may have different evolution and
 * versioning guidelines.
 * </p>
 */
@Provider
public final class ContextRuntimeContextInjectableProvider extends BaseRuntimeContextInjectableProvider<Context> {
	public ContextRuntimeContextInjectableProvider(final IRuntimeContext instance) {
		super(instance);
	}
}