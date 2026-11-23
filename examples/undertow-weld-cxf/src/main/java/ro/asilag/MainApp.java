package ro.asilag;

import io.undertow.Undertow;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.handlers.DefaultServlet;
import jakarta.servlet.DispatcherType;
import org.apache.commons.configuration2.Configuration;
import org.apache.cxf.cdi.CXFCdiServlet;
import org.jboss.weld.environment.servlet.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.asilag.config.AppConfig;
import ro.asilag.config.AppProperties;
import ro.asilag.servlet.RoutingFilter;

import java.lang.invoke.MethodHandles;

public class MainApp {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) throws Exception {

        ClassLoader classLoader = MethodHandles.lookup().lookupClass().getClassLoader();

        // Prepare & read application.properties
        AppProperties props = new AppProperties();
        Configuration config = props.getAppPath();
        int serverPort = Integer.parseInt(props.server(config, "port"));
        String restRootApp = props.server(config, "rest");
        String serverListening = props.server(config, "listening");

        // Hold restRootApp to use later
        AppConfig.init(restRootApp);

        // Prepare Undertow
        DeploymentInfo deployment = Servlets.deployment()
            .setClassLoader(classLoader)
            .setContextPath("/")
            .setDeploymentName("app")
            .setResourceManager(new ClassPathResourceManager(classLoader, "static"))
            .addWelcomePages("index.html")
            // Weld listener
            .addListener(Servlets.listener(Listener.class))
            // CXF Servlet
            .addServlets(
                Servlets.servlet("cxf", CXFCdiServlet.class)
                    .addMapping(restRootApp + "/*")
                    .setLoadOnStartup(1)
            )
            .addServlets(
                // Serves / using static/index.html from the JAR
                Servlets.servlet("default", DefaultServlet.class)
                    .addMapping("/")
            )
            .addFilters(
                Servlets.filter("routerFilter", RoutingFilter.class)
            )
            .addFilterUrlMapping(
                "routerFilter", "/*", DispatcherType.REQUEST
            );

        DeploymentManager manager = Servlets.defaultContainer().addDeployment(deployment);
        manager.deploy();

        Undertow server = Undertow.builder()
            .addHttpListener(serverPort, serverListening)
            .setHandler(manager.start())
            .build();

        try {
            server.start();
            log.info("Server started - http://localhost:{}", serverPort);
            Thread.currentThread().join();
        } finally {
            server.stop();
        }

    }

}
