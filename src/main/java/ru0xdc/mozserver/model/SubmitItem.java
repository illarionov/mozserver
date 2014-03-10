package ru0xdc.mozserver.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.postgis.PGgeometryLW;
import org.postgis.Point;
import org.postgis.binary.BinaryWriter;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by alexey on 06.02.14.
 */
public class SubmitItem {

    public double lat = Double.NEGATIVE_INFINITY;

    public double lon = Double.NEGATIVE_INFINITY;

    public Date time;

    public float accuracy = -1;

    public float altitude = Float.NEGATIVE_INFINITY;

    public float altitude_accuracy = Float.NEGATIVE_INFINITY;

    public String radio;

    public List<SubmitCell> cell;

    public List<SubmitWifi> wifi;

    private static final BinaryWriter bw = new BinaryWriter();

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String locationAsEwkt() {
        return String.format(Locale.US, "SRID=4326;POINT(%f %f)", lon, lat);
    }

    /*
    public byte[] locationAsEwkb() {
        Point p = new Point(lon, lat);
        p.setSrid(4326);
        return bw.writeBinary(p);
    }
    */

}
