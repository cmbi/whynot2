package nl.ru.cmbi.whynot.backup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.CommentDAO;
import nl.ru.cmbi.whynot.model.Annotation;
import nl.ru.cmbi.whynot.model.Comment;
import nl.ru.cmbi.whynot.util.SpringUtil;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class Backuper {
	public static void main(String[] args) throws BeansException, IOException {
		Logger.getLogger(Backuper.class).info("Backup start.");
		((Backuper) SpringUtil.getContext().getBean("backuper")).backup();
		Logger.getLogger(Backuper.class).info("Backup done.");
	}

	@Autowired
	private CommentDAO	comdao;

	public void backup() throws IOException {
		File dirBackup = new File("backup/");
		//Make sure download directory exist
		if (!dirBackup.isDirectory() && !dirBackup.mkdir())
			throw new FileNotFoundException(dirBackup.getAbsolutePath());

		File file = new File("backup/" + System.currentTimeMillis() + ".backup");
		PrintWriter pw = new PrintWriter(new FileWriter(file));
		for (Comment com : comdao.findAll()) {
			pw.println("COMMENT: " + com.getText());
			for (Annotation ann : com.getAnnotations()) {
				pw.print(ann.getEntry().getDatabank().getName());
				pw.print(',');
				pw.println(ann.getEntry().getPdbid());
			}
		}
		pw.close();
		Logger.getLogger(getClass()).info("Backup complete: " + file.getAbsolutePath());
	}
}
