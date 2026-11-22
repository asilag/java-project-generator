package ro.asilag.service;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TestService {

    public String status() {
        return "up";
    }

}
