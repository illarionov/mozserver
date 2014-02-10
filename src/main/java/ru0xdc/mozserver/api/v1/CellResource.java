package ru0xdc.mozserver.api.v1;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.palominolabs.jersey.cors.Cors;
import com.yammer.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru0xdc.mozserver.jdbi.CellDao;
import ru0xdc.mozserver.model.Cell;
import ru0xdc.mozserver.model.SubmitCell;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

// TODO: replace to cell
@Path("/v1/cells")
@Produces(MediaType.APPLICATION_JSON)
public class CellResource {

    Logger mLog = LoggerFactory.getLogger(SubmitResource.class.getName());

    private CellDao mCellDao;

    public CellResource(CellDao cellDao) {
        mCellDao = cellDao;
    }

    @GET
    @Timed
    @Cors
    public ImmutableList<Cell> get(
            @QueryParam("network_radio") Optional<String> queryRadio,
            @QueryParam("mcc") Optional<Integer> queryMcc,
            @QueryParam("mnc") Optional<Integer> queryMnc,
            @QueryParam("lac") Optional<Integer> queryLac,
            @QueryParam("cid") Optional<Integer> queryCid,
            @QueryParam("psc") Optional<Integer> queryPsc) {

        return mCellDao.select(queryMcc, queryMnc, queryLac, queryCid, queryPsc, queryRadio);
    }
}
