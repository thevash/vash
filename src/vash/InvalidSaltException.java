package vash;

public class InvalidSaltException extends IllegalArgumentException {
	static final long serialVersionUID = 0;
	public InvalidSaltException(String msg) {
		super(msg);
	}
}
