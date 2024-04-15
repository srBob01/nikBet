package ru.arsentiev.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.dto.user.UserDto;

import java.io.IOException;

import static ru.arsentiev.utils.AttributeGetter.NAME_ATTRIBUTE_USER;
import static ru.arsentiev.utils.UrlPathGetter.ADMIN_DEFAULT_URL;
import static ru.arsentiev.utils.UrlPathGetter.LOGIN_URL;

@WebFilter("/user/*")
public class UserFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        UserDto user = (UserDto) ((HttpServletRequest) servletRequest).getSession().getAttribute(NAME_ATTRIBUTE_USER);
        switch (user.role()) {
            case ADMIN -> ((HttpServletResponse) servletResponse).sendRedirect(ADMIN_DEFAULT_URL);
            case USER -> filterChain.doFilter(servletRequest, servletResponse);
            case null -> throw new RuntimeException();
        }
    }
}
