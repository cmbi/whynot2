package scripts;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Expects 3 parameters:
 * 1: Databank name
 * 2: Comment to add to entries
 * 3: Pattern to match on line
 * Adds annotation for comment for each PDBID read from
 * System.in that matches the specified Pattern.
 */
public class ParsePDBEntryType {
	public static void main(String... args) throws Exception {
		if (args.length != 3)
			throw new IllegalArgumentException("Expected arguments [DBNAME] [COMMENT] [PATTERN]");
		List<String> nmr_entries = getEntries(args[2]);
		List<String> affected = new ArrayList<String>();

		//Read pdbids from System.in and add if in nmr entries
		Scanner scn = new Scanner(System.in);
		while (scn.hasNextLine()) {
			String pdbid = scn.nextLine();
			if (nmr_entries.contains(pdbid))
				affected.add(pdbid);
		}

		//If none were affected, we're done
		if (affected.isEmpty())
			return;

		//Write comment and all entries to System.out
		System.out.println("COMMENT: " + args[1]);
		for (String pdbid : affected)
			System.out.println(args[0] + ',' + pdbid);
	}

	/**
	 * Reads the NMR entries from wwpdb.org's pdb_entry_type.txt.
	 * 
	 * @param pattern
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private static List<String> getEntries(String pattern) throws MalformedURLException, IOException {
		URLConnection con = new URL("ftp://ftp.wwpdb.org/pub/pdb/derived_data/pdb_entry_type.txt").openConnection();
		Scanner scn = new Scanner(con.getInputStream());
		Matcher m;
		Pattern p = Pattern.compile(pattern);
		List<String> pdbids = new ArrayList<String>();
		while (scn.hasNextLine())
			if ((m = p.matcher(scn.nextLine())).matches())
				pdbids.add(m.group(1));
		return pdbids;
	}
}
