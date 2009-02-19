package persistance;

@SuppressWarnings("serial")
public class PersistanceException extends Exception {
	protected PersistanceException(String message, Throwable cause) {
		super(message, cause);
	};
}
