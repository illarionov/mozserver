package ru0xdc.mozserver.api.v1;

import com.google.common.base.Optional;
import com.sun.jersey.api.Responses;
import com.yammer.metrics.annotation.Timed;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru0xdc.mozserver.jdbi.CellDao;
import ru0xdc.mozserver.jdbi.CellLogDao;
import ru0xdc.mozserver.jdbi.WifiLogDao;
import ru0xdc.mozserver.model.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/v1/submit")
@Produces(MediaType.APPLICATION_JSON)
public class SubmitResource {

    Logger mLog = LoggerFactory.getLogger(SubmitResource.class.getName());

    private final DBI mJdbi;

    public SubmitResource(DBI jdbi) {
        mJdbi = jdbi;
    }

    @POST
    @Timed
    public Response submit(
            @HeaderParam("X-Nickname") String username,
            SubmitRequest submit
    ) {
        if (submit == null) {
            throw new SubmitErrorException("empty request");
        }

        if (submit.items == null || submit.items.isEmpty()) {
            throw new SubmitErrorException("no items");
        }

        // mLog.debug("submit: " + submit.toString());

        Handle h = mJdbi.open();
        try {
            h.begin();
            insertCellItems(h, submit.items);
            h.commit();

            h.begin();
            insertWifiItems(h, submit.items);
            h.commit();
        } finally {
            h.close();
        }

        return Response.noContent().build();
    }

    private void insertCellItems(Handle jdbiHandle,  List<SubmitItem> submitItems) {
        CellDao cellDao = jdbiHandle.attach(CellDao.class);
        CellLogDao logDao = jdbiHandle.attach(CellLogDao.class);

        List<Long> cellIds = new ArrayList<Long>();
        List<Integer> signals = new ArrayList<Integer>();
        List<Integer> timingAdvances = new ArrayList<Integer>();


        for (SubmitItem item: submitItems) {
            if (item.cell == null || item.cell.isEmpty()) continue;
            java.sql.Timestamp itemTime = new java.sql.Timestamp(item.time.getTime());
            for (SubmitCell cell: item.cell) {
                long cellId;
                if (!cell.hasEnoughFields()) {
                    mLog.info("Skip not fully defined cell " + cell);
                    continue;
                }

                List<Long> id = cellDao.selectForShare(cell);
                if (id.isEmpty()) {
                    cellId = cellDao.insert(cell);
                }else {
                    cellId = id.get(0);
                }

                cellIds.add(cellId);
                signals.add(cell.getSignalStrengthDbm());
                timingAdvances.add(cell.getTa() != SubmitCell.UNKNOWN_SIGNAL ? cell.getTa() : null);
            }
            if (!cellIds.isEmpty()) {
                logDao.insertBatch(
                        itemTime,
                        Optional.fromNullable(item.accuracy > 0 ? item.accuracy : null),
                        Optional.fromNullable(item.altiude != Float.NEGATIVE_INFINITY ? item.altiude : null),
                        Optional.fromNullable(item.altitude_accuracy > 0 ? item.altitude_accuracy : null),
                        item.locationAsEwkt(),
                        cellIds,
                        timingAdvances,
                        signals
                );
                cellIds.clear();
                signals.clear();
                timingAdvances.clear();
            }
        }
    }

    private void insertWifiItems(Handle jdbiHandle,  List<SubmitItem> submitItems) {
        List<String> keys = new ArrayList<String>();
        List<Integer> frequencies = new ArrayList<Integer>();
        List<Integer> signals = new ArrayList<Integer>();

        WifiLogDao wifiLogDao = jdbiHandle.attach(WifiLogDao.class);

        for (SubmitItem item: submitItems) {
            if (item.wifi == null || item.wifi.isEmpty()) continue;
            java.sql.Timestamp itemTime = new java.sql.Timestamp(item.time.getTime());
            for (SubmitWifi wifi: item.wifi) {
                keys.add(wifi.key);
                frequencies.add(wifi.frequency > 0 ? wifi.frequency : null);
                signals.add(wifi.signal);
            }

            wifiLogDao.insertBatch(itemTime, item.locationAsEwkt(), keys, frequencies, signals);

            keys.clear();
            frequencies.clear();
            signals.clear();
        }
    }

    public static class SubmitErrorException extends WebApplicationException {

        public SubmitErrorException() {
            super(Responses.clientError().entity(new SubmitResponse()).build());
        }

        public SubmitErrorException(String message) {
            super(Responses.clientError().entity(new SubmitResponse(message)).build());
        }
    }

}
