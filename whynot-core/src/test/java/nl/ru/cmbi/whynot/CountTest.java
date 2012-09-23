package nl.ru.cmbi.whynot;

import java.io.IOException;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.CommentDAO;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.DatabankDAO;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.whynot.model.Annotation;
import nl.ru.cmbi.whynot.model.Comment;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Databank.CrawlType;
import nl.ru.cmbi.whynot.model.Entry;
import nl.ru.cmbi.whynot.model.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring.xml" })
public class CountTest {
	private static final int	factor	= 100;

	@Autowired
	private EntryDAO			entdao;

	@Autowired
	private DatabankDAO			dbdao;

	@Autowired
	private CommentDAO			commentdao;

	@Rule
	public TemporaryFolder		folder	= new TemporaryFolder();

	@Before
	public void setupDatabase() throws IOException {
		// Create databases
		Databank pdb = new Databank("PDB", CrawlType.LINE, "[a-z0-9]{4}", "#", "#${PDBID}");
		Databank dssp = new Databank("DSSP", pdb, CrawlType.LINE, "[a-z0-9]{4}", "#", "#${PDBID}");

		// Store in our in mem database
		dbdao.makePersistent(pdb);
		dbdao.makePersistent(dssp);

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
		commentdao.makePersistent(comment);
		Entry entry = new Entry(dssp, Integer.toString(0));
		dssp.getEntries().add(entry);
		Annotation annotation = new Annotation(comment, entry, System.currentTimeMillis());
		entry.getAnnotations().add(annotation);

		// Sanity check
		Assert.assertEquals(22 * factor + 1 + 1, entdao.countAll());
	}

	@Test
	@Transactional
	public void getXXXcount() {
		Databank dssp = dbdao.findByName("DSSP");

		// Check the various counts
		long countPresent = entdao.countPresent(dssp);
		long countValid = entdao.countValid(dssp);
		long countObsolete = entdao.countObsolete(dssp);
		long countMissing = entdao.countMissing(dssp);
		long countAnnotated = entdao.countAnnotated(dssp);
		long counUnannotated = entdao.counUnannotated(dssp);

		Assert.assertEquals(12 * factor, countPresent);
		Assert.assertEquals(8 * factor, countValid);
		Assert.assertEquals(4 * factor, countObsolete);
		Assert.assertEquals(2 * factor + 1, countMissing);
		Assert.assertEquals(1, countAnnotated);
		Assert.assertEquals(2 * factor, counUnannotated);

		Assert.assertEquals(countPresent, entdao.getPresent(dssp).size());
		Assert.assertEquals(countValid, entdao.getValid(dssp).size());
		Assert.assertEquals(countObsolete, entdao.getObsolete(dssp).size());
		Assert.assertEquals(countMissing, entdao.getMissing(dssp).size());
		Assert.assertEquals(countAnnotated, entdao.getAnnotated(dssp).size());
		Assert.assertEquals(counUnannotated, entdao.getUnannotated(dssp).size());
	}
}
