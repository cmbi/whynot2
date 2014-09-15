package nl.ru.cmbi.whynot.backup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nl.ru.cmbi.whynot.hibernate.CommentRepo;
import nl.ru.cmbi.whynot.model.Annotation;
import nl.ru.cmbi.whynot.model.Comment;
import nl.ru.cmbi.whynot.util.SpringUtil;

@Service
@Transactional
public class Backuper {
	private static final Logger	log	= LoggerFactory.getLogger(Backuper.class);

	public static void main(String[] args) throws BeansException, IOException {
		log.info("Backup start.");
		SpringUtil.getContext().getBean(Backuper.class).backup();
		log.info("Backup done.");
	}

	@Autowired
	private CommentRepo	comdao;

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
		log.info("Backup complete: " + file.getAbsolutePath());
	}
}
