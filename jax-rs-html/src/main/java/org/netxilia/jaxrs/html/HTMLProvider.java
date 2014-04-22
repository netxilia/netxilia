/*******************************************************************************
 * 
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
package org.netxilia.jaxrs.html;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Produces("text/html")
@Provider
public class HTMLProvider implements MessageBodyWriter<ModelAndView<?>> {
	public static final String NETXILIA_REDIRECT_COOKIE = "nx.redirect";

	@Context
	private HttpServletResponse response;

	@Context
	private HttpServletRequest request;

	private String modelAttributeName = "model";

	private List<IHTMLProviderInterceptor<?>> interceptors;

	public List<IHTMLProviderInterceptor<?>> getInterceptors() {
		return interceptors;
	}

	public void setInterceptors(List<IHTMLProviderInterceptor<?>> interceptors) {
		this.interceptors = interceptors;
	}

	@Override
	public long getSize(ModelAndView<?> t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mt) {
		return -1;
	}

	@Override
	public boolean isWriteable(Class<?> arg0, Type arg1, Annotation[] arg2, MediaType arg3) {
		// there should be only one html provider and different mappers.
		// TODO does it make sense to check with the mapper here?
		return true;
	}

	/**
	 * The name of the request attribute will contain the object returned by the resource's method call.
	 * 
	 * @return
	 */
	public String getModelAttributeName() {
		return modelAttributeName;
	}

	public void setModelAttributeName(String modelAttributeName) {
		this.modelAttributeName = modelAttributeName;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void writeTo(ModelAndView<?> modelAndView, Class<?> clazz, Type genericType, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, final OutputStream os) throws IOException {
		try {
			httpHeaders.putSingle(javax.ws.rs.core.HttpHeaders.CONTENT_TYPE, mediaType.toString() + ";charset=UTF-8");
			if (interceptors != null) {
				for (IHTMLProviderInterceptor<?> interceptor : interceptors) {
					interceptor.intercept((ModelAndView) modelAndView, request);
				}
			}
			RequestDispatcher dispatcher = request.getRequestDispatcher(modelAndView.getView());
			if (dispatcher == null) {
				throw new IOException("Cannot find the JSP page [" + modelAndView.getView() + "]");
			}
			HttpServletRequestWrapper wrappedRequest = new HttpServletRequestWrapper(request);

			wrappedRequest.setAttribute(modelAttributeName, modelAndView.getModel());
			dispatcher.forward(wrappedRequest, response);

		} catch (Exception e) {
			throw new IOException(e);
		}
	}
}
