package inout.database;

import inout.CommandlineWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class JDBCXMLReader {
	private File file;

	public JDBCXMLReader() {
		this.file = new File("jdbc.xml");
		if (!this.file.exists())
			this.writeDefaultXMLFile();
	}

	private void writeDefaultXMLFile() {
		try {
			String[] lines = { "<?xml version='1.0'?>", "<database>",
					"	<connection-settings>",
					"		<driver>org.postgresql.Driver</driver>",
					"		<protocol>jdbc:postgresql:</protocol>",
					"		<host>131.174.88.34</host>",
					"		<port>5432</port>",
					"		<name>whynot</name>", "		<user>whynot_owner</user>",
					"		<pass/>", "	</connection-settings>",
					"</database>" };
			new BufferedWriter(new FileWriter(this.file)).close();
			BufferedWriter bf = new BufferedWriter(new FileWriter(this.file,
					true));
			for (String line : lines) {
				bf.write(line);
				bf.newLine();
			}
			bf.flush();
			bf.close();
		} catch (IOException e) {
		}
	}

	public Properties getProperties() {
		try {
			Properties properties = new Properties();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(this.file);
			doc.getDocumentElement().normalize();
			NodeList nodeLst = doc.getElementsByTagName("connection-settings");

			for (int s = 0; s < nodeLst.getLength(); s++) {
				Node fstNode = nodeLst.item(s);
				if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
					Element fstElmnt = (Element) fstNode;
					for (String setting : new String[] { "driver", "protocol",
							"host", "port", "name", "user", "pass" }) {
						NodeList subNodeLst = ((Element) fstElmnt
								.getElementsByTagName(setting).item(0))
								.getChildNodes();
						if (0 < subNodeLst.getLength())
							properties.setProperty(setting, subNodeLst.item(0)
								.getNodeValue());
						else
							properties.setProperty(setting, "");
					}
				}
			}
			return properties;

		} catch (ParserConfigurationException e) {
			CommandlineWriter.cmd.printFatalError("Malformed jdbc.xml", "Could not get jdbc connection settings");
		} catch (SAXException e) {
			CommandlineWriter.cmd.printFatalError("Malformed jdbc.xml", "Could not get jdbc connection settings");
		} catch (IOException e) {
			CommandlineWriter.cmd.printFatalError("Unreadable jdbc.xml", "Could not get jdbc connection settings");
		}
		return null;
	}
}
