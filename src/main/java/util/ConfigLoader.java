package util;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Clase de utilidad para cargar el fichero de configuración.
 */
public class ConfigLoader {
    private static final String CONFIG_FILE = "src/main/resources/config.properties";
    private final Properties properties;

    /**
     * Carga las propiedades desde el fichero de configuración.
     */
    public ConfigLoader() {
        properties = new Properties();
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            properties.load(reader);
        } catch (IOException e) {
            System.err.println("Error: No se pudo cargar el fichero de configuración 'config.properties'.");
            System.err.println("Asegúrese de que el fichero existe en la ruta correcta.");
            // En una aplicación real, podríamos querer terminar aquí o usar valores por defecto.
            // System.exit(1);
        }
    }

    /**
     * Obtiene una propiedad del fichero de configuración.
     * @param key La clave de la propiedad.
     * @return El valor de la propiedad.
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}