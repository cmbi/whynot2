package test;

import java.util.Random;

import javax.jws.WebService;

@WebService(endpointInterface = "test.RNG")
public class RNGImpl implements RNG {
	/**
	 * @see http://localhost:8080/whynot-ws/webservice/getRandomNumber
	 */
	public double getRandomNumber() {
		return new Random().nextDouble();
	}

	/**
	 * @see http://localhost:8080/whynot-ws/webservice/getRandomNumberBetween/min/4/max/12
	 */
	public int getRandomNumberBetween(int min, int max) {
		return new Random().nextInt(max) + min;
	}
}
