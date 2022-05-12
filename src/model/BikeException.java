package model;

/**
 * Anwendungsspezifische Exceptions
 */
public class BikeException extends Exception
{
	/**
	 * Konstruktor.
	 *
	 * @param msg Fehlermeldung
	 */
	public BikeException(String msg)
	{
		super(msg);
	}
}