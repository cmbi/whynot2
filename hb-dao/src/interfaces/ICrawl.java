package interfaces;

import java.util.List;

import model.Database;
import model.EntryFile;

public interface ICrawl {
	public Database retrieveDatabase(String name);

	public void storeAll(List<EntryFile> entries);

	public void update(Database db);
}
