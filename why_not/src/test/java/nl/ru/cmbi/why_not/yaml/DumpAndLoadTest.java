package nl.ru.cmbi.why_not.yaml;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import nl.ru.cmbi.why_not.model.Databank;
import nl.ru.cmbi.why_not.model.Databank.CrawlType;

import org.junit.Test;
import org.yaml.snakeyaml.Loader;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class DumpAndLoadTest {

	String	file	= "src/test/resources/yaml/databases.yaml";

	@Test
	public void dumpDB() throws IOException {
		Databank pdb, dssp;

		List<Databank> list = new ArrayList<Databank>();
		list.add(pdb = new Databank("PDB", "http://www.pdb.org/", "http://www.pdb.org/pdb/download/downloadFile.do?fileFormat=pdb&compression=NO&structureId=", null, ".*/pdb([\\d\\w]{4})\\.ent(\\.gz)?", CrawlType.FILE));
		list.add(dssp = new Databank("DSSP", "http://swift.cmbi.ru.nl/gv/dssp/", "http://mrs.cmbi.ru.nl/mrs-3/entry.do?db=dssp&id=", pdb, ".*/([\\d\\w]{4})\\.dssp", CrawlType.FILE));
		list.add(new Databank("HSSP", "http://swift.cmbi.ru.nl/gv/hssp/", "http://mrs.cmbi.ru.nl/mrs-3/entry.do?db=hssp&id=", dssp, ".*/([\\d\\w]{4})\\.hssp", CrawlType.FILE));
		list.add(new Databank("PDBFINDER", "http://swift.cmbi.ru.nl/gv/pdbfinder/", "http://mrs.cmbi.ru.nl/mrs-3/entry.do?db=pdbfinder2&id=", pdb, "ID           : ([\\d\\w]{4})", CrawlType.LINE));
		list.add(new Databank("PDBREPORT", "http://swift.cmbi.ru.nl/gv/pdbreport/", "http://www.cmbi.ru.nl/pdbreport/cgi-bin/nonotes?PDBID=", pdb, ".*pdbreport.*/([\\d\\w]{4})", CrawlType.FILE));
		list.add(new Databank("PDB_REDO", "http://www.cmbi.ru.nl/pdb_redo/", "http://www.cmbi.ru.nl/pdb_redo/cgi-bin/redir2.pl?pdbCode=", pdb, ".*pdb_redo.*/([\\d][\\d\\w]{3})", CrawlType.FILE));
		list.add(new Databank("NRG-CING", "http://nmr.cmbi.ru.nl/cing/", "http://www.cmbi.ru.nl/cgi-bin/cing.pl?id=", pdb, ".*/([\\d\\w]{4})\\.exist", CrawlType.FILE));
		list.add(new Databank("RECOORD", "http://www.ebi.ac.uk/msd-srv/docs/NMR/recoord/main.html", "http://www.cmbi.ru.nl/whynot/recoord_trampoline/", pdb, ".*/([\\d\\w]{4})_cns_w\\.pdb", CrawlType.FILE));
		list.add(new Databank("STRUCTUREFACTORS", "http://www.pdb.org/", "http://www.pdb.org/pdb/download/downloadFile.do?fileFormat=STRUCTFACT&compression=NO&structureId=", pdb, ".*/r([\\d\\w]{4})sf\\.ent", CrawlType.FILE));

		StringWriter sw = new StringWriter();
		new Yaml().dump(list, sw);
		sw.close();

		//Remove nulls, set type
		PrintWriter fw = new PrintWriter(new FileWriter(file));
		LineNumberReader lnr = new LineNumberReader(new StringReader(sw.toString()));
		String line;
		while ((line = lnr.readLine()) != null)
			if (!line.contains("null") && !line.contains("TreeSet"))
				if (line.startsWith("- &") && !line.contains("!!model.Databank"))
					fw.println(line + " !!model.Databank");
				else
					if (line.startsWith("- c") && !line.contains("!!model.Databank"))
						fw.println(line.replace("- ", "- !!model.Databank\n  "));
					else
						fw.println(line);
		fw.close();
		lnr.close();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void loadDB() throws IOException {
		FileReader fr = new FileReader(file);
		Constructor constructor = new Constructor(List.class);
		TypeDescription carDescription = new TypeDescription(List.class);
		carDescription.putListPropertyType("", Databank.class);
		constructor.addTypeDescription(carDescription);
		Loader loader = new Loader(constructor);
		Yaml yaml = new Yaml(loader);

		List<Databank> bla = (List<Databank>) yaml.load(fr);
		for (Databank db : bla)
			System.out.println(db);
		fr.close();
	}
}
