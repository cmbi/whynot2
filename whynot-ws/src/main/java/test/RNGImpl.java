package test;

import javax.jws.WebService;

@WebService(endpointInterface = "test.RNG")
public class RNGImpl implements RNG {
	public int getRandomNumber() {
		return 4;
	}
}
