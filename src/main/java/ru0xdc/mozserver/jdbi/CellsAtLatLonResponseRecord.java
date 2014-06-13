package ru0xdc.mozserver.jdbi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Optional;
import org.postgis.Point;
import org.postgis.binary.BinaryParser;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import javax.annotation.concurrent.Immutable;
import java.sql.ResultSet;
import java.sql.SQLException;

@Immutable
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CellsAtLatLonResponseRecord {

	private final int mMcc;

	private final int mMnc;

	private final int mLac;

	private final int mCid;

	private final int mPsc;

	private final String mRadio;

	private final float mSignalDbm;

	public CellsAtLatLonResponseRecord(int mcc, int mnc, int lac, int cid, int psc, String radio,
			float signalDbm) {
		if (radio == null) radio = "gsm";
		mMcc = mcc;
		mMnc = mnc;
		mLac = lac;
		mCid = cid;
		mPsc = psc;
		mRadio = radio;
		mSignalDbm = signalDbm;
	}

	public Optional<Integer> getMcc() {
		return Optional.fromNullable(mMcc > 0 ? mMcc : null);
	}

	public Optional<Integer> getMnc() {
		return Optional.fromNullable(mMnc > 0 ? mMnc : null);
	}

	public Optional<Integer> getLac() {
		return Optional.fromNullable(mLac > 0 ? mLac : null);
	}

	public Optional<Integer> getCid() {
		return Optional.fromNullable(mCid > 0 ? mCid : null);
	}

	public Optional<Integer> getPsc() {
		return Optional.fromNullable(mPsc > 0 ? mPsc : null);
	}

	public float getSignal() {
		return mSignalDbm;
	}

	public String getRadio() {
		return mRadio;
	}


	public static class Mapper implements ResultSetMapper<CellsAtLatLonResponseRecord>
	{
		public CellsAtLatLonResponseRecord map(int index, ResultSet r, StatementContext ctx) throws SQLException
		{
			return new CellsAtLatLonResponseRecord(
					r.getInt("mcc"),
					r.getInt("mnc"),
					r.getInt("lac"),
					r.getInt("cid"),
					r.getInt("psc"),
					r.getString("radio"),
					r.getFloat("signal")
			);
		}
	}
}
