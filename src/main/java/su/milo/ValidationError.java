package su.milo;

public class ValidationError extends Exception {
	public ValidationError(Iterable<String> errors) {
		super(errors.toString());
	}
}
