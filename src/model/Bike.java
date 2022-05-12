package model;

import database.Database;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Model-Klasse Bike
 */
public class Bike
{
	private final StringProperty rahmennr = new SimpleStringProperty();
	private final StringProperty markeType = new SimpleStringProperty();
	private final StringProperty text = new SimpleStringProperty();
	private final ObjectProperty<BigDecimal> preis = new SimpleObjectProperty<>();

	/**
	 * Konstruktor aus Rahmennummer
	 *
	 * @param rahmennr Rahmennummer
	 */
	public Bike(String rahmennr)
	{
		setRahmennr(rahmennr);
	}

	/**
	 * Konstruktor mit allen Attributen.
	 *
	 * @param rahmennr  Rahmennummer
	 * @param markeType Marke und Type
	 * @param text      Kommentar
	 * @param preis     Preis
	 */
	public Bike(String rahmennr, String markeType, String text, BigDecimal preis)
	{
		setRahmennr(rahmennr);
		setMarkeType(markeType);
		setText(text);
		setPreis(preis);
	}

	/**
	 * Selektion eines Bike aus der Datenbank.
	 *
	 * @param rahmennr Rahmennummer
	 *
	 * @return Bike
	 *
	 * @throws SQLException
	 * @throws BikeException
	 */
	public static Bike select(String rahmennr) throws SQLException, BikeException
	{
		PreparedStatement pstmt = Database.getInstance().getPstmtSelect();
		pstmt.setString(1, rahmennr);
		ResultSet resultSet = pstmt.executeQuery();

		Bike bike;
		if (resultSet.next())
		{
			bike = new Bike(resultSet.getString("rahmennr"),
			                resultSet.getString("markeType"),
			                resultSet.getString("text"),
			                resultSet.getBigDecimal("preis")
			);
		}
		else
		{
			bike = new Bike(rahmennr);
		}

		return bike;
	}

	public String getRahmennr()
	{
		return rahmennr.get();
	}

	public void setRahmennr(String rahmennr)
	{
		this.rahmennr.set(rahmennr);
	}

	public StringProperty rahmennrProperty()
	{
		return rahmennr;
	}

	public String getMarkeType()
	{
		return markeType.get();
	}

	public void setMarkeType(String markeType)
	{
		this.markeType.set(markeType);
	}

	public StringProperty markeTypeProperty()
	{
		return markeType;
	}

	public String getText()
	{
		return text.get();
	}

	public void setText(String text)
	{
		this.text.set(text);
	}

	public StringProperty textProperty()
	{
		return text;
	}

	public BigDecimal getPreis()
	{
		return preis.get();
	}

	public void setPreis(BigDecimal preis)
	{
		this.preis.set(preis);
	}

	public ObjectProperty<BigDecimal> preisProperty()
	{
		return preis;
	}

	/**
	 * Defaulting und Überprüfung. Wird vor jedem Schreiben auf die Datenbank aufgerufen.
	 *
	 * @throws BikeException
	 */
	private void fillAndKill() throws BikeException
	{
		if (rahmennr.get() == null)
		{
			throw new BikeException("Rahmennummer muss angegeben werden!");
		}

		if (rahmennr.get().length() < 5)
		{
			throw new BikeException("Rahmennummer muss zumindest 5 Stellen haben!");
		}

		if (markeType.get() == null)
		{
			throw new BikeException("Marke und Type muss angegeben werden!");
		}

		if (markeType.get().length() < 3)
		{
			throw new BikeException("Marke und Type muss zumindest 3 Stellen haben!");
		}

		if (text.get() == null || text.get().length() == 0)
		{
			throw new BikeException("Text muss angegeben werden!");
		}

		if (preis.get() == null)
		{
			throw new BikeException("Preis muss angegeben werden!");
		}
	}

	/**
	 * Bike in Datenbank speichern.
	 * <p>
	 * Zunächst wird versucht das Bike einzufügen. Wenn dies wegen einer Primärschlüssel-Violation schief geht, wird
	 * versucht es abzuändern. (insert-default-update)
	 *
	 * @throws BikeException
	 * @throws SQLException
	 * @throws IOException
	 */
	public void save() throws BikeException, SQLException, IOException
	{
		fillAndKill();

		try
		{
			// Insert versuchen
			PreparedStatement pstmt = Database.getInstance().getPstmtInsert();
			pstmt.setString(1, getRahmennr());
			pstmt.setString(2, getMarkeType());
			pstmt.setString(3, getText());
			pstmt.setBigDecimal(4, getPreis());
			pstmt.execute();
		}
		catch (SQLException e)
		{
			// Primary Key Violation
			if (e.getSQLState().equals("23505"))
			{
				// Update versuchen
				PreparedStatement pstmt = Database.getInstance().getPstmtUpdate();
				pstmt.setString(1, getMarkeType());
				pstmt.setString(2, getText());
				pstmt.setBigDecimal(3, getPreis());
				pstmt.setString(4, getRahmennr());
				pstmt.execute();
			}
			else
			{
				throw e;
			}
		}
	}

	@Override
	public String toString()
	{
		return "Bike{" +
				"rahmennr=" + rahmennr +
				", markeType=" + markeType +
				", text=" + text +
				", preis=" + preis +
				'}';
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		Bike bike = (Bike) o;

		return getRahmennr().equals(bike.getRahmennr());
	}
}