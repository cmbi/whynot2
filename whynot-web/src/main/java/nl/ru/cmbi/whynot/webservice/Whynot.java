package nl.ru.cmbi.whynot.webservice;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface Whynot {
	List<String> getAnnotations(@WebParam(name = "databank") String databank, @WebParam(name = "pdbid") String pdbid);

	List<String> getEntries(@WebParam(name = "databank") String databank, @WebParam(name = "selection") String selection);
}