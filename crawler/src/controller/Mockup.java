package controller;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Mockup {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String raw = "/home/tbeek/Desktop/raw/", pdb = raw + "pdb/", dssp = raw + "dssp/", hssp = raw + "hssp/";

		int count = 0;
		String pdbid, dir;
		for (int i = 0; i < 50000; i++) {
			pdbid = Mockup.genID();
			dir = pdb + pdbid.substring(0, 2) + "/";
			new File(dir).mkdirs();
			if (new File(dir + "pdb" + pdbid + ".ent").createNewFile())
				count++;
		}
		System.out.println("PDB: " + count);
		count = 0;
		for (int i = 0; i < 40000; i++) {
			pdbid = Mockup.genID();
			dir = dssp + pdbid.substring(0, 2) + "/";
			new File(dir).mkdirs();
			if (new File(dir + pdbid + ".dssp").createNewFile())
				count++;
		}
		System.out.println("DSSP: " + count);
		count = 0;
		for (int i = 0; i < 30000; i++) {
			pdbid = Mockup.genID();
			dir = hssp + pdbid.substring(0, 2) + "/";
			new File(dir).mkdirs();
			if (new File(dir + pdbid + ".hssp").createNewFile())
				count++;
		}
		System.out.println("HSSP: " + count);
	}

	private static Random	r	= new Random();
	private static String	abc	= "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	private static String genID() {
		StringBuilder sb = new StringBuilder();
		sb.append(Mockup.r.nextInt(3) + 1);
		sb.append(Mockup.abc.charAt(Mockup.r.nextInt(36)));
		sb.append(Mockup.abc.charAt(Mockup.r.nextInt(36)));
		sb.append(Mockup.abc.charAt(Mockup.r.nextInt(36)));
		return sb.toString();
	}
}
