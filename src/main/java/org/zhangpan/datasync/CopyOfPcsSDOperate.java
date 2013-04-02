package org.zhangpan.datasync;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import com.baidu.solution.client.service.ServiceException;
import com.baidu.solution.pcs.sd.PcsSd;
import com.baidu.solution.pcs.sd.impl.ErrorInfo;
import com.baidu.solution.pcs.sd.impl.records.UpdateRecord;
import com.baidu.solution.pcs.sd.impl.tables.CreateTable;
import com.baidu.solution.pcs.sd.model.ColumnType;
import com.baidu.solution.pcs.sd.model.Order;
import com.baidu.solution.pcs.sd.model.Record;
import com.baidu.solution.pcs.sd.model.RecordSet;
import com.baidu.solution.pcs.sd.model.Table;
import com.baidu.solution.pcs.sd.model.condition.AndCondition;
import com.google.gson.Gson;

public class CopyOfPcsSDOperate {

	private static enum Steps {
		CREATE_TABLE, INSERT_RECORD, SELECT_RECORD, UPDATE_RECORD, DONE;
	}

	private int stepIndex = 0;

	public CopyOfPcsSDOperate() {

		initControls();
	}

	static final Logger LOGGER = Logger.getLogger(CopyOfPcsSDOperate.class.getName());

	/** Name of favorite song table. */
	private static final String FAVORITE_TABLE = "favorite_song";

	/** Name of artist index. */
	private static final String ARTIST_INDEX = "artist_index";

	/** Name of artist index. */
	private static final String LANGUAGE_INDEX = "language_index";

	/** Get the access token of developer. */
	private static String getDeveloperAccessToken() {
		return "Access token of developer";
	}

	/** Get the access token of user. */
	private static String getUserAccessToken() {
		return "Access token of user";
	}

	/**
	 * Create favorite song table with the access token of developer(You).
	 * 
	 * @throws IOException
	 *             If error occurs while executing the request
	 * @throws ServiceException
	 *             (extends IOException) If the server of service returns error
	 *             code
	 */
	private static Table createFavoriteSongTable(String accessToken)
			throws IOException {
		// MUST use the access token of developer. The users' access token has
		// no permission to do the request about table, only the records.
		PcsSd service = new PcsSd(accessToken);

		// Add columns.
		CreateTable create = service.tables().create(FAVORITE_TABLE);
		create.addColumn("name", "name of song", ColumnType.STRING, true);
		create.addColumn("artist", "artist of song", ColumnType.STRING, true);
		create.addColumn("ablums", "ablums name to which the song belong",
				ColumnType.STRING, true);
		create.addColumn("createTime",
				"time of song added to the favorite list", ColumnType.INT, true);
		create.addColumn("language", "song language", ColumnType.STRING, true);
		// Add indexes.
		create.addIndex(ARTIST_INDEX, "artist", Order.ASC).addIndex(
				LANGUAGE_INDEX, "language", Order.ASC);

		return create.execute();
	}

	/**
	 * @param userAccessToken
	 * @return
	 * @throws IOException
	 */
	private static RecordSet insertRecords(String userAccessToken)
			throws IOException {
		// MUST use access token of user whose information will be inserted to
		// the table with the record.
		PcsSd service = new PcsSd(userAccessToken);

		// Create some favorite songs and inserts them to favorite table.
		List<Song> favorSongs = new LinkedList<Song>();

		favorSongs.add(new Song("Black Humor", "Jay", "Jay", System
				.currentTimeMillis(), "Chinese"));
		// Add a song with a wrong albums, will be update soon.
		favorSongs.add(new Song("Super Star", "SHE", "SHE", System
				.currentTimeMillis(), "Chinese"));

		return service.records().insert(FAVORITE_TABLE, favorSongs).execute();
	}

	/**
	 * @param userAccessToken
	 * @param name
	 * @param albums
	 * @return
	 * @throws IOException
	 */
	private static RecordSet selectRecordsBySongNameAndAlbums(
			String userAccessToken, String name, String albums)
			throws IOException {
		return new PcsSd(userAccessToken)
				.records()
				.select(FAVORITE_TABLE)
				.setCondition(
						new AndCondition().addEqual("albums", albums).addEqual(
								"name", name)).execute();
	}

	/**
	 * Correct wrong records.
	 * 
	 * @return
	 * 
	 * @throws IOException
	 */
	private static RecordSet correctRecords(String userAccessToken,
			RecordSet records) throws IOException {
		if (records.getRecords().size() < 1) {
			return new RecordSet();
		}

		// Correct all records with wrong information.
		UpdateRecord updateRecord = null;
		for (Record wrongRecord : records.getRecords()) {
			// Correct the record.
			Song wrongSong = wrongRecord.toType(Song.class);
			Song correctSong = wrongSong.setAlbums("Super Star");
			// Add first update to update record request.
			if (null == updateRecord) {
				updateRecord = new PcsSd(getUserAccessToken()).records()
						.update(FAVORITE_TABLE, wrongRecord.getKey(),
								wrongRecord.getModifyTime(), correctSong);
				continue;
			}
			// Add others.
			updateRecord.add(wrongRecord.getKey(), wrongRecord.getModifyTime(),
					wrongRecord);
		}

		// Update the records with the wrong albums information.
		return updateRecord.setReplace().execute();
	}

	Table table;

	RecordSet records;



	private void initControls() {
		String log = "";
		try {
			switch (Steps.values()[stepIndex % Steps.values().length]) {
			case CREATE_TABLE:
				try {
					// Create favorite song table with columns and indexes.
					table = createFavoriteSongTable(getDeveloperAccessToken());
					log = "Step 1: Create " + FAVORITE_TABLE + " table done.";
				} catch (ServiceException e) {
					ErrorInfo info = e.toErrorInformation(ErrorInfo.class);
					long code = info.getErrorCode();
					if (code == 31476 || code == 31472) {
						log = "Step 1: " + FAVORITE_TABLE
								+ " table already exist.";
						break;
					}
					log = "Step 1: Create " + FAVORITE_TABLE + " table failed:"
							+ e.getMessage();
					throw e;
				}
				break;
			case INSERT_RECORD:
				records = insertRecords(getUserAccessToken());
				log = "Step 2: Inserted " + records.size() + " records:\n"
						+ new Gson().toJson(records);
				break;
			case SELECT_RECORD:
				// Search table by artist name.
				records = selectRecordsBySongNameAndAlbums(
						getUserAccessToken(), "Super Star", "SHE");
				log = "Step 3: Selected " + records.size() + " records:\n"
						+ new Gson().toJson(records);
				break;
			case UPDATE_RECORD:
				// Correct records with wrong information.
				records = correctRecords(getUserAccessToken(), records);
				log = "Step 4: Corrected " + records.size() + " records:\n"
						+ new Gson().toJson(records);
			case DONE:
				table = null;
				records = null;
				log = "Done. Press to run again.";
			}
			stepIndex++;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
