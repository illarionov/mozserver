package ru0xdc.mozserver.jdbi;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.unstable.BindIn;
import ru0xdc.mozserver.model.Cell;
import ru0xdc.mozserver.model.SubmitCell;

import java.util.List;

@RegisterMapper(CellMapper.class)
public interface CellDao {

    @SqlUpdate("insert into cell (id, mcc, mnc, lac, cid, psc, radio) values (:id, :mcc, :mnc, :lac, :cid, :psc, :radio)")
    void insert(@Bind("id") long id, @BindSubmitCell SubmitCell submitCell);

    @SqlUpdate("insert into cell (mcc, mnc, lac, cid, psc, radio) values (:mcc, :mnc, :lac, :cid, :psc, :radio)")
    @GetGeneratedKeys
    long insert(@BindSubmitCell SubmitCell submitCell);

    @SqlQuery("select id from cell WHERE mcc=:mcc AND mnc=:mnc AND lac=:lac AND cid=:cid AND psc=:psc AND radio=:radio LIMIT 1 FOR SHARE")
    List<Long> selectForShare(@BindSubmitCell SubmitCell submitCell);

    @SqlQuery("select * from cell WHERE "
            + "(CAST(:mcc AS int) IS NULL OR mcc=:mcc)"
            + " AND (CAST(:mnc AS int) IS NULL OR mnc=:mnc)"
            + " AND (CAST(:lac AS int) IS NULL OR lac=:lac)"
            + " AND (CAST(:cid AS int) IS NULL OR cid=:cid)"
            + " AND (CAST(:psc AS int) IS NULL OR psc=:psc)"
            + " AND (CAST(:radio AS text) IS NULL OR radio=:radio)")
    ImmutableList<Cell> select(
            @Bind("mcc") Optional<Integer> mcc,
            @Bind("mnc") Optional<Integer> mnc,
            @Bind("lac") Optional<Integer> lac,
            @Bind("cid") Optional<Integer> cid,
            @Bind("psc") Optional<Integer> psc,
            @Bind("radio") Optional<String> radio);

    /**
     * close with no args is used to close the connection
     */
    void close();
}
