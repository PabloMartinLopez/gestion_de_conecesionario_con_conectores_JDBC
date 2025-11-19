package Exeptions;

public class PropietarioNotFoundException extends RuntimeException {
    /**
     * Constructor que acepta un mensaje detallado
     */
    public PropietarioNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor opcional que acepta un mensaje y la causa (otra Throwable)
     */
    public PropietarioNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}