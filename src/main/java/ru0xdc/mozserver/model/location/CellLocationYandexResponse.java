package ru0xdc.mozserver.model.location;

import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="location")
public class CellLocationYandexResponse {

	@XmlAttribute
	String source;

	public Coordinates coordinates;

	public static class Coordinates {
		@XmlAttribute
		public double latitude;
		@XmlAttribute
		public double longitude;
		@XmlAttribute
		public double nlatitude;
		@XmlAttribute
		public double nlongitude;
	}

}
