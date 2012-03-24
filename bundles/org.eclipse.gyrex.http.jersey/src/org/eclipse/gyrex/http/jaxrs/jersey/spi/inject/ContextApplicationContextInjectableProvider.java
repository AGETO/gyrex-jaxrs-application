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

import org.eclipse.gyrex.http.application.context.IApplicationContext;

/**
 * {@link Context} Injector for {@link IApplicationContext}.
 * <p>
 * This class may be instantiated by clients. However, they must be aware that
 * this code is based on 3rd party code which may have different evolution and
 * versioning guidelines.
 * </p>
 */
public final class ContextApplicationContextInjectableProvider extends BaseApplicationContextInjectableProvider<Context> {
	public ContextApplicationContextInjectableProvider(final IApplicationContext instance) {
		super(instance);
	}
}