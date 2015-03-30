package nl.ru.cmbi.whynot.search;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;
import org.wicketstuff.annotation.mount.MountPath;

import nl.ru.cmbi.whynot.hibernate.EntryRepo;
import nl.ru.cmbi.whynot.home.HomePage;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Entry;
import nl.ru.cmbi.whynot.panels.AnnotationPanel;
import nl.ru.cmbi.whynot.panels.FilePanel;

@MountPath("search")
public class ResultsPage extends HomePage {
	@SpringBean
	protected EntryRepo	entrydao;

	public ResultsPage(final PageParameters parameters) {
		if (parameters.getNamedKeys().contains("pdbid")) {
			List<StringValue> pdbids = parameters.getValues("pdbid");
			RepeatingView rv = new RepeatingView("resultslist");
			for (StringValue pdbid : pdbids)
				if (entrydao.contains(pdbid.toString()))
					rv.add(new ResultFragment(rv.newChildId(), pdbid.toString()));
				else
					warn("No data available for PDBID \"" + pdbid + "\"");
			add(rv);
		}
		else {
			error("No value specified for parameter pdbid.");
			add(new Label("resultslist"));
		}

	}

	public class ResultFragment extends Fragment {
		public ResultFragment(final String id, final String pdbid) {
			super(id, "resultfragment", ResultsPage.this);
			add(new Label("pdbid", pdbid));
			ListView<Databank> lv = new ListView<Databank>("databanklist", databankdao.findAll()) {
				@Override
				protected void populateItem(final ListItem<Databank> item) {
					Databank db = item.getModelObject();
					item.add(new Label("databank", db.getName()));
					Entry entry = entrydao.findByDatabankAndPdbid(db, pdbid);
					if (entry != null && entry.getFile() != null)
						item.add(new FilePanel("result", entry));
					else
					{
						if (entry != null && !entry.getAnnotations().isEmpty())
							item.add(new AnnotationPanel("result", entry));
						else {
							Databank par = db.getParent();
							Entry parentEntry = entrydao.findByDatabankAndPdbid(par, pdbid);
							
							// As per Gert: Do not show blanks, but display not available & dependency
							StringBuilder msg = new StringBuilder("Not available");
							
							if( parentEntry==null || parentEntry.getFile() == null )
								msg.append(", depends on: ").append(par.getName());
							
							Label lbl = new Label("result", msg.toString());
							lbl.add(new AttributeModifier("class", "annotation"));
							item.add(lbl);

							// TODO Feature requests Robbie:
							// If source was created / added less than a week ago: "Too new / pending"
							// If source is available, but older than a week: "Pending"
							// If source is not available: "Not available, depends on XYZ"
						}
					}
				}
			};
			add(lv);
		}
	}
}
