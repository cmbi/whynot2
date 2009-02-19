package model;

import java.util.Calendar;
import java.util.Date;

public class Comment {
	private int comid;

	private Author author;

	private String comment;

	private long timestamp;

	public Comment(String content) {
		this.comid = -1;
		this.author = new Author("Administrator", "whynot@cmbi.ru.nl");
		this.comment = content;
		this.timestamp = Calendar.getInstance().getTime().getTime();
	}

	public Comment(int comid, Author author, String content, long timestamp) {
		this.comid = comid;
		this.author = author;
		this.comment = content;
		this.timestamp = timestamp;
	}

	public int getComid() {
		return this.comid;
	}

	public void setComid(int i) {
		this.comid = i;
	}

	public Author getAuthor() {
		return this.author;
	}

	public String getComment() {
		return this.comment;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public Date getDatetimestamp() {
		return new Date(this.timestamp);
	}
}
