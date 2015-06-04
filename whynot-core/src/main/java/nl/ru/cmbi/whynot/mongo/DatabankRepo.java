package nl.ru.cmbi.whynot.mongo;

import java.util.List;

import nl.ru.cmbi.whynot.model.Databank;

public interface DatabankRepo {

    Databank getRoot();
	Databank findByName(final String name);
	List<Databank> findAll();
}
