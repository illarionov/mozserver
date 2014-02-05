package ru0xdc.mozserver.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.Immutable;
import java.util.HashMap;
import java.util.Map;


@Immutable
public class SubmitCell {

    public static final String CELL_RADIO_GSM = "gsm";
    public static final String CELL_RADIO_UMTS = "umts";
    public static final String CELL_RADIO_CDMA = "cdma";
    public static final String CELL_RADIO_LTE = "lte";

    public static final int UNKNOWN_IDENT = -1;

    public static final int UNKNOWN_SIGNAL = Integer.MIN_VALUE;

    private final Logger mLog = LoggerFactory.getLogger(SubmitCell.class.getName());

    private final String radio;

    private final int mcc;

    private final int mnc;

    private final int lac;

    private final int cid;

    private final int psc;

    private final int signal;

    private final int asu;

    private final int ta;

    public SubmitCell(String radio, int mcc, int mnc, int lac, int cid, int psc,
                      int signal, int asu, int ta) {
        this.radio = radio;
        this.mcc = mcc;
        this.mnc = mnc;
        this.lac = lac;
        this.cid = cid;
        this.psc = psc;
        this.signal = signal;
        this.asu = asu;
        this.ta = ta;
    }

    @JsonCreator
    public SubmitCell(Map<String, Object> props) {
        this(
          (String)props.get("radio"),
          props.containsKey("mcc") ? (Integer)props.get("mcc") :  UNKNOWN_IDENT,
          props.containsKey("mnc") ? (Integer)props.get("mnc") :  UNKNOWN_IDENT,
          props.containsKey("lac") ? (Integer)props.get("lac") :  UNKNOWN_IDENT,
          props.containsKey("cid") ? (Integer)props.get("cid") :  UNKNOWN_IDENT,
          props.containsKey("psc") ? (Integer)props.get("psc") :  UNKNOWN_IDENT,
          props.containsKey("signal") ? (Integer)props.get("signal") :  UNKNOWN_SIGNAL,
          props.containsKey("asu") ? (Integer)props.get("asu") :  UNKNOWN_SIGNAL,
          props.containsKey("ta") ? (Integer)props.get("ta") :  UNKNOWN_SIGNAL
        );
    }

    public int getSignalStrengthDbm() {
        if (signal != UNKNOWN_SIGNAL) return signal;
        if (asu == UNKNOWN_SIGNAL) {
            return UNKNOWN_SIGNAL;
        }

        if (CELL_RADIO_GSM.equals(radio)) {
            return asu == 99 ? UNKNOWN_SIGNAL : (2 * asu) - 113;
        } else if (CELL_RADIO_UMTS.equals(radio)) {
            return asu == 255 ? UNKNOWN_SIGNAL : asu - 116;
        } else if (CELL_RADIO_LTE.equals(radio)) {
            return asu - 140;
        } else if (CELL_RADIO_CDMA.equals(radio)) {
            // XXX
            if (asu <= 1) return -100;
            else if (asu <= 2) return -95;
            else if (asu <= 4) return -90;
            else if (asu <= 8) return -82;
            else if (asu <= 16) return -75;
            else return -60;
        } else {
            mLog.error("Unknown radio: " + radio);
            // XXX
            return asu;
        }
    }

    public int getMcc() {
        return mcc;
    }

    public String getRadio() {
        return radio;
    }

    public int getMnc() {
        return mnc;
    }

    public int getLac() {
        return lac;
    }

    public int getCid() {
        return cid;
    }

    public int getPsc() {
        return psc;
    }

    public int getSignal() {
        return signal;
    }

    public int getTa() {
        return ta;
    }

    public int getAsu() {
        return asu;
    }

    public boolean hasEnoughFields() {
        return (mcc != UNKNOWN_IDENT)
                && (mnc != UNKNOWN_IDENT)
                && (getSignalStrengthDbm() != UNKNOWN_SIGNAL);
    }

    @Override
    public String toString() {
        Joiner.MapJoiner joiner = Joiner.on(", ").withKeyValueSeparator(": ");
        Map<String, String> parts = new HashMap<String, String>(10);

        if (!Strings.isNullOrEmpty(radio)) parts.put("radio", radio);
        if (mcc != UNKNOWN_IDENT) parts.put("mcc", String.valueOf(mcc));
        if (mnc != UNKNOWN_IDENT) parts.put("mnc", String.valueOf(mnc));
        if (lac != UNKNOWN_IDENT) parts.put("lac", String.valueOf(lac));
        if (cid != UNKNOWN_IDENT) parts.put("cid", String.valueOf(cid));
        if (psc != UNKNOWN_IDENT) parts.put("psc", String.valueOf(psc));

        if (signal != UNKNOWN_SIGNAL) parts.put("signal", String.valueOf(signal));
        if (asu != UNKNOWN_SIGNAL) parts.put("asu", String.valueOf(asu));
        if (ta != UNKNOWN_SIGNAL) parts.put("ta", String.valueOf(ta));

        if (getSignalStrengthDbm() != UNKNOWN_SIGNAL) parts.put("getSignalStrengthDbm", String.valueOf(getSignalStrengthDbm()));

        return "CELL[" + joiner.join(parts) + "]";
    }

}
