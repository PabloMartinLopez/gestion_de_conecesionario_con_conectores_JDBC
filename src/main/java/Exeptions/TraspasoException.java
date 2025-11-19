package Exeptions;

public class TraspasoException extends RuntimeException {
    /**
     * Constructor que acepta un mensaje detallado
     */
    public TraspasoException(String message) {
        super(message);
    }

    /**
     * Constructor opcional que acepta un mensaje y la causa (otra Throwable)
     */
    public TraspasoException(String message, Throwable cause) {
        super(message, cause);
    }
}