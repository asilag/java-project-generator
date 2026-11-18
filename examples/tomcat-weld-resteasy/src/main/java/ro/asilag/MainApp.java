package ro.asilag;

import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.configuration2.Configuration;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.weld.environment.servlet.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.asilag.config.AppConfig;
import ro.asilag.config.AppProperties;
import ro.asilag.servlet.IndexServlet;
import ro.asilag.servlet.NotFoundServlet;
import ro.asilag.servlet.RoutingFilter;

import java.lang.invoke.MethodHandles;

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

        // Prepare Tomcat server & context
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(serverPort);
        Connector connector = tomcat.getConnector();
        String virtualThreads = props.server(config, "virtual");
        connector.setProperty("useVirtualThreads", virtualThreads);
        Context context = tomcat.addContext("", System.getProperty("java.io.tmpdir"));

        // Tomcat - Filter registration
        FilterDef filterDef = new FilterDef();
        filterDef.setFilterName("routing");
        filterDef.setFilter(new RoutingFilter());
        context.addFilterDef(filterDef);

        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName("routing");
        filterMap.addURLPattern("/*");
        context.addFilterMapBefore(filterMap);

        // Servlet implementation for /
        tomcat.addServlet("", "default", IndexServlet.class.getName());
        context.addServletMapping("/", "default");

        // Resteasy section
        context.addApplicationListener(Listener.class.getName());
        context.addParameter(
                "resteasy.servlet.mapping.prefix",
                restRootApp
        );
        context.addApplicationListener(
                "org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap"
        );
        Wrapper resteasyServlet = Tomcat.addServlet(
                context,
                "resteasy",
                new HttpServletDispatcher()
        );
        resteasyServlet.addInitParameter(
                "jakarta.ws.rs.Application",
                "ro.asilag.restapp.RestApplication"
        );
        resteasyServlet.setLoadOnStartup(1);
        context.addServletMapping(
                restRootApp + "/*",
                "resteasy"
        );

        // Tomcat implementation for not-found
        Tomcat.addServlet(context, "notFound", new NotFoundServlet());
        context.addServletMapping("/_errors/404", "notFound");

        ErrorPage ep = new ErrorPage();
        ep.setErrorCode(404);
        ep.setLocation("/_errors/404");
        context.addErrorPage(ep);

        try {
            tomcat.start();
            log.info("Server started - http://localhost:{}", serverPort);
            tomcat.getServer().await();
        } finally {
            tomcat.stop();
            tomcat.destroy();
        }

    }

}
