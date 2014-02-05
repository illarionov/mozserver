package ru0xdc.mozserver.jdbi;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.util.Date;
import java.util.List;

/**
 * Created by alexey on 08.02.14.
 */
public interface WifiLogDao {

    @SqlBatch("insert into wifi_log "
            + "(bssid, time, frequency, signal, location) "
            + "values (macaddr(:bssid), :time, :frequency, :signal, ST_Transform(ST_GeomFromEWKT(:ewkt), 3857))"
    )
    void insertBatch(
            @Bind("time") java.sql.Timestamp datetime,
            @Bind("ewkt") String locationEwkt,
            @Bind("bssid") List<String> bssids,
            @Bind("frequency") List<Integer> frequencies,
            @Bind("signal") List<Integer> signals);


    @SqlUpdate("insert into wifi_log "
            + "(bssid, time, frequency, signal, location) "
            + "values (macaddr(:bssid), :time, :frequency, :signal, ST_Transform(ST_GeomFromEWKT(:ewkt), 3857))"
    )
    void insert(
            @Bind("time") java.sql.Timestamp datetime,
            @Bind("ewkt") String locationEwkt,
            @Bind("bssid") String bssid,
            @Bind("frequency") Integer frequency,
            @Bind("signal") int signal);

    /**
     * close with no args is used to close the connection
     */
    void close();
}
