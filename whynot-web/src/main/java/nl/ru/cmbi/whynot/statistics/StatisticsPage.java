package nl.ru.cmbi.whynot.statistics;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.annotation.mount.MountPath;

import nl.ru.cmbi.whynot.hibernate.*;
import nl.ru.cmbi.whynot.home.HomePage;
import nl.ru.cmbi.whynot.model.Annotation;
import nl.ru.cmbi.whynot.model.File;

@MountPath("statistics")
public class StatisticsPage extends HomePage {
	@SpringBean
	private AnnotationRepo	anndao;
	@SpringBean
	private CommentRepo		comdao;
	@SpringBean
	private DatabankRepo		dbdao;
	@SpringBean
	private EntryRepo		entdao;
	@SpringBean
	private FileRepo			filedao;

	SimpleDateFormat		sdf	= new SimpleDateFormat("dd/MM/yyyy HH:mm");

	public StatisticsPage() {
		// Counts
		add(new Label("annotations", "" + anndao.count()));
		add(new Label("comments", "" + comdao.count()));
		add(new Label("databanks", "" + dbdao.count()));
		add(new Label("entries", "" + entdao.count()));
		add(new Label("files", "" + filedao.count()));

		// Most recent annotations
		ListView<Annotation> lva = new ListView<Annotation>("annotation", anndao.getRecent()) {
			@Override
			protected void populateItem(final ListItem<Annotation> item) {
				Annotation ann = item.getModelObject();
				item.add(new Label("comment", ann.getComment().getText()).setEscapeModelStrings(false));
				item.add(new Label("databank", ann.getEntry().getDatabank().getName()));
				item.add(new Label("pdbid", ann.getEntry().getPdbid()));
				item.add(new Label("timestamp", sdf.format(new Date(ann.getTimestamp()))));
			}
		};
		add(lva);

		// Most recent files
		ListView<File> lvf = new ListView<File>("file", filedao.getRecent()) {
			@Override
			protected void populateItem(final ListItem<File> item) {
				File file = item.getModelObject();
				String path = file.getPath();
				item.add(new Label("path", path));
				item.add(new Label("timestamp", sdf.format(new Date(file.getTimestamp()))));
			}
		};
		add(lvf);

	}

}
