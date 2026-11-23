package ro.asilag.exception;

import jakarta.json.Json;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(NotFoundException exception) {

        log.warn("{} - {}", exception.getMessage(), uriInfo.getRequestUri().getPath());

        String errResponse = Json.createObjectBuilder()
            .add("status", 404)
            .add("path", uriInfo.getRequestUri().getPath())
            .add("message", "resource not found")
            .build()
            .toString()
        ;

        return Response.status(Response.Status.NOT_FOUND)
            .entity(errResponse)
            .type(MediaType.APPLICATION_JSON)
            .build()
        ;

    }

}
