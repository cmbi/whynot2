package test;

import javax.jws.WebService;

@WebService
public interface RNG {
	public int getRandomNumber();
}
