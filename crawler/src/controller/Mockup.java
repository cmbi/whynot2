package controller;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Mockup {
	private static String	raw	= "/home/tbeek/Desktop/raw/";
	private static int		pdb	= 0, dssp = 0, hssp = 0;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String pdbid;
		for (int i = 0; i < 50000; i++) {
			pdbid = Mockup.genID();
			Mockup.writePDB(pdbid);
			if (Mockup.r.nextDouble() < 0.9) {
				Mockup.writeDSSP(pdbid);
				if (Mockup.r.nextDouble() < 0.9)
					Mockup.writeHSSP(pdbid);
			}
		}
		System.out.println("pdb: " + Mockup.pdb + ", dssp: " + Mockup.dssp + ", hssp: " + Mockup.hssp);
		for (int i = 0; i < 1000; i++)
			Mockup.writeDSSP(Mockup.genID());
		for (int i = 0; i < 1000; i++)
			Mockup.writeHSSP(Mockup.genID());
		System.out.println("pdb: " + Mockup.pdb + ", dssp: " + Mockup.dssp + ", hssp: " + Mockup.hssp);
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

	private static void writePDB(String pdbid) throws IOException {
		String dir = Mockup.raw + "pdb/" + pdbid.substring(0, 2) + "/";
		new File(dir).mkdirs();
		if (new File(dir + "pdb" + pdbid + ".ent").createNewFile())
			Mockup.pdb++;
	}

	private static void writeDSSP(String pdbid) throws IOException {
		String dir = Mockup.raw + "dssp/" + pdbid.substring(0, 2) + "/";
		new File(dir).mkdirs();
		if (new File(dir + pdbid + ".dssp").createNewFile())
			Mockup.dssp++;
	}

	private static void writeHSSP(String pdbid) throws IOException {
		String dir = Mockup.raw + "hssp/" + pdbid.substring(0, 2) + "/";
		new File(dir).mkdirs();
		if (new File(dir + pdbid + ".hssp").createNewFile())
			Mockup.hssp++;
	}
}
