package Exeptions;

public class CarNotFoundException extends RuntimeException {
    /**
     * Constructor que acepta un mensaje detallado
     */
    public CarNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor opcional que acepta un mensaje y la causa (otra Throwable)
     */
    public CarNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}