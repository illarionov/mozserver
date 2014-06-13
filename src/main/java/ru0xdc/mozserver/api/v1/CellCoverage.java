package ru0xdc.mozserver.api.v1;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.palominolabs.jersey.cors.Cors;
import com.yammer.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Entity;
import ru0xdc.mozserver.jdbi.CellDao;
import ru0xdc.mozserver.jdbi.CellLogDao;
import ru0xdc.mozserver.jdbi.CellsAtLatLonResponseRecord;
import ru0xdc.mozserver.jdbi.CoverageResponseRecord;
import ru0xdc.mozserver.model.Cell;
import ru0xdc.mozserver.model.SubmitCell;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/v1/coverage")
@Produces(MediaType.APPLICATION_JSON)
public class CellCoverage {

    Logger mLog = LoggerFactory.getLogger(SubmitResource.class.getName());

    private CellLogDao mCellLogDao;

    public CellCoverage(CellLogDao cellDao) {
        mCellLogDao = cellDao;
    }

    @GET
    @Timed
    @Cors
    public double[][] get(
            @QueryParam("network_radio") Optional<String> queryRadio,
            @QueryParam("mcc") Optional<Integer> queryMcc,
            @QueryParam("mnc") Optional<Integer> queryMnc,
            @QueryParam("lac") Optional<Integer> queryLac,
            @QueryParam("cid") Optional<Integer> queryCid,
            @QueryParam("psc") Optional<Integer> queryPsc,
			@QueryParam("rnc") Optional<Integer> queryRnc) {

        List<CoverageResponseRecord> coverateResponse = mCellLogDao.getCoverage(queryMcc, queryMnc,
                queryLac, queryCid, queryPsc, queryRnc, queryRadio);

        ArrayList<double[]> coverage = new ArrayList<double[]>(coverateResponse.size());
        for (CoverageResponseRecord r: coverateResponse) coverage.add(r.vec3d());

        return coverage.toArray(new double[coverage.size()][]);
    }


	@GET @Path("/hull")
	@Timed
	@Cors
	public Response getCoverageHull(@QueryParam("network_radio") Optional<String> queryRadio,
			@QueryParam("mcc") Optional<Integer> queryMcc,
			@QueryParam("mnc") Optional<Integer> queryMnc,
			@QueryParam("lac") Optional<Integer> queryLac,
			@QueryParam("cid") Optional<Integer> queryCid,
			@QueryParam("psc") Optional<Integer> queryPsc,
			@QueryParam("rnc") Optional<Integer> queryRnc) {
		String json =  mCellLogDao.getCoverageHull(queryMcc, queryMnc,
				queryLac, queryCid, queryPsc, queryRnc, queryRadio);

		return Response.ok().entity(json).build();
	}


	@GET @Path("/point")
	@Timed
	@Cors
	public List<CellsAtLatLonResponseRecord> getCellsAtPoint(
			@QueryParam("lat") double queryLat,
			@QueryParam("lon") double queryLon,
			@QueryParam("network_radio") Optional<String> queryRadio,
			@QueryParam("mcc") Optional<Integer> queryMcc,
			@QueryParam("mnc") Optional<Integer> queryMnc,
			@QueryParam("lac") Optional<Integer> queryLac,
			@QueryParam("cid") Optional<Integer> queryCid,
			@QueryParam("psc") Optional<Integer> queryPsc,
			@QueryParam("rnc") Optional<Integer> queryRnc
	) {
		return mCellLogDao.getCellsAtLatLon(queryLat, queryLon, queryMcc, queryMnc, queryLac,
				queryCid, queryPsc, queryRnc, queryRadio);
	}

}
