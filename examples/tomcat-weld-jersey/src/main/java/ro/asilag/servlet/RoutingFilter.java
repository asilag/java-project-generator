package ro.asilag.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ro.asilag.config.AppConfig;

import java.io.IOException;

public class RoutingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getRequestURI()
                .toLowerCase()
                .substring(req.getContextPath()
                        .length());

        String restRootApp = AppConfig.getRestRootApp();

        if (path.equals("/")
                || path.equals("/index.html")
                || path.startsWith(restRootApp + "/")) {
            chain.doFilter(request, response);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }

    }



}
