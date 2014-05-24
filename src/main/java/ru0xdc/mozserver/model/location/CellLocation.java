package ru0xdc.mozserver.model.location;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru0xdc.mozserver.model.SubmitCell;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CellLocation {

	private final double mLatitude;

	private final double mLongitude;

	private final Float mAccuracy;

	@JsonIgnore
	private final Logger mLog = LoggerFactory.getLogger(SubmitCell.class.getName());

	public CellLocation(double lat, double lon) {
		this(lat, lon, null);
	}

	public CellLocation(double lat, double lon, Float accuarcy) {
		mLatitude = lat;
		mLongitude = lon;
		mAccuracy = accuarcy;
	}

	@JsonCreator
	public CellLocation(Map<String, Object> props) {
		this((Double)props.get("lat"),
				(Double)props.get("lon"));
	}

	@JsonProperty("lat")
	public double getLat() {
		return mLatitude;
	}

	@JsonProperty("lon")
	public double getLon() {
		return mLongitude;
	}

	@JsonProperty("accuracy")
	public Float getAccuracy() {
		return mAccuracy;
	}

	@Override
	public String toString() {
		return String.valueOf(mLatitude) + ", " + mLongitude + "";
	}

	public static class Error {
		public String error;

		public Error(String error) {
			this.error = error;
		}
	}

}
