/*******************************************************************************
 * Copyright 2010 Alexandru Craciun, and individual contributors as indicated
 * by the @authors tag. 
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 ******************************************************************************/
package org.netxilia.jaxrs.js;

public class ResourceRegistry {
	// protected void processMethod(ResourceFactory ref, String base, Class<?>
	// clazz, Method method) {
	// Path path = method.getAnnotation(Path.class);
	// Set<String> httpMethods = IsHttpMethod.getHttpMethods(method);
	// if (path == null && httpMethods == null) {
	// if (clazz.isInterface())
	// return;
	//
	// Method intfMethod = null;
	// for (Class intf : clazz.getInterfaces()) {
	// try {
	// Method tmp = intf.getMethod(method.getName(),
	// method.getParameterTypes());
	// if (intfMethod != null)
	// throw new
	// RuntimeException("Ambiguous inherited JAX-RS annotations applied to method: "
	// + method);
	// path = tmp.getAnnotation(Path.class);
	// httpMethods = IsHttpMethod.getHttpMethods(tmp);
	// if (path != null || httpMethods != null)
	// intfMethod = tmp;
	// } catch (NoSuchMethodException ignored) {
	// }
	// }
	// if (intfMethod == null)
	// return;
	// processMethod(ref, base, clazz, intfMethod);
	// return;
	// }
	//
	// UriBuilderImpl builder = new UriBuilderImpl();
	// if (base != null)
	// builder.path(base);
	// if (clazz.isAnnotationPresent(Path.class)) {
	// builder.path(clazz);
	// }
	// if (path != null) {
	// builder.path(method);
	// }
	// String pathExpression = builder.getPath();
	// if (pathExpression == null)
	// pathExpression = "";
	//
	// InjectorFactory injectorFactory = new
	// InjectorFactoryImpl(providerFactory);
	// if (httpMethods == null) {
	// ResourceLocator locator = new ResourceLocator(ref, injectorFactory,
	// providerFactory, clazz, method);
	// rootSegment.addPath(pathExpression, locator);
	// } else {
	// ResourceMethod invoker = new ResourceMethod(clazz, method,
	// injectorFactory, ref, providerFactory,
	// httpMethods);
	// rootSegment.addPath(pathExpression, invoker);
	// }
	// size++;
	// }
	//	
	// /**
	// * Given a class, search itself and implemented interfaces for jax-rs
	// annotations.
	// *
	// * @param clazz
	// * @return list of class and intertfaces that have jax-rs annotations
	// */
	// public static Class getRootResourceClass(Class clazz)
	// {
	// return AnnotationResolver.getClassWithAnnotation(clazz, Path.class);
	// }
	//
	// /**
	// * Given a class, search itself and implemented interfaces for jax-rs
	// annotations.
	// *
	// * @param clazz
	// * @return list of class and intertfaces that have jax-rs annotations
	// */
	// public static Class getSubResourceClass(Class clazz)
	// {
	// if (clazz.isAnnotationPresent(Path.class))
	// {
	// return clazz;
	// }
	// for (Method method : clazz.getMethods())
	// {
	// if (method.isAnnotationPresent(Path.class)) return clazz;
	// for (Annotation ann : method.getAnnotations())
	// {
	// if (ann.annotationType().isAnnotationPresent(HttpMethod.class)) return
	// clazz;
	// }
	// }
	// // ok, no @Path or @HttpMethods so look in interfaces.
	// Class[] intfs = clazz.getInterfaces();
	// for (Class intf : intfs)
	// {
	// if (intf.isAnnotationPresent(Path.class))
	// {
	// return intf;
	// }
	// for (Method method : intf.getMethods())
	// {
	// if (method.isAnnotationPresent(Path.class)) return intf;
	// for (Annotation ann : method.getAnnotations())
	// {
	// if (ann.annotationType().isAnnotationPresent(HttpMethod.class)) return
	// intf;
	// }
	// }
	// }
	// return null;
	// }
	//
	// public static boolean isRootResource(Class clazz)
	// {
	// return getRootResourceClass(clazz) != null;
	// }
}
