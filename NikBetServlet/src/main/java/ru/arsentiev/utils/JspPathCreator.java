package ru.arsentiev.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JspPathCreator {
    private final static String JSP_DEFAULT_FORMAT = "/WEB-INF/jsp/%s.jsp";
    private final static String JSP_USER_FORMAT = "/WEB-INF/jsp/user/%s.jsp";
    private final static String JSP_ADMIN_FORMAT = "/WEB-INF/jsp/admin/%s.jsp";
    public String getDefaultPath(String jsp) {
        return JSP_DEFAULT_FORMAT.formatted(jsp);
    }
    public String getUserPath(String jsp) {
        return JSP_USER_FORMAT.formatted(jsp);
    }
    public String getAdminPath(String jsp) {
        return JSP_ADMIN_FORMAT.formatted(jsp);
    }
}
