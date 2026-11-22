package ro.asilag.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.text.StringSubstitutor;

import java.io.IOException;
import java.util.Map;

public class NotFoundServlet extends HttpServlet {

    @Override
    protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        String uri = (String) req.getAttribute("jakarta.servlet.error.request_uri");
        resp.setContentType("application/json;charset=UTF-8");
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        Map<String, String> context = Map.of("uri", uri);
        String responseTemplate = """
            {
                "status": 404,
                "path": "${uri}",
                "message": "not found"
            }
            """;
        String responseBlock = new StringSubstitutor(context).replace(responseTemplate);
        resp.getWriter().write(responseBlock);
    }

}
