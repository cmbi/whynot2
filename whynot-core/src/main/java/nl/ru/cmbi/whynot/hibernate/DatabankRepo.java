package nl.ru.cmbi.whynot.hibernate;

import nl.ru.cmbi.whynot.model.Databank;

public interface DatabankRepo extends DomainObjectRepository<Databank> {

    Databank getRoot();
	Databank findByName(final String name);
}
