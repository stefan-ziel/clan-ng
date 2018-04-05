package ch.claninfo.clanng.web.rerouter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Simply makes the request have a cacheable
 */
public class RequestCachingFilter implements Filter {
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		if (request instanceof CachingRequestWrapper) {
			chain.doFilter(request, response);

		} else {
			chain.doFilter(new CachingRequestWrapper(request), response);
		}
	}

	@Override
	public void destroy() {
	}
}