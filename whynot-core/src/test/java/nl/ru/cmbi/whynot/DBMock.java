package nl.ru.cmbi.whynot;

import java.io.IOException;

import nl.ru.cmbi.whynot.hibernate.CommentRepo;
import nl.ru.cmbi.whynot.hibernate.DatabankRepo;
import nl.ru.cmbi.whynot.hibernate.EntryRepo;
import nl.ru.cmbi.whynot.model.Annotation;
import nl.ru.cmbi.whynot.model.Comment;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Databank.CrawlType;
import nl.ru.cmbi.whynot.model.Entry;
import nl.ru.cmbi.whynot.model.File;
import nl.ru.cmbi.whynot.util.SpringUtil;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DBMock {
	public static void main(final String[] args) throws Exception {
		DBMock dbMock = SpringUtil.getContext().getBean(DBMock.class);
		dbMock.setupDatabase(1);
	}

	@Rule
	public TemporaryFolder	folder	= new TemporaryFolder();

	@Autowired
	private DatabankRepo		dbdao;

	@Autowired
	private CommentRepo		commentdao;

	@Autowired
	private EntryRepo		entdao;

	@Transactional
	public void setupDatabase(final int factor) throws IOException {
		// Create databases
		Databank pdb = new Databank("PDB", CrawlType.LINE, "[a-z0-9]{4}", "#", "#${PDBID}");
		Databank dssp = new Databank("DSSP", pdb, CrawlType.LINE, "[a-z0-9]{4}", "#", "#${PDBID}");

		// Store in our in mem database
		dbdao.save(pdb);
		dbdao.save(dssp);

		// Add some fake entries, that all reference the same file per databank
		File pdbfile = new File(folder.newFile());
		for (int i = 0; i <= 10 * factor; i++) {
			Entry entry = new Entry(pdb, Integer.toString(i));
			pdb.getEntries().add(entry);
			entry.setFile(pdbfile);
		}
		File dsspfile = new File(folder.newFile());
		for (int i = 2 * factor + 1; i <= 14 * factor; i++) {
			Entry entry = new Entry(dssp, Integer.toString(i));
			dssp.getEntries().add(entry);
			entry.setFile(dsspfile);
		}
		// Add comments for the first factor items
		Comment comment = new Comment("Some comment");
		commentdao.save(comment);
		Entry entry = new Entry(dssp, Integer.toString(0));
		dssp.getEntries().add(entry);
		Annotation annotation = new Annotation(comment, entry, System.currentTimeMillis());
		entry.getAnnotations().add(annotation);

		// Sanity check
		Assert.assertEquals(22 * factor + 1 + 1, entdao.count());
	}
}
