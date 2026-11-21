package ro.asilag.resource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import ro.asilag.service.TestService;

@Path("/test")
@ApplicationScoped
public class TestResource {

    @Inject
    private TestService testService;

    @GET
    @Path("/check")
    @Produces(MediaType.APPLICATION_JSON)
    public String test() {

        return Json.createObjectBuilder()
            .add("status", testService.status())
            .build()
            .toString();

    }

}