package org.zhangpan.datasync;

/**
 * @author yanlong
 *
 */
public class Song {
	
	/**
	 * Constructor.
	 * 
	 * @param name
	 * @param artist
	 * @param ablums
	 * @param createTime
	 * @param language
	 */
	public Song(String name, String artist, String ablums,
			long createTime, String language) {
		this.name = name;
		this.artist = artist;
		this.albums = ablums;
		this.create_time = createTime;
		this.language = language;
	}

	private String name;
	
	private String artist;
	
	private String albums;
	
	private long create_time;
	
	private String language;
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("name : " + name);
		builder.append(", ");
		builder.append("artist : " + artist);
		builder.append(", ");
		builder.append("albums : " + albums);
		builder.append(", ");
		builder.append("create_time : " + create_time);
		builder.append(", ");
		builder.append("language : " + language);
		return builder.toString();
	}

	/**
	 * @param string
	 * @return
	 */
	public Song setAlbums(String albums) {
		this.albums = albums;
		return this;
	}
}
