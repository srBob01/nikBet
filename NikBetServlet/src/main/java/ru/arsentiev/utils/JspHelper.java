package ru.arsentiev.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JspHelper {
    private final static String JSP_FORMAT = "/WEB-INF/jsp/%s.jsp";
    public String getPath(String jsp) {
        return JSP_FORMAT.formatted(jsp);
    }
}
