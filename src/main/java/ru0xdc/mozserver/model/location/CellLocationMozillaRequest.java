package ru0xdc.mozserver.model.location;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CellLocationMozillaRequest {

	public String radio;

	public List<Cell> cell;

	public CellLocationMozillaRequest() {
	}

	public CellLocationMozillaRequest(int mcc, int mnc, int lac, int cid, Integer psc, String radio) {
		Cell c = new Cell();
		if (radio != null) c.radio = radio;
		c.mcc = mcc;
		c.mnc = mnc;
		c.lac = lac;
		c.cid = cid;
		if (psc != null) c.psc = psc;
		cell = Collections.singletonList(c);
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Cell {
		public String radio;
		public int mcc;
		public int mnc;
		public int lac;
		public int cid;
		public Integer psc;
		public Integer asu;
	}



}
