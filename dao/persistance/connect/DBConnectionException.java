package persistance.connect;

@SuppressWarnings("serial")
public class DBConnectionException extends RuntimeException {
	protected DBConnectionException(String message, Throwable cause) {
		super(message, cause);
	}
}
