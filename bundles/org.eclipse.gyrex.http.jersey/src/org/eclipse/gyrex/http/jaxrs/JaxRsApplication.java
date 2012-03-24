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
import org.eclipse.gyrex.http.jaxrs.jersey.spi.inject.ContextApplicationContextInjectableProvider;
import org.eclipse.gyrex.http.jaxrs.jersey.spi.inject.ContextRuntimeContextInjectableProvider;
import org.eclipse.gyrex.http.jaxrs.jersey.spi.inject.InjectApplicationContextInjectableProvider;
import org.eclipse.gyrex.http.jaxrs.jersey.spi.inject.InjectRuntimeContextInjectableProvider;

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
 * <p>
 * This class may be instantiated or extended by clients.
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
	 * Called by {@link #doInit()} to create the JAX-RS Application object.
	 * <p>
	 * The default implementation creates an application object that is
	 * populated with classes and singletons returned by
	 * {@link #getJaxRsClasses()} and {@link #getJaxRsSingletons()}. Subclasses
	 * may override to provider a more specialized application.
	 * </p>
	 * 
	 * @return the JAX-RS Application object (must not be <code>null</code>)
	 */
	protected javax.ws.rs.core.Application createJaxRsApplication() {
		final DefaultResourceConfig resourceConfig = new DefaultResourceConfig();

		final Set<Class<?>> classes = getJaxRsClasses();
		if (null != classes) {
			resourceConfig.getClasses().addAll(classes);
		}
		final Set<Object> singletons = getJaxRsSingletons();
		if (null != singletons) {
			resourceConfig.getSingletons().addAll(singletons);
		}

		// add more interesting injectors
		resourceConfig.getSingletons().add(new ContextRuntimeContextInjectableProvider(getContext()));
		resourceConfig.getSingletons().add(new ContextApplicationContextInjectableProvider(getApplicationContext()));
		resourceConfig.getSingletons().add(new InjectRuntimeContextInjectableProvider(getContext()));
		resourceConfig.getSingletons().add(new InjectApplicationContextInjectableProvider(getApplicationContext()));

		// add init properties
		resourceConfig.getProperties().putAll(getApplicationContext().getInitProperties());

		// TODO - make that configurable
		if (!resourceConfig.getProperties().containsKey(ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS)) {
			resourceConfig.getProperties().put(ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS, LoggingFilter.class.getName());
		}
		if (!resourceConfig.getProperties().containsKey(ResourceConfig.PROPERTY_CONTAINER_RESPONSE_FILTERS)) {
			resourceConfig.getProperties().put(ResourceConfig.PROPERTY_CONTAINER_RESPONSE_FILTERS, LoggingFilter.class.getName());
		}

		return resourceConfig;
	}

	/**
	 * Initializes the JAX-RS application and registers it at the configured
	 * {@link #getJaxRsAlias() alias}.
	 * <p>
	 * Subclasses may override and perform additional initialization. However,
	 * they must call super to initialize the JAX-RS runtime.
	 * </p>
	 * <p>
	 * This implementation calls {@link #createJaxRsApplication()} to create the
	 * JAX-RS Application object that will be used for configuring the JAX-RS
	 * runtime.
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
		final javax.ws.rs.core.Application jaxRsApplication = createJaxRsApplication();
		if (null == jaxRsApplication) {
			throw new IllegalStateException("no application returned by createJaxRsApplication");
		}

		// register
		getApplicationContext().registerServlet(getJaxRsAlias(), new ServletContainer(jaxRsApplication), null);
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
	 * @return a set of classes (may be <code>null</code>)
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
	 * @return a set of singletons (may be <code>null</code>)
	 */
	protected Set<Object> getJaxRsSingletons() {
		return Collections.emptySet();
	}

}
