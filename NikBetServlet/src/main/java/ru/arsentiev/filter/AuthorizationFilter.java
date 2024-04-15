package ru.arsentiev.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import static ru.arsentiev.utils.AttributeGetter.NAME_ATTRIBUTE_USER;
import static ru.arsentiev.utils.UrlPathGetter.LOGIN_URL;
import static ru.arsentiev.utils.UrlPathGetter.REGISTRATION_URL;

@WebFilter("/*")
public class AuthorizationFilter implements Filter {

    private static final List<String> PUBLIC_URI_PATH = List.of(REGISTRATION_URL, LOGIN_URL);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        var uri = ((HttpServletRequest) servletRequest).getRequestURI();
        if (isPublishUri(uri) || isUserLogin(servletRequest)) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            ((HttpServletResponse) servletResponse).sendRedirect(LOGIN_URL);
        }
    }

    private boolean isPublishUri(String myUri) {
        return PUBLIC_URI_PATH.stream().anyMatch(myUri::startsWith);
    }

    private boolean isUserLogin(ServletRequest servletRequest) {
        var user = ((HttpServletRequest) servletRequest).getSession().getAttribute(NAME_ATTRIBUTE_USER);
        return user != null;
    }
}
