package com.xingyu.auth;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import org.apache.shiro.authz.UnauthorizedException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTFilter extends BasicHttpAuthenticationFilter {

	private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	/**
	 * to check if Authorization is included into header
	 */
	@Override
	protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
		HttpServletRequest req = (HttpServletRequest) request;
		String authorization = req.getHeader("Authorization");
		return authorization != null;
	}

	@Override
	protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String authorization = httpServletRequest.getHeader("Authorization");

		JWTToken token = new JWTToken(authorization);
		SecurityUtils.getSubject().login(token);
		return true;
	}

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		if (isLoginAttempt(request, response)) {
			try {
				executeLogin(request, response);
			} catch (Exception e) {
                responseInvalidToken(request, response);
			}
		}
		return true;
	}

    private void responseInvalidToken(ServletRequest req, ServletResponse resp) {
        try {
            HttpServletResponse httpServletResponse = (HttpServletResponse) resp;
            httpServletResponse.sendRedirect("/invalid_token");
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }
}