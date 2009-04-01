package main;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String dir = "raw/";
		if (!new File(dir).isDirectory())
			new File(dir).mkdir();
		
		for (File f : Arrays.asList(new File(dir).listFiles()))
			f.delete();
		
		String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random r = new Random();
		for (int i = 0; i < 5; i++)
			new File(dir+"pdb" + r.nextInt(10) + abc.charAt(r.nextInt(36)) + abc.charAt(r.nextInt(36)) + abc.charAt(r.nextInt(36))+".ent").createNewFile();
		for (int i = 0; i < 5; i++)
			new File(dir+"" + r.nextInt(10) + abc.charAt(r.nextInt(36)) + abc.charAt(r.nextInt(36)) + abc.charAt(r.nextInt(36))+".dssp").createNewFile();
	}

}
