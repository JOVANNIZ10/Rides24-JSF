package exceptions;

public class ErabiltzaileaDagoenekoErregistratutaException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ErabiltzaileaDagoenekoErregistratutaException() {
		super("The user is already registered.");
	}
	public ErabiltzaileaDagoenekoErregistratutaException(String message) {
		super(message);
	}
}