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
package org.eclipse.gyrex.http.jaxrs.internal.injectors;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.eclipse.gyrex.common.services.BundleServiceHelper;
import org.eclipse.gyrex.common.services.IServiceProxy;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

/**
 * {@link Context} injector for OSGi services.
 */
@Provider
public class ServiceInjectableProvider implements InjectableProvider<Context, Type> {

	private static final Logger LOG = LoggerFactory.getLogger(ServiceInjectableProvider.class);

	private final BundleContext bundleContext;
	private final BundleServiceHelper serviceHelper;

	/**
	 * Creates a new instance.
	 */
	public ServiceInjectableProvider(final BundleContext bundleContext) {
		this.bundleContext = bundleContext;
		serviceHelper = new BundleServiceHelper(bundleContext);
	}

	public void dispose() {
		serviceHelper.dispose();
	}

	private Class<?> getClass(final Type type) {
		if (type instanceof Class<?>) {
			return (Class<?>) type;
		} else if (type instanceof ParameterizedType) {
			try {
				return (Class<?>) ((ParameterizedType) type).getRawType();
			} catch (final Exception ignored) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Unable to cast raw type of ParameterizedType ({}) to Class.", type, ignored);
				}
			}
		}
		return null;
	}

	private Class<?> getCollectionElementType(final Type type) {
		// assume that the element is a ParameterizedType
		if (type instanceof ParameterizedType) {
			final Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
			// Collections can only be parameterized with one argument
			if (typeArguments.length == 1) {
				return getClass(typeArguments[0]);
			}
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Unable to detect collection element type for ({}).", type);
		}
		return null;
	}

	@Override
	public Injectable getInjectable(final ComponentContext componentContext, final Context contextAnnotation, final Type requestedType) {
		final Class<?> clazz = getClass(requestedType);
		if (null == clazz) {
			return null;
		}

		// check if we have a collection
		final boolean collectionOfServices = Collection.class.isAssignableFrom(clazz) || List.class.isAssignableFrom(clazz);

		// detect OSGi service interface
		final Class<?> serviceInterface = collectionOfServices ? getCollectionElementType(requestedType) : clazz;

		// quck check if there is a service available
		final ServiceReference<?> sr = bundleContext.getServiceReference(serviceInterface.getName());
		if (sr == null) {
			return null;
		}

		// a service reference is available
		// return a proxy to decouple from the service life-cycle
		final IServiceProxy<?> serviceProxy = serviceHelper.trackService(serviceInterface);
		return new Injectable<Object>() {
			@Override
			public Object getValue() {
				return serviceProxy.getProxy();
			}
		};
	}

	@Override
	public ComponentScope getScope() {
		// OSGi services are valid per request only
		return ComponentScope.PerRequest;
	}

}
