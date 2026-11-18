package ro.asilag;

import jakarta.servlet.DispatcherType;
import org.apache.commons.configuration2.Configuration;
import org.eclipse.jetty.ee11.servlet.DefaultServlet;
import org.eclipse.jetty.ee11.servlet.ServletContextHandler;
import org.eclipse.jetty.ee11.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.VirtualThreadPool;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.weld.environment.servlet.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.asilag.config.AppConfig;
import ro.asilag.config.AppProperties;
import ro.asilag.restapp.RestApplication;
import ro.asilag.servlet.RoutingFilter;

import java.lang.invoke.MethodHandles;
import java.util.EnumSet;

public class MainApp {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) throws Exception {

        // Prepare & read application.properties
        AppProperties props = new AppProperties();
        Configuration config = props.getAppPath();
        int serverPort = Integer.parseInt(props.server(config, "port"));
        String restRootApp = props.server(config, "rest");

        // Hold restRootApp to use later
        AppConfig.init(restRootApp);

        // Config Jetty Virtual Threads
        Server server;
        if (Boolean.parseBoolean(props.server(config, "virtual"))) {
            QueuedThreadPool threadPool = new QueuedThreadPool();
            VirtualThreadPool virtualPool = new VirtualThreadPool();
            threadPool.setVirtualThreadsExecutor(virtualPool);
            server = new Server(threadPool);
        } else {
            server = new Server();
        }
        server.setStopAtShutdown(true);
        server.setName("jetty");
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(serverPort);
        server.addConnector(connector);

        ServletContextHandler context =
            new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");

        // Weld CDI bootstrap
        context.addEventListener(new Listener());

        // Serve static file - index.html
        ResourceFactory resourceFactory = ResourceFactory.root();
        Resource webRoot = resourceFactory.newResource(
            MainApp.class.getResource("/static/").toURI()
        );
        context.setBaseResource(webRoot);

        context.addServlet(DefaultServlet.class, "/");

        // Not found
        context.addFilter(
                RoutingFilter.class,
                "/*",
                EnumSet.of(DispatcherType.REQUEST)
        );

        // RESTEasy servlet
        ServletHolder resteasy = new ServletHolder(
            "resteasy",
            new HttpServletDispatcher()
        );

        resteasy.setInitOrder(1);

        resteasy.setInitParameter(
            "jakarta.ws.rs.Application",
            RestApplication.class.getName()
        );

        context.setInitParameter(
            "resteasy.servlet.mapping.prefix",
            restRootApp
        );
        context.addServlet(resteasy, restRootApp + "/*");

        server.setHandler(context);

        try {
            server.start();
            log.info("Server started - http://localhost:{}", serverPort);
            server.join();
        } finally {
            server.stop();
            server.destroy();
        }

    }

}
