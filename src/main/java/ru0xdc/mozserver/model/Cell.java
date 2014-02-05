package ru0xdc.mozserver.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


public class Cell {

    private final String mRadio;

    private final int mMcc;

    private final int mMnc;

    private final int mLac;

    private final int mCid;

    private final int mPsc;

    @JsonIgnore
    private final Logger mLog = LoggerFactory.getLogger(SubmitCell.class.getName());

    public Cell(String radio, int mcc, int mnc, int lac, int cid, int psc) {
        this.mRadio = radio;
        this.mMcc = mcc;
        this.mMnc = mnc;
        this.mLac = lac;
        this.mCid = cid;
        this.mPsc = psc;
    }

    @JsonCreator
    public Cell(Map<String, Object> props) {
        this((String) props.get("radio"),
                props.containsKey("mcc") ? (Integer) props.get("mcc") : SubmitCell.UNKNOWN_IDENT,
                props.containsKey("mnc") ? (Integer) props.get("mnc") : SubmitCell.UNKNOWN_IDENT,
                props.containsKey("lac") ? (Integer) props.get("lac") : SubmitCell.UNKNOWN_IDENT,
                props.containsKey("cid") ? (Integer) props.get("cid") : SubmitCell.UNKNOWN_IDENT,
                props.containsKey("psc") ? (Integer) props.get("psc") : SubmitCell.UNKNOWN_IDENT);
    }

    public int getMcc() {
        return mMcc;
    }

    public String getRadio() {
        return mRadio;
    }

    public int getMnc() {
        return mMnc;
    }

    public int getLac() {
        return mLac;
    }

    public int getCid() {
        return mCid;
    }

    public int getPsc() {
        return mPsc;
    }

    @Override
    public String toString() {
        Joiner.MapJoiner joiner = Joiner.on(", ").withKeyValueSeparator(": ");
        Map<String, String> parts = new HashMap<String, String>(10);

        if (!Strings.isNullOrEmpty(mRadio)) parts.put("radio", mRadio);
        if (mMcc != SubmitCell.UNKNOWN_IDENT) parts.put("mcc", String.valueOf(mMcc));
        if (mMnc != SubmitCell.UNKNOWN_IDENT) parts.put("mnc", String.valueOf(mMnc));
        if (mLac != SubmitCell.UNKNOWN_IDENT) parts.put("lac", String.valueOf(mLac));
        if (mCid != SubmitCell.UNKNOWN_IDENT) parts.put("cid", String.valueOf(mCid));
        if (mPsc != SubmitCell.UNKNOWN_IDENT) parts.put("psc", String.valueOf(mPsc));

        return "CELL[" + joiner.join(parts) + "]";
    }

}
