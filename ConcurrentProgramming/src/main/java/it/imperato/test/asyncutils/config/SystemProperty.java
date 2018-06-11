package it.imperato.test.asyncutils.config;

public enum SystemProperty {

    numWorkers("pputils.numWorkers", "int", "Number of worker threads to create",
            Constants.AVAILABLE_PROCESSORS_STR),

    showWarning("pputils.showWarning", "bool", "Show warning/debug messages",
            "false"),

    showRuntimeStats("pputils.showRuntimeStats", "bool",
            "Show executor service stats", "false");


    public static void setSystemProperty(final SystemProperty hjSystemProperty,
            final Object value) {
        System.setProperty(hjSystemProperty.propertyKey(),
                String.valueOf(value));
    }

    private final String propertyKey;

    private final String propertyType;

    private final String propertyDescription;

    private final String defaultValue;

    SystemProperty(final String pPropertyKey,
            final String pPropertyType,
            final String pPropertyDescription,
            final String pDefaultValue) {
        this.propertyKey = pPropertyKey;
        this.propertyType = pPropertyType;
        this.propertyDescription = pPropertyDescription;
        this.defaultValue = pDefaultValue;
    }

    private String propertyKey() {
        return propertyKey;
    }

    public String getPropertyValue() {
        final String systemProperty = System.getProperty(propertyKey);
        if (systemProperty == null || systemProperty.trim().isEmpty()) {
            return defaultValue;
        } else {
            return systemProperty;
        }
    }

    public void set(final Object value) {
        setProperty(value);
    }

    public void setProperty(final Object value) {
        setSystemProperty(this, value);
    }

    @Override
    public String toString() {
        final String configuredValue = System.getProperty(propertyKey);
        final String displayValue;
        if (configuredValue != null) {
            displayValue = configuredValue;
        } else {
            displayValue = "";
        }

        return String.format("%20s : type=%-6s, default=%-6s, current=%-6s, "
                + "description=%s", propertyKey, propertyType, defaultValue,
                displayValue, propertyDescription);
    }

    private static class Constants {

        private static int availableProcessors =
            Runtime.getRuntime().availableProcessors();

        private static final String AVAILABLE_PROCESSORS_STR =
            Integer.toString(availableProcessors);

        private static final String MAX_THREADS_DEFAULT =
            Integer.toString(Math.min(128, 10 * availableProcessors));
    }
}
