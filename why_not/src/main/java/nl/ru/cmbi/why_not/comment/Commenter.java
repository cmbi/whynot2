package nl.ru.cmbi.why_not.comment;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.ParseException;

import nl.ru.cmbi.why_not.hibernate.DAOFactory;
import nl.ru.cmbi.why_not.hibernate.SpringUtil;
import nl.ru.cmbi.why_not.hibernate.GenericDAO.CommentDAO;
import nl.ru.cmbi.why_not.model.Comment;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class Commenter {
	private static String		append			= ".done";
	private static FileFilter	commentFilter	= new FileFilter() {
													@Override
													public boolean accept(File pathname) {
														return pathname.isFile() && !pathname.getName().contains(append);
													}
												};

	public static void main(String[] args) throws IOException, ParseException {
		File dirComments = new File("comment/");
		File dirUncomments = new File("uncomment/");

		//Make sure comment directories exist
		if (!dirComments.isDirectory() && !dirComments.mkdir())
			throw new FileNotFoundException(dirComments.getAbsolutePath());
		if (!dirUncomments.isDirectory() && !dirUncomments.mkdir())
			throw new FileNotFoundException(dirUncomments.getAbsolutePath());

		Commenter commenter = (Commenter) SpringUtil.getContext().getBean("commenter");
		//Comment / Uncomment all files in directories
		for (File file : dirComments.listFiles(commentFilter))
			commenter.comment(file);
		for (File file : dirUncomments.listFiles(commentFilter))
			commenter.uncomment(file);

		//Cleanup unused Comments
		commenter.cleanup();
	}

	@Autowired
	private DAOFactory	factory;

	public void comment(File file) throws IOException, ParseException {
		String line = readFirstLine(file);
		if (line.startsWith("PDBID"))
			new LegacyCommenter(factory).comment(file);
		else
			if (line.startsWith("COMMENT"))
				new NewCommenter(factory).comment(file);

		//Rename file to prevent rerunning
		file.renameTo(new File(file.getAbsolutePath() + Commenter.append));
		Logger.getLogger(NewCommenter.class).info("Completed file" + file.getAbsolutePath());
	}

	public void uncomment(File file) throws IOException, ParseException {
		String line = readFirstLine(file);
		if (line.startsWith("PDBID"))
			new LegacyCommenter(factory).uncomment(file);
		else
			if (line.startsWith("COMMENT"))
				new NewCommenter(factory).uncomment(file);

		//Rename file to prevent rerunning
		file.renameTo(new File(file.getAbsolutePath() + Commenter.append));
		Logger.getLogger(NewCommenter.class).info("Completed file" + file.getAbsolutePath());
	}

	private String readFirstLine(File file) throws FileNotFoundException, IOException {
		LineNumberReader lnr = new LineNumberReader(new FileReader(file));
		String line = lnr.readLine();
		lnr.close();
		return line;
	}

	public void cleanup() {
		CommentDAO comdao = factory.getCommentDAO();
		for (Comment comment : comdao.findAll())
			if (comment.getAnnotations().isEmpty())
				comdao.makeTransient(comment);
	}
}
