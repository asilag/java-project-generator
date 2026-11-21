package ro.asilag.config;

public class AppConfig {

    private static String restRootApp;

    public static void init(String value) {
        restRootApp = value;
    }

    public static String getRestRootApp() {
        return restRootApp;
    }

}
