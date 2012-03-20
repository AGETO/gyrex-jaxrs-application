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
package org.eclipse.gyrex.http.jaxrs;

import java.util.Collections;
import java.util.Set;

import org.eclipse.gyrex.context.IRuntimeContext;
import org.eclipse.gyrex.http.application.Application;
import org.eclipse.gyrex.http.application.context.IApplicationContext;

import com.sun.jersey.api.container.filter.LoggingFilter;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 * Base class for HTTP Applications with support for JAX-RS web services.
 * <p>
 * This class provides a convenient default implementation for launching a
 * JAX-RS runtime and registering resources and singletons. Subclasses typically
 * override {@link #getJaxRsClasses()} and/or {@link #getJaxRsSingletons()} to
 * provide the resources to te JAX-RS runtime.
 * </p>
 */
public class JaxRsApplication extends Application {

	/**
	 * Creates a new instance.
	 * 
	 * @param id
	 * @param context
	 */
	protected JaxRsApplication(final String id, final IRuntimeContext context) {
		super(id, context);
	}

	/**
	 * Initializes the JAX-RS application and registers it at the configure
	 * {@link #getJaxRsAlias() alias}.
	 * <p>
	 * Subclasses may override and perform additional initialization. However,
	 * they must call super to initialize the JAX-RS runtime.
	 * </p>
	 * 
	 * @throws IllegalStateException
	 *             in case the initialization can not be completed currently but
	 *             may be repeated at a later time
	 * @throws Exception
	 *             in case of unrecoverable initialization failures
	 */
	@Override
	protected void doInit() throws IllegalStateException, Exception {
		// delegate discovery to the JaxRsApplication
		// note, there is an additional trick in Equinox that works here
		// by providing an anonymous implementation here, the Equinox-TCCL
		// may find the bundle that extends the class
		final DefaultResourceConfig resourceConfig = new DefaultResourceConfig() {
			@Override
			public Set<Class<?>> getClasses() {
				final Set<Class<?>> classes = JaxRsApplication.this.getJaxRsClasses();
				if (null == classes) {
					return Collections.emptySet();
				}
				return classes;
			}

			@Override
			public Set<Object> getSingletons() {
				final Set<Object> singletons = JaxRsApplication.this.getJaxRsSingletons();
				if (null == singletons) {
					return Collections.emptySet();
				}
				return singletons;
			}
		};

		// TODO - make that extensible
		resourceConfig.getProperties().put(ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS, LoggingFilter.class.getName());
		resourceConfig.getProperties().put(ResourceConfig.PROPERTY_CONTAINER_RESPONSE_FILTERS, LoggingFilter.class.getName());

		// register
		getApplicationContext().registerServlet(getJaxRsAlias(), new ServletContainer(resourceConfig), null);
	}

	/**
	 * Returns an alias for registering the JAX-RS runtime.
	 * <p>
	 * The default implementation returns the root alias '/'. Subclasses may
	 * override to return a custom alias. However, the returned alias must
	 * comply to the alias rules spec'd by
	 * {@link IApplicationContext#registerServlet(String, javax.servlet.Servlet, java.util.Map)}
	 * for proper registration.
	 * </p>
	 * 
	 * @return the alias for registering the JAX-RS runtime
	 */
	protected String getJaxRsAlias() {
		return "/";
	}

	/**
	 * Returns all available JAX-RS classes.
	 * <p>
	 * This method is the direct equivalent to JAX-RS
	 * {@link javax.ws.rs.core.Application#getClasses()}. It is called during
	 * initialization of the JAX-RS runtime to obtain all available classes that
	 * should be passed to the JAX-RS runtime.
	 * </p>
	 * <p>
	 * The default implementation returns an empty set. Subclasses may override.
	 * </p>
	 * 
	 * @return a set of classes
	 * @see javax.ws.rs.core.Application#getClasses()
	 */
	protected Set<Class<?>> getJaxRsClasses() {
		return Collections.emptySet();
	}

	/**
	 * Returns all available JAX-RS singletons.
	 * <p>
	 * This method is the direct equivalent to JAX-RS
	 * {@link javax.ws.rs.core.Application#getSingletons()}. It is called during
	 * initialization of the JAX-RS runtime to obtain all available singletons
	 * that should be passed to the JAX-RS runtime.
	 * </p>
	 * <p>
	 * The default implementation returns an empty set. Subclasses may override.
	 * </p>
	 * 
	 * @return a set of singletons
	 */
	protected Set<Object> getJaxRsSingletons() {
		return Collections.emptySet();
	}

}
