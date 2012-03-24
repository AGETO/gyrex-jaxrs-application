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

import org.eclipse.gyrex.common.runtime.BaseBundleActivator;

public class JaxRsActivator extends BaseBundleActivator {

	private static final String SYMBOLIC_NAME = "org.eclipse.gyrex.http.jersey";

	public JaxRsActivator() {
		super(SYMBOLIC_NAME);
	}

	@Override
	protected Class getDebugOptions() {
		return JaxRsDebug.class;
	}

}
