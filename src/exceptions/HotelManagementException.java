package exceptions;

/**
 * Custom exception class for hotel management system errors.
 */
public class HotelManagementException extends Exception {
    
    /**
     * Constructor with error message.
     * @param message Error description
     */
    public HotelManagementException(String message) {
        super(message);
    }
    
    /**
     * Constructor with error message and cause.
     * @param message Error description
     * @param cause Underlying exception
     */
    public HotelManagementException(String message, Throwable cause) {
        super(message, cause);
    }
}