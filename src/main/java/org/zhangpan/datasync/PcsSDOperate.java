package org.zhangpan.datasync;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.solution.pcs.sd.PcsSd;
import com.baidu.solution.pcs.sd.impl.records.UpdateRecord;
import com.baidu.solution.pcs.sd.impl.tables.CreateTable;
import com.baidu.solution.pcs.sd.impl.tables.DescribeTable;
import com.baidu.solution.pcs.sd.model.ColumnType;
import com.baidu.solution.pcs.sd.model.Record;
import com.baidu.solution.pcs.sd.model.RecordSet;
import com.baidu.solution.pcs.sd.model.Table;
import com.baidu.solution.pcs.sd.model.condition.AndCondition;
import com.google.gson.Gson;

public class PcsSDOperate {

	private static Logger logger = LoggerFactory.getLogger(PcsSDOperate.class);

	/** Name of favorite song table. */
	private static final String SYNC_TABLE = "sync_info";
	private static final String LAST_MODIFY_TIME = "last_modify_time";

	protected static String devAccessToken = "3.d812bd27085fa39feafa8410aefb6f10.2592000.1367463460.3355604315-502107";
	protected static String accessToken = "3.d812bd27085fa39feafa8410aefb6f10.2592000.1367463460.3355604315-502107";

	public PcsSDOperate() {

		init();
	}

	private static class SyncInfo {
		public String name;
		public long create_time;
	}

	private Table createSyncInfoTable()
			throws IOException {
		// MUST use the access token of developer. The users' access token has
		// no permission to do the request about table, only the records.
		PcsSd service = new PcsSd(accessToken);

		// Add columns.
		CreateTable create = service.tables().create(SYNC_TABLE);
		create.addColumn("name", "last sync time", ColumnType.STRING, true);
		create.addColumn("createTime",
				"time of song added to the favorite list", ColumnType.INT, true);

		return create.execute();
	}

	private boolean isSyncInfoTableExists() {
		try {

			PcsSd service = new PcsSd(accessToken);
			DescribeTable describeTable = service.tables().describe(SYNC_TABLE);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * @param userAccessToken
	 * @return
	 * @throws IOException
	 */
	private static RecordSet insertRecords()
			throws IOException {

		PcsSd service = new PcsSd(accessToken);
		List<SyncInfo> infos = new LinkedList<SyncInfo>();
		SyncInfo info = new SyncInfo();
		info.name = LAST_MODIFY_TIME;
		info.create_time = 0L;
		infos.add(info);
		return service.records().insert(SYNC_TABLE, infos).execute();
	}

	public static Long getLastSyncTime() throws IOException {

		RecordSet recordSet = new PcsSd(accessToken)
				.records()
				.select(SYNC_TABLE)
				.setCondition(
						new AndCondition().addEqual("name", LAST_MODIFY_TIME))
				.execute();

		logger.info(new Gson().toJson(recordSet));
		return null;
	}


	/**
	 * Correct wrong records.
	 * 
	 * @return
	 * 
	 * @throws IOException
	 */
	public static void updateLastModifyTime() throws IOException {
		PcsSd service = new PcsSd(accessToken);
		RecordSet records = service
				.records()
				.select(SYNC_TABLE)
				.setCondition(
						new AndCondition().addEqual("name", LAST_MODIFY_TIME))
				.execute();

		// Correct all records with wrong information.
		UpdateRecord updateRecord = null;
		for (Record oldRecord : records.getRecords()) {
			SyncInfo oldInfo = oldRecord.toType(SyncInfo.class);
			// Correct the record.
			SyncInfo info = oldInfo;
			info.create_time = System.currentTimeMillis();
			// Add first update to update record request.
			updateRecord = service.records().update(SYNC_TABLE,
					oldRecord.getKey(), oldRecord.getModifyTime(), info);
		}

		// Update the records with the wrong albums information.
		updateRecord.setReplace().execute();
	}

	public void init() {

		try {

			if (!isSyncInfoTableExists()) {
				logger.info(SYNC_TABLE + " table not exits");
				createSyncInfoTable();
				insertRecords();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
