package nl.ru.cmbi.whynot.statistics;

import java.text.SimpleDateFormat;
import java.util.Date;

import nl.ru.cmbi.whynot.home.HomePage;
import nl.ru.cmbi.whynot.mongo.DatabankRepo;
import nl.ru.cmbi.whynot.mongo.EntryRepo;
import nl.ru.cmbi.whynot.model.Entry;
import nl.ru.cmbi.whynot.model.Entry.File;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("statistics")
public class StatisticsPage extends HomePage {
	@SpringBean
	private DatabankRepo dbdao;
	@SpringBean
	private EntryRepo entdao;

	SimpleDateFormat		sdf	= new SimpleDateFormat("dd/MM/yyyy HH:mm");

	public StatisticsPage() {
		// Counts
		add(new Label("annotations", "" + entdao.countAllAnnotated()));
		add(new Label("comments", "" + entdao.listComments().size()));
		add(new Label("databanks", "" + dbdao.countAll()));
		add(new Label("entries", "" + entdao.countAll()));
		add(new Label("files", "" + entdao.countAllPresent()));

		// Most recent annotations
		ListView<Entry> lva = new ListView<Entry>("annotation", entdao.getRecentlyAnnotated()) {
			@Override
			protected void populateItem(final ListItem<Entry> item) {
				Entry entry = item.getModelObject();
				item.add(new Label("comment", entry.getComment()).setEscapeModelStrings(false));
				item.add(new Label("databank", entry.getDatabankName()));
				item.add(new Label("pdbid", entry.getPdbid()));
				item.add(new Label("timestamp", sdf.format(new Date(entry.getLastModified()))));
			}
		};
		add(lva);

		// Most recent files
		ListView<File> lvf = new ListView<File>("file", entdao.getRecentFiles()) {
			@Override
			protected void populateItem(final ListItem<File> item) {
				File file = item.getModelObject();
				String path = file.getPath();
				item.add(new Label("path", path));
				item.add(new Label("timestamp", sdf.format(new Date(file.getMtime()))));
			}
		};
		add(lvf);

	}

}
