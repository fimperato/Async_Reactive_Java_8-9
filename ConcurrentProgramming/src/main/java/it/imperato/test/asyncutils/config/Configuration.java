package it.imperato.test.asyncutils.config;

import java.io.InputStream;
import java.util.Properties;

public final class Configuration {

    public static boolean showWarning;

    public static boolean SHOW_RUNTIME_STATS;

    public static String BUILD_INFO;

    static {
        initializeFlags();
    }

    private Configuration() {
        throw new IllegalStateException(
                "Emptyton, no instance creation expected!");
    }

    private static void initializeFlags() {
        showWarning = readBooleanProperty(SystemProperty.showWarning);
        if (showWarning) {
            printConfiguredOptions();
        }
        SHOW_RUNTIME_STATS =
            readBooleanProperty(SystemProperty.showRuntimeStats);

        String buildInfo;
        try {
            final Properties buildProperties = new Properties();
            final InputStream buildPropsStream =
                Configuration.class.getResourceAsStream("/build.properties");
            buildProperties.load(buildPropsStream);
            buildInfo = buildProperties.getProperty("version") + ' '
                + buildProperties.getProperty("buildTimestamp");
        } catch (final Exception e) {
            buildInfo = "";
        }
        BUILD_INFO = buildInfo;
    }

    public static boolean readBooleanProperty(
            final SystemProperty systemProperty) {
        final Lambda<String, Boolean> converter =
            new Lambda<String, Boolean>() {
                @Override
                public Boolean apply(final String s) {
                    return Boolean.parseBoolean(s);
                }
            };
        return extractProperty(systemProperty, converter);
    }

    public static void printConfiguredOptions() {
        System.err.println("Interpreter flags: ");
        for (final SystemProperty systemProperty : SystemProperty.values()) {
            System.err.println(" " + systemProperty);
        }
    }

    public static int readIntProperty(final SystemProperty systemProperty) {
        final Lambda<String, Integer> converter =
            new Lambda<String, Integer>() {
                @Override
                public Integer apply(final String s) {
                    return Integer.parseInt(s);
                }
            };
        return extractProperty(systemProperty, converter);
    }

    private static <T> T extractProperty(
        final SystemProperty propertyName,
        final Lambda<String, T> converter) {
        try {
            final String valueStr = propertyName.getPropertyValue();
            return converter.apply(valueStr);
        } catch (final Exception ex) {
            // value not configured properly, use default value
            throw new IllegalStateException("Error while converting property: "
                    + propertyName);
        }
    }

    public static String readStringProperty(
            final SystemProperty systemProperty) {
        final Lambda<String, String> converter = new Lambda<String, String>() {
            @Override
            public String apply(final String s) {
                return s;
            }
        };
        return extractProperty(systemProperty, converter);
    }

    private interface Lambda<P, R> {
        R apply(P p);
    }
}

