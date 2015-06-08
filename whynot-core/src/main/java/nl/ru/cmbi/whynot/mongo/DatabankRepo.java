package nl.ru.cmbi.whynot.mongo;

import java.util.List;

import nl.ru.cmbi.whynot.model.Databank;

public interface DatabankRepo {

	Databank getParent(Databank db);
    Databank getRoot();
	Databank findByName(final String name);
	List<Databank> findAll();
	long countAll();
}
