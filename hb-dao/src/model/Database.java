package model;

import java.net.URL;
import java.util.Set;
import java.util.regex.Pattern;

public class Database {
	private String		name;
	private URL			reference;
	private URL			filelink;
	private Database	parent;

	private Pattern		filepattern;
	private Set<Entry>	entries;

	protected Database() {}
}
