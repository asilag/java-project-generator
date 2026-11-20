package ro.asilag.restapp;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import ro.asilag.exception.NotFoundExceptionMapper;
import ro.asilag.resource.TestResource;

import java.util.Set;

@ApplicationPath("/")
public class RestApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(
            TestResource.class,
            NotFoundExceptionMapper.class
        );
    }
}
