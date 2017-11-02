package translator.exception;

public class LanguageDetectionException extends RuntimeException {
    public LanguageDetectionException(String message) {
        super(message);
    }

    public LanguageDetectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
