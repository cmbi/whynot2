package interfaces;

import java.io.File;
import java.util.List;

import model.Database;

public interface ICrawl {
	public Database getDatabase(String name);

	public void addToDB(String dbname, List<File> entries);
}
