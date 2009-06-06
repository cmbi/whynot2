package nl.ru.cmbi.whynot.backup;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import nl.ru.cmbi.whynot.hibernate.SpringUtil;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.CommentDAO;
import nl.ru.cmbi.whynot.model.Annotation;
import nl.ru.cmbi.whynot.model.Comment;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;

public class Backuper {
	public static void main(String[] args) throws BeansException, IOException {
		((Backuper) SpringUtil.getContext().getBean("backuper")).backup();
	}

	@Autowired
	private CommentDAO	comdao;

	private void backup() throws IOException {
		PrintWriter pw = new PrintWriter(new FileWriter(System.currentTimeMillis() + ".backup"));
		for (Comment com : comdao.findAll()) {
			pw.println(com.getText());
			for (Annotation ann : com.getAnnotations()) {
				pw.print(ann.getEntry().getDatabank().getName());
				pw.print(',');
				pw.println(ann.getEntry().getPdbid());
			}
		}
		pw.close();
	}
}
