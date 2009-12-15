package scripts;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * Expects 3 parameters:
 * 1: Databank name
 * 2: Comment to add to entries
 * 3: Pattern to match on line
 * Adds annotation for comment for each PDBID read from
 * System.in that matches the specified Pattern.
 */
public class GetDSSPErrors { 
	public static void main(String... args) throws Exception {
		Scanner scn = new Scanner(System.in);
		String prevError = ""; //We don't want to print immediate doubles
		while (scn.hasNextLine()) {
			String pdbid = scn.nextLine();

			//Get PDB file /data/uncompressed/pdb/pdb1giy.ent
			String pdbfile = "/data/uncompressed/pdb/pdb" + pdbid + ".ent";
			//Run dsspcmbi pdbfile /dev/null/
			ProcessBuilder pb = new ProcessBuilder("./scripts/dsspcmbi", pdbfile, "/dev/null");
			pb.redirectErrorStream(true);
			Process process = pb.start();

			//Read stdout
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null, lastline = "No output read from dsspcmbi";
			while ((line = br.readLine()) != null)
				lastline = line;

			//Wait for DSSPCMBI to exit: continue if exit code was normal
			if (process.waitFor() == 0)
				continue;

			//Strip "!!!" & trim
			String error = lastline.replaceAll("!", "").trim();
			//Print COMMENT: error
			if (!prevError.equals(error))
				System.out.println("COMMENT: " + (prevError = error));
			//Print DSSP,1giy
			System.out.println("DSSP," + pdbid);
		}
	}
}
