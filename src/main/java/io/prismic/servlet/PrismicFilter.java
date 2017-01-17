package io.prismic.servlet;

import io.prismic.*;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/*
 * WebFilter for JEE applications.
 *
 * Application not running in a servlet container (Play Framework, Android, etc.)
 * should ignore this class.
 */
@WebFilter(filterName = "Prismic")
public class PrismicFilter implements Filter {

	private String endpoint;
	private String accessToken;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
    endpoint = filterConfig.getInitParameter("endpoint");
    accessToken = filterConfig.getInitParameter("accessToken");
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    if (endpoint == null) {
      throw new ServletException("Missing parameter in PrismicFilter: endpoint");
    }
		String referenceFromCookies = getRefFromCookies(req.getCookies());
    Api api = Api.get(endpoint, accessToken, referenceFromCookies);
		request.setAttribute("prismicapi", api);
		chain.doFilter(request, response);
	}

	private String getRefFromCookies(Cookie[] cookies) {
		if(cookies == null) {
			return null;
		}
		String experimentCookie = null;
		String previewCookie = null;
        for(Cookie cookie : cookies) {
        	if (Prismic.EXPERIMENTS_COOKIE.equals(cookie.getName())) {
        		experimentCookie = cookie.getValue();
        	}
        	if (Prismic.PREVIEW_COOKIE.equals(cookie.getName())) {
        		previewCookie = cookie.getValue();
        	}
        }
		if (previewCookie != null) {
			return previewCookie;
		}
		if (experimentCookie != null) {
			return experimentCookie;
		}
		return null;
	}

}
