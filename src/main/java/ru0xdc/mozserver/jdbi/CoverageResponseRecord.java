package ru0xdc.mozserver.jdbi;

import org.postgis.Point;
import org.postgis.binary.BinaryParser;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import javax.annotation.concurrent.Immutable;
import java.sql.ResultSet;
import java.sql.SQLException;

@Immutable
public class CoverageResponseRecord {

    public final double lat;

    public final double lon;

    public final double signal;

    public CoverageResponseRecord(double lat, double lon, double signal) {
        this.lat = lat;
        this.lon = lon;
        this.signal = signal;
    }

    public double[] vec3d() {
        return new double[] {lat, lon, (double)signal};
    }

    public static class Mapper implements ResultSetMapper<CoverageResponseRecord>
    {
        private final BinaryParser mBinaryParser = new BinaryParser();

        public CoverageResponseRecord map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
            Point p = (Point)mBinaryParser.parse(r.getBytes("location"));
            return new CoverageResponseRecord(
                    Math.round(p.getY() * 1e6) / 1e6,
                    Math.round(p.getX() * 1e6) / 1e6,
                    r.getDouble("signal"));
        }
    }
}
