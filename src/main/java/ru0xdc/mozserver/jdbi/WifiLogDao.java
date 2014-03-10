package ru0xdc.mozserver.jdbi;

import com.google.common.base.Optional;
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
            + "(bssid, ssid, capabilities, time, frequency, signal, accuracy, altitude, location) "
            + "values (macaddr(:bssid), :ssid, :capabilities, :time, :frequency, :signal, :accuracy, :altitude, ST_Transform(ST_GeomFromEWKT(:ewkt), 3857))"
    )
    void insertBatch(
            @Bind("time") java.sql.Timestamp datetime,
            @Bind("ewkt") String locationEwkt,
            @Bind("accuracy") Optional<Float> accuracy,
            @Bind("altitude") Optional<Float> altitude,
            @Bind("bssid") List<String> bssids,
            @Bind("ssid") List<String> ssids,
            @Bind("capabilities") List<String> capabilities,
            @Bind("frequency") List<Integer> frequencies,
            @Bind("signal") List<Integer> signals);


    @SqlUpdate("insert into wifi_log "
            + "(bssid, ssid, capabilities, time, frequency, signal, accuracy, altitude, location) "
            + "values (macaddr(:bssid), :ssid, :capabilities, :time, :frequency, :signal, :accuracy, :altitude, ST_Transform(ST_GeomFromEWKT(:ewkt), 3857))"
    )
    void insert(
            @Bind("time") java.sql.Timestamp datetime,
            @Bind("ewkt") String locationEwkt,
            @Bind("accuracy") Float accuracy,
            @Bind("altitude") Float altitude,
            @Bind("bssid") String bssid,
            @Bind("ssid") String ssid,
            @Bind("capabilities") String capabilities,
            @Bind("frequency") Integer frequency,
            @Bind("signal") int signal);

    /**
     * close with no args is used to close the connection
     */
    void close();
}
