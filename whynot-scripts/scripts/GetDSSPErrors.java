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
		String comment = "";
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
			String line = null, previous = null;
			while ((line = br.readLine()) != null)
				if (!line.equals(previous))
					previous = line;

			//Wait for DSSPCMBI to exit with 1
			if (process.waitFor() != 0) {
				//Strip !!! & trim
				String lastline = previous.replaceAll("!", "").trim();
				//Print COMMENT: line
				if (!comment.equals(lastline)) {
					comment = lastline;
					System.out.println("COMMENT: " + comment);
				}
				//Print DSSP,1giy
				System.out.println("DSSP," + pdbid);
			}
		}
	}
}
