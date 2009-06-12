import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnnotateStructureFactorsOnExperimentType {
	public static void main(String... args) throws Exception {
		String id = "([a-zA-Z0-9]{4})";
		List<String> em_entries = getEntries(id + "\t.*\tEM");
		List<String> nmr_entries = getEntries(id + "\t.*\tNMR");
		List<String> diff_entries = getEntries(id + "\t.*\tdiffraction");

		String prev_comment = "COMMENT: Unknown comment";
		for (String pdbid : args)
			if (em_entries.contains(pdbid))
				;
			else
				if (nmr_entries.contains(pdbid)) {
					if (!prev_comment.equals("COMMENT: NMR experiment"))
						System.out.println(prev_comment = "COMMENT: NMR experiment");
					System.out.println("STRUCTUREFACTORS," + pdbid);
				}
				else
					if (diff_entries.contains(pdbid))
						;
					else
						;//Other
	}

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
