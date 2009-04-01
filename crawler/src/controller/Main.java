package controller;

import interfaces.CrawlImpl;
import interfaces.ICrawl;
import io.Crawler;

import java.io.File;
import java.util.List;

import model.Database;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		if (args.length != 2)
			throw new IllegalArgumentException("Usage: crawler DATABASE DIRECTORY/FILE");

		ICrawl dao = new CrawlImpl();
		Database db = dao.getDatabase(args[0]);
		String regex = db.getRegex();
		List<File> entries = new Crawler(regex).getEntries(args[1]);
		dao.addToDB(args[0], entries);
	}
}
