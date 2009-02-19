package inout.file;

import inout.CommandlineWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import model.Database;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DatabasesXMLReader {
	private File file;

	public DatabasesXMLReader() {
		this.file = new File("databases.xml");
		if (!this.file.exists())
			this.writeDefaultXMLFile();
	}

	private void writeDefaultXMLFile() {
		try {
			String[] lines = {
					"<?xml version='1.0'?>",
					"<databases>",
					"	<database>",
					"		<name>DSSP</name>",
					"		<reference>http://swift.cmbi.ru.nl/gv/dssp/</reference>",
					"		<filelink>http://mrs.cmbi.ru.nl/mrs-3/entry.do?db=dssp&amp;id=</filelink>",
					"		<regex>.*/[\\d\\w]{4}\\.dssp</regex>",
					"		<offset>0</offset>",
					"	</database>",
					"	<database>",
					"		<name>HSSP</name>",
					"		<reference>http://swift.cmbi.kun.nl/gv/hssp/</reference>",
					"		<filelink>http://mrs.cmbi.ru.nl/mrs-3/entry.do?db=hssp&amp;id=</filelink>",
					"		<regex>.*/[\\d\\w]{4}\\.hssp</regex>",
					"		<offset>0</offset>",
					"	</database>",
					"	<database>",
					"		<name>PDB</name>",
					"		<reference>http://www.pdb.org/</reference>",
					"		<filelink>http://www.pdb.org/pdb/download/downloadFile.do?fileFormat=pdb&amp;compression=NO&amp;structureId=</filelink>",
					"		<regex>.*/pdb[\\d\\w]{4}\\.ent(\\.gz)?</regex>",
					"		<offset>3</offset>",
					"	</database>",
					"	<database>",
					"		<name>PDBFINDER</name>",
					"		<reference>http://swift.cmbi.ru.nl/gv/pdbfinder/</reference>",
					"		<filelink>http://mrs.cmbi.ru.nl/mrs-3/entry.do?db=pdbfinder2&amp;id=</filelink>",
					"		<regex>.*/PDBFIND2?\\.TXT</regex>",
					"		<offset>-1</offset>",
					"	</database>",
					"	<database>",
					"		<name>PDBREPORT</name>",
					"		<reference>http://swift.cmbi.ru.nl/gv/pdbreport/</reference>",
					"		<filelink>http://www.cmbi.ru.nl/pdbreport/cgi-bin/nonotes?PDBID=</filelink>",
					"		<regex>.*pdbreport.*/[\\d\\w]{4}</regex>",
					"		<offset>0</offset>",
					"	</database>",
					"	<database>",
					"		<name>PDB_REDO</name>",
					"		<reference>http://swift.cmbi.ru.nl/pdb_redo/</reference>",
					"		<filelink>http://swift.cmbi.ru.nl/pdb_redo/</filelink>",
					"		<regex>.*pdb_redo.*/[\\d\\w]{4}</regex>",
					"		<offset>0</offset>",
					"	</database>",
					"	<database>",
					"		<name>STRUCTUREFACTORS</name>",
					"		<reference>http://www.pdb.org/</reference>",
					"		<filelink>http://www.pdb.org/pdb/download/downloadFile.do?fileFormat=STRUCTFACT&amp;compression=NO&amp;structureId=</filelink>",
					"		<regex>.*/r[\\d\\w]{4}sf\\.ent</regex>",
					"		<offset>1</offset>", "	</database>", "</databases>" };
			new BufferedWriter(new FileWriter(this.file)).close();
			BufferedWriter bf = new BufferedWriter(new FileWriter(this.file,
					true));
			for (String line : lines) {
				bf.write(line);
				bf.newLine();
			}
			bf.flush();
			bf.close();
			CommandlineWriter.cmd
					.printProgress("Created new " + this.file.getName()
							+ " file with default settings",
							"Please confirm settings in "
									+ this.file.getAbsolutePath());
		} catch (IOException e) {
		}

	}

	public Database[] getDatabases() {
		Set<Database> databases = new HashSet<Database>();

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(this.file);
			doc.getDocumentElement().normalize();

			NodeList nodeLst = doc.getElementsByTagName("database");
			for (int s = 0; s < nodeLst.getLength(); s++) {
				Node fstNode = nodeLst.item(s);
				if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
					Element fstElmnt = (Element) fstNode;
					NodeList subNodeLst;

					subNodeLst = ((Element) fstElmnt.getElementsByTagName(
							"name").item(0)).getChildNodes();
					String name = subNodeLst.item(0).getNodeValue();

					subNodeLst = ((Element) fstElmnt.getElementsByTagName(
							"regex").item(0)).getChildNodes();
					String regex = subNodeLst.item(0).getNodeValue();

					subNodeLst = ((Element) fstElmnt.getElementsByTagName(
							"offset").item(0)).getChildNodes();
					int offset = Integer.valueOf(subNodeLst.item(0)
							.getNodeValue());

					Database filetype = new Database(name, regex, offset);

					if ((Element) fstElmnt.getElementsByTagName("reference")
							.item(0) != null) {
						subNodeLst = ((Element) fstElmnt.getElementsByTagName(
								"reference").item(0)).getChildNodes();
						if (subNodeLst.item(0) != null) {
							String reference = subNodeLst.item(0)
									.getNodeValue();
							filetype.setReference(reference);
						}
					}

					if ((Element) fstElmnt.getElementsByTagName("filelink")
							.item(0) != null) {
						subNodeLst = ((Element) fstElmnt.getElementsByTagName(
								"filelink").item(0)).getChildNodes();
						if (subNodeLst.item(0) != null) {
							String filelink = subNodeLst.item(0).getNodeValue();
							filetype.setFilelink(filelink);
						}
					}

					databases.add(filetype);
				}
			}
		} catch (ParserConfigurationException e) {
			CommandlineWriter.cmd.printFatalError("Reading "
					+ this.file.getName() + " failed", "Verify "
					+ this.file.getName() + " is present and welformed.");
		} catch (SAXException e) {
			CommandlineWriter.cmd.printFatalError("Reading "
					+ this.file.getName() + " failed", "Verify "
					+ this.file.getName() + " is present and welformed.");
		} catch (IOException e) {
			CommandlineWriter.cmd.printFatalError("Reading "
					+ this.file.getName() + " failed", "Verify "
					+ this.file.getName() + " is present and welformed.");
		}

		return databases.toArray(new Database[0]);
	}
}
