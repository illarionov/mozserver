package ru0xdc.mozserver.jdbi;

import com.google.common.base.Optional;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;


public interface CellLogDao {

    @SqlBatch("insert into cell_log "
            + "(cell_id, time, accuracy, altitude, altitude_accuracy, signal, ta, location) "
            + "values (:cell_id, :time, :accuracy, :altitude, :altitude_accuracy, :signal, :ta, ST_Transform(ST_GeomFromEWKT(:ewkt), 3857))"
    )
    void insertBatch(
            @Bind("time") java.sql.Timestamp datetime,
            @Bind("accuracy") Optional<Float> accuracy,
            @Bind("altitude") Optional<Float> altitude,
            @Bind("altitude_accuracy") Optional<Float> altitudeAccuracy,
            @Bind("ewkt") String locationEwkt,
            @Bind("cell_id") List<Long> cellIds,
            @Bind("ta") List<Integer> timingAdvances,
            @Bind("signal") List<Integer> signals
            );


    @SqlQuery("SELECT round(AVG(signal)) AS signal, ST_AsEWKB(ST_Transform(ST_snaptogrid(location, 10), 4326)) AS location"
            + "  FROM cell_log"
            + "  INNER JOIN cell ON cell.id=cell_log.cell_id"
            + "  WHERE "
            + "    (CAST(:mcc AS int) IS NULL OR mcc=:mcc)"
            + "       AND (CAST(:mnc AS int) IS NULL OR mnc=:mnc)"
            + "       AND (CAST(:lac AS int) IS NULL OR lac=:lac)"
            + "       AND (CAST(:cid AS int) IS NULL OR cid=:cid)"
            + "       AND (CAST(:psc AS int) IS NULL OR psc=:psc)"
            + "       AND (CAST(:radio AS text) IS NULL OR radio=:radio)"
            + "  GROUP BY ST_snaptogrid(location, 10)")
    @RegisterMapper(CoverageResponseRecord.Mapper.class)
    List<CoverageResponseRecord> getCoverage(
            @Bind("mcc") Optional<Integer> mcc,
            @Bind("mnc") Optional<Integer> mnc,
            @Bind("lac") Optional<Integer> lac,
            @Bind("cid") Optional<Integer> cid,
            @Bind("psc") Optional<Integer> psc,
            @Bind("radio") Optional<String> radio
    );

    /**
     * close with no args is used to close the connection
     */
    void close();
}
