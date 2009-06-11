package nl.ru.cmbi.whynot.webservice;

import java.util.Random;

import javax.jws.WebService;

@WebService(endpointInterface = "nl.ru.cmbi.whynot.webservice.RNG")
public class RNGImpl implements RNG {
	/**
	 * @see /webservice/ws/getRandomNumber
	 */
	public double getRandomNumber() {
		return new Random().nextDouble();
	}

	/**
	 * @see /webservice/ws/getRandomNumberBetween/min/4/max/12
	 */
	public int getRandomNumberBetween(int min, int max) {
		return new Random().nextInt(max) + min;
	}
}
