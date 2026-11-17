package ro.asilag.resources;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.apache.commons.text.StringSubstitutor;
import ro.asilag.service.TestService;

import java.util.Map;

@Path("/test")
@ApplicationScoped
public class TestResource {

    @Inject
    private TestService testService;

    @GET
    @Path("/check")
    @Produces(MediaType.APPLICATION_JSON)
    public String test() {
        Map<String, String> context = Map.of("status", testService.status());
        String responseTemplate =  """
            {
              "status": "${status}"
            }
            """;
        return new StringSubstitutor(context).replace(responseTemplate);
    }

}
