package nl.ru.cmbi.whynot.webservice;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface RNG {
	public double getRandomNumber();

	public int getRandomNumberBetween(@WebParam(name = "min") int min, @WebParam(name = "max") int max);
}
//TODO Implement actual Whynot WS
//TODO Integrate into web-project
