package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Arrays;

public enum Farbe
{
	Rot, Gruen, Gelb, Blau;

	public static ObservableList<Farbe> valuesAsObservableList()
	{
		return FXCollections.observableList(Arrays.asList(values()));
	}
}