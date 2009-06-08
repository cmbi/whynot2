package test;

import java.util.Random;

import javax.jws.WebService;

@WebService(endpointInterface = "test.RNG")
public class RNGImpl implements RNG {
	public double getRandomNumber() {
		return new Random().nextDouble();
	}

	@Override
	public int getRandomNumberBetween(int min, int max) {
		return new Random().nextInt(max) + min;
	}
}
