package ro.asilag.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.text.StringSubstitutor;
import ro.asilag.config.AppConfig;

import java.io.IOException;
import java.util.Map;

public class RoutingFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getRequestURI()
                .substring(req.getContextPath().length());

        String restRootApp = AppConfig.getRestRootApp();

        if (path.equals("/") || path.equals("/index.html")
                || path.equals(restRootApp) || path.startsWith(restRootApp + "/")) {
            chain.doFilter(request, response);
            return;
        }

        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        Map<String, String> context = Map.of("path", path);
        String responseTemplate = """
            {
                "code": 404,
                "path": "${path}",
                "message": "not found"
            }
            """;
        String responseBlock = new StringSubstitutor(context).replace(responseTemplate);
        resp.getWriter().write(responseBlock);

    }

}

