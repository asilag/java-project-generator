package ro.asilag.config;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.invoke.MethodHandles;

public class AppProperties {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final String restEasyApp = System.getenv("REST_APP");
    private final Configurations configs = new Configurations();
    private Configuration config;

    public Configuration getAppPath() {

        try {
            if (restEasyApp != null) {
                config = configs.properties(new File(restEasyApp + "/application.properties"));
            } else {
                config = configs.properties(new File("src/main/resources/application.properties"));
            }
            return config;
        } catch (Exception ex) {
            log.error("Configuration Exception when try to read config file - {}", ex.getMessage());
            return null;
        }

    }

    public String datasource(Configuration config, String param) {

        return switch(param) {
            case "url" -> config.getString("datasource.url");
            case "user" -> config.getString("datasource.user");
            case "password" -> config.getString("datasource.password");
            default -> throw new IllegalStateException("Unexpected value: " + param);
        };

    }

    public Integer hikari(Configuration config, String param) {

        return switch(param) {
            case "maxPoolSize" -> config.getInt("hikari.setMaximumPoolSize");
            case "minIdle" -> config.getInt("hikari.setMinimumIdle");
            case "idleTimeout" -> config.getInt("hikari.setIdleTimeout");
            default -> throw new IllegalStateException("Unexpected value: " + param);
        };

    }

    public String batis(Configuration config, String param) {

        return switch(param) {
            case "env" -> config.getString("batis.environment");
            default -> throw new IllegalStateException("Unexpected value: " + param);
        };

    }


    public String server(Configuration config, String param) {

        return switch(param) {
            case "port" -> config.getString("server.port");
            case "virtual" -> config.getString("server.virtual.threads");
            case "listening" -> config.getString("server.listening");
            case "context" -> config.getString("server.context");
            case "rest" -> config.getString("rest.root.app");
            default -> throw new IllegalStateException("Unexpected value: " + param);
        };

    }

    public String app(Configuration config, String param) {

        return switch(param) {
            case "root" -> config.getString("rest.root.app");
            default -> throw new IllegalStateException("Unexpected value: " + param);
        };

    }

}
