package controller;

import interfaces.ICrawl;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		if (args.length != 2)
			throw new IllegalArgumentException("Usage: crawler DATABASE DIRECTORY/FILE");

		ICrawl dao;
	}

}
