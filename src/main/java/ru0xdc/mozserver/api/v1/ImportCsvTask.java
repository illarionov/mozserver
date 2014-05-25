package ru0xdc.mozserver.api.v1;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMultimap;
import com.yammer.dropwizard.tasks.Task;
import ru0xdc.mozserver.jdbi.CellDao;
import ru0xdc.mozserver.jdbi.CellLogDao;
import ru0xdc.mozserver.model.SubmitCell;

import java.io.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by alexey on 25.05.14.
 */
public class ImportCsvTask extends Task {

	private final CellDao mCellDao;
	private final CellLogDao mCellLogDao;

	public ImportCsvTask(CellDao cellDao, CellLogDao cellLogDao) {
		super("ImportCsvTask");
		mCellDao = cellDao;
		mCellLogDao = cellLogDao;

	}

	@Override
	public void execute(ImmutableMultimap<String, String> parameters, PrintWriter output) throws Exception {
		String csvFile = "/mnt/ext/alexey/compile/mozstumbler-server3/opencellid/measurements/log1.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String radio;
				String[] record = line.split(cvsSplitBy);

				int mcc = Integer.valueOf(record[0]);
				int mnc = Integer.valueOf(record[1]);
				int lac = Integer.valueOf(record[2]);
				int cid = Integer.valueOf(record[3]);
				double lon = Double.valueOf(record[4]);
				double lat = Double.valueOf(record[5]);
				long createdAt = Long.valueOf(record[7]);
				int signal = Integer.valueOf(record[6]);

				if (signal < -160 || signal > -40) signal = -120;
				radio = cid < 0xffff0000l ? "gsm" : "umts";

				SubmitCell cell = new SubmitCell(radio, mcc, mnc, lac, cid, SubmitCell.UNKNOWN_IDENT,
						signal, SubmitCell.UNKNOWN_SIGNAL, SubmitCell.UNKNOWN_SIGNAL, "opencellid");
				List<Long> id = mCellDao.selectForShare(cell);
				long cellId;
				if (id.isEmpty()) {
					cellId = mCellDao.insert(cell);
				}else {
					cellId = id.get(0);
				}

				mCellLogDao.insertBatch(
						new java.sql.Timestamp(createdAt),
						Optional.fromNullable((Float)null),
						Optional.fromNullable((Float)null),
						Optional.fromNullable((Float)null),
						String.format(Locale.US, "SRID=4326;POINT(%f %f)", lon, lat),
						Collections.singletonList(cellId),
						Collections.singletonList(cell.getNetworkType()),
						Collections.singletonList((Integer)null),
						Collections.singletonList(cell.getSignalStrengthDbm())
				);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("Done");
	}
}
