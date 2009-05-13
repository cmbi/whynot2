package nl.ru.cmbi.why_not;

import org.springframework.stereotype.Service;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Usage:");
		System.out.println("java install.Installer -cp why_not.jar");
		System.out.println("java crawl.Crawler -cp why_not.jar");
		System.out.println("java comment.Commenter -cp why_not.jar");
		System.out.println("java list.Lister -cp why_not.jar");
	}

	@Service
	public static class RandomNumberGenerator {
		public int getRandomNumber() {
			//chosen by fair dice roll
			//guaranteed to be random
			return 4;
		}
	}

}
