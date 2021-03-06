package ru0xdc.mozserver.jdbi;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.util.StringMapper;

import java.util.List;


public interface CellLogDao {

	static final int COVERAGE_HULL_BUFFER = 200;
	static final int CELLS_AT_LAT_LON_RADIUS = 30;

    @SqlBatch("insert into cell_log "
            + "(cell_id, network_type, time, accuracy, altitude, altitude_accuracy, signal, ta, location) "
            + "values (:cell_id, :network_type, :time, :accuracy, :altitude, :altitude_accuracy, :signal, :ta, ST_Transform(ST_GeomFromEWKT(:ewkt), 3857))"
    )
    void insertBatch(
            @Bind("time") java.sql.Timestamp datetime,
            @Bind("accuracy") Optional<Float> accuracy,
            @Bind("altitude") Optional<Float> altitude,
            @Bind("altitude_accuracy") Optional<Float> altitudeAccuracy,
            @Bind("ewkt") String locationEwkt,
            @Bind("cell_id") List<Long> cellIds,
            @Bind("network_type") List<String> networkTypes,
            @Bind("ta") List<Integer> timingAdvances,
            @Bind("signal") List<Integer> signals
            );

    @SqlQuery("SELECT MAX(signal) AS signal, ST_AsEWKB(ST_Transform(location, 4326)) AS location"
            + "  FROM cell_coverage"
            + "  INNER JOIN cell ON cell.id=cell_coverage.cell_id"
            + "  WHERE "
            + "    (CAST(:mcc AS int) IS NULL OR mcc=:mcc)"
            + "       AND (CAST(:mnc AS int) IS NULL OR mnc=:mnc)"
            + "       AND (CAST(:lac AS int) IS NULL OR lac=:lac)"
            + "       AND (CAST(:cid AS int) IS NULL OR cid=:cid)"
            + "       AND (CAST(:psc AS int) IS NULL OR psc=:psc)"
			+ "       AND (CAST(:rnc AS int) IS NULL OR (cid >> 16) & CAST(x'ffff' AS int) = :rnc)"
            + "       AND (CAST(:radio AS text) IS NULL OR radio=:radio)"
            + "  GROUP BY location")
    @RegisterMapper(CoverageResponseRecord.Mapper.class)
    List<CoverageResponseRecord> getCoverage(
            @Bind("mcc") Optional<Integer> mcc,
            @Bind("mnc") Optional<Integer> mnc,
            @Bind("lac") Optional<Integer> lac,
            @Bind("cid") Optional<Integer> cid,
            @Bind("psc") Optional<Integer> psc,
			@Bind("rnc") Optional<Integer> rnc,
            @Bind("radio") Optional<String> radio
    );

	@SqlQuery("SELECT ST_AsGeoJSON( ST_Transform(ST_Buffer(ST_ConcaveHull(ST_Collect(location), 1, false), " + COVERAGE_HULL_BUFFER + "), 4326) )"
			+ "  FROM cell_coverage"
			+ "  INNER JOIN cell ON cell.id=cell_coverage.cell_id"
			+ "  WHERE "
			+ "    (CAST(:mcc AS int) IS NULL OR mcc=:mcc)"
			+ "       AND (CAST(:mnc AS int) IS NULL OR mnc=:mnc)"
			+ "       AND (CAST(:lac AS int) IS NULL OR lac=:lac)"
			+ "       AND (CAST(:cid AS int) IS NULL OR cid=:cid)"
			+ "       AND (CAST(:psc AS int) IS NULL OR psc=:psc)"
			+ "       AND (CAST(:rnc AS int) IS NULL OR (cid >> 16) & CAST(x'ffff' AS int) = :rnc)"
			+ "       AND (CAST(:radio AS text) IS NULL OR radio=:radio)"
	)
	String getCoverageHull(
			@Bind("mcc") Optional<Integer> mcc,
			@Bind("mnc") Optional<Integer> mnc,
			@Bind("lac") Optional<Integer> lac,
			@Bind("cid") Optional<Integer> cid,
			@Bind("psc") Optional<Integer> psc,
			@Bind("rnc") Optional<Integer> rnc,
			@Bind("radio") Optional<String> radio
	);


	@SqlQuery("SELECT cell.mcc, cell.mnc, cell.lac, cell.cid, cell.psc, cell.radio, MAX(signal) AS signal "
	        + " FROM cell_coverage inner join cell on cell.id=cell_coverage.cell_id"
			+ " WHERE ST_DWITHIN(location, "
			+ " ST_Transform(ST_SetSRID(ST_MakePoint(:lon, :lat),4326), 3857), "
			+   CELLS_AT_LAT_LON_RADIUS + ")"
			+ "       AND (CAST(:mcc AS int) IS NULL OR mcc=:mcc)"
			+ "       AND (CAST(:mnc AS int) IS NULL OR mnc=:mnc)"
			+ "       AND (CAST(:lac AS int) IS NULL OR lac=:lac)"
			+ "       AND (CAST(:cid AS int) IS NULL OR cid=:cid)"
			+ "       AND (CAST(:psc AS int) IS NULL OR psc=:psc)"
			+ "       AND (CAST(:rnc AS int) IS NULL OR (cid >> 16) & CAST(x'ffff' AS int) = :rnc)"
			+ "       AND (CAST(:radio AS text) IS NULL OR radio=:radio)"
			+ " GROUP BY cell.id"
			+ " ORDER BY cell.mcc, cell.mnc, cell.radio, cell.lac, signal DESC")
	@RegisterMapper(CellsAtLatLonResponseRecord.Mapper.class)
	List<CellsAtLatLonResponseRecord> getCellsAtLatLon(
			@Bind("lat") double lat,
			@Bind("lon") double lon,
			@Bind("mcc") Optional<Integer> mcc,
			@Bind("mnc") Optional<Integer> mnc,
			@Bind("lac") Optional<Integer> lac,
			@Bind("cid") Optional<Integer> cid,
			@Bind("psc") Optional<Integer> psc,
			@Bind("rnc") Optional<Integer> rnc,
			@Bind("radio") Optional<String> radio
	);

    @SqlCall("REFRESH MATERIALIZED VIEW cell_coverage")
    void refreshCoverage();

    /**
     * close with no args is used to close the connection
     */
    void close();
}
