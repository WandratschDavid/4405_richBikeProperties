package database;

import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;

/**
 * Singleton zum Zugriff auf eine JDBC-Datenbank.
 * Verbindungsdetails befinden sich in der Datei dbconnect.properties in den Props
 * <ul>
 *   <li>driver</li>
 *   <li>url</li>
 *   <li>username</li>
 *   <li>password</li>
 * </ul>
 */
public class Database
{
	private static Database instance;
	private Connection connection;
	private Statement statement;
	private String driver;
	private String url;
	private String username;
	private String password;
	private PreparedStatement pstmtSelect;
	private PreparedStatement pstmtInsert;
	private PreparedStatement pstmtUpdate;

	/**
	 * Privater Konstruktor, da es sich um ein Singleton handelt und die einzige Instanz nur von der Klasse selbst
	 * erstellt und verwaltet wird.
	 */
	private Database()
	{
		// DB-Properties laden
		try (FileInputStream in = new FileInputStream("dbconnect.properties");)
		{
			Properties prop = new Properties();
			prop.load(in);
			driver = prop.getProperty("driver");
			url = prop.getProperty("url");
			username = prop.getProperty("username");
			password = prop.getProperty("password");

			// Alles da?
			if (driver == null || url == null || username == null || password == null)
			{
				throw new Exception("Fehler! Property File muss driver, url, username, password enthalten!");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}

		// Verbindung erstellen
		try
		{
			connection = DriverManager.getConnection(url, username, password);
			statement = connection.createStatement();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(2);
		}
	}

	/**
	 * Liefert die einzige Instanz.
	 * @return Instanz
	 */
	public static Database getInstance()
	{
		return instance;
	}

	/**
	 * Öffnen der Datenbank.
	 * @throws SQLException
	 */
	public static void open() throws SQLException
	{
		open(true);
	}

	/**
	 * Öffnen der Datenbank, wahlweise mit und ohne Erstellung der Prepared Statements - abhängig vom Schalter
	 * createPstms.
	 * @param createPstms Schalter
	 * @throws SQLException
	 */
	public static void open(boolean createPstms) throws SQLException
	{
		instance = new Database();

		if (createPstms)
		{
			instance.createPstmtSelect();
			instance.createPstmtInsert();
			instance.createPstmtUpdate();
		}
	}

	public static void close()
	{
		try
		{
			getInstance().connection.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.exit(3);
		}
	}

	/**
	 * Getter für Connection.
	 * @return Connection.
	 */
	public Connection getConnection()
	{
		return connection;
	}

	/**
	 * Getter für dynamisches Datenbank-Statement.
	 * @return dynamishes DB-Statement.
	 */
	public Statement getStatement()
	{
		return statement;
	}

	/**
	 * Getter für statisches Select-Datenbank-Statement.
	 * @return statisches Select-Datenbank-Statement
	 */
	public PreparedStatement getPstmtSelect()
	{
		return pstmtSelect;
	}

	/**
	 * Getter für statisches Insert-Datenbank-Statement.
	 * @return statisches Insert-Datenbank-Statement
	 */
	public PreparedStatement getPstmtInsert()
	{
		return pstmtInsert;
	}

	/**
	 * Getter für statisches Update-Datenbank-Statement.
	 * @return statisches Update-Datenbank-Statement
	 */
	public PreparedStatement getPstmtUpdate()
	{
		return pstmtUpdate;
	}

	private void createPstmtSelect() throws SQLException
	{
		String sql =
				" select rahmennr" +
						",   markeType" +
						",   text" +
						",   preis" +
						" from bike " +
						" where rahmennr = ?";

		pstmtSelect = connection.prepareStatement(sql);
	}

	private void createPstmtInsert() throws SQLException
	{
		String sql
				= "  insert "
				+ " into Bike (rahmennr "
				+ "           ,markeType "
				+ "           ,text "
				+ "           ,preis "
				+ "           ) "
				+ " values (?,?,?,?)";

		pstmtInsert = connection.prepareStatement(sql);
	}

	private void createPstmtUpdate() throws SQLException
	{
		String sql = "update Bike " +
				" set markeType  = ? " +
				",    text       = ? " +
				",    preis      = ? " +
				" where rahmennr = ?";

		pstmtUpdate = connection.prepareStatement(sql);
	}
}