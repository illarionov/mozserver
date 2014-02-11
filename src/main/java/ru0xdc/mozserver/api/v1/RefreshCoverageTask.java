package ru0xdc.mozserver.api.v1;

import com.google.common.collect.ImmutableMultimap;
import com.yammer.dropwizard.tasks.Task;
import ru0xdc.mozserver.jdbi.CellDao;
import ru0xdc.mozserver.jdbi.CellLogDao;

import java.io.PrintWriter;


public class RefreshCoverageTask extends Task {

    private final CellLogDao mDao;

    public RefreshCoverageTask(CellLogDao dao) {
        super("refresh_coverage");
        mDao = dao;
    }

    @Override
    public void execute(ImmutableMultimap<String, String> parameters, PrintWriter output) throws Exception {
        mDao.refreshCoverage();
    }
}
