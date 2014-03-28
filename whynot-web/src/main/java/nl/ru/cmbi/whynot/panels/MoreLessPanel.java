package nl.ru.cmbi.whynot.panels;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

/**
 * @author cbaakman
 * 
 * A list of entries that can be extended by clicking.
 *
 * @param <T>
 */
public class MoreLessPanel<T> extends Panel {
	
	private List<T> shownList = new ArrayList<T>();
	private String toggleText = "";
	
	public MoreLessPanel(final String id, final List<T> entries, final int maxInitiallyShown) {
		
		super(id);
		
		final MarkupContainer entryContainer=new WebMarkupContainer("entry-container");
		
		boolean matchesFit = entries.size() < maxInitiallyShown;
		
		if(matchesFit) {
			
			this.shownList.addAll( entries );
			
		} else {
			
			this.shownList.addAll( entries.subList(0, maxInitiallyShown) );
			this.toggleText = "<more>";
		}
		
		entryContainer.add(new ListView<T>("entrylist", new PropertyModel<List<T>>(this,"shownList")) {
			@Override
			protected void populateItem(ListItem<T> entryItem) {
				entryItem.add(new Label("entry", entryItem.getModelObject().toString()));
			}
		});
		
		entryContainer.setOutputMarkupId(true);
		entryContainer.setOutputMarkupPlaceholderTag(true);
		
		Link togglelink = new AjaxFallbackLink("toggle-link")
        {
			private boolean expanded=false;
			
            @Override
            public void onClick(final AjaxRequestTarget target)
            {
                    // toggles visibility when clicked:

    				MoreLessPanel.this.shownList.clear();
    				
            		if(this.expanded) {
            			
            			MoreLessPanel.this.shownList.addAll(entries.subList(0, maxInitiallyShown));
	            		
            			MoreLessPanel.this.toggleText = "<more>";
            			
            		} else {
            			
            			MoreLessPanel.this.shownList.addAll(entries);
	            		
            			MoreLessPanel.this.toggleText = "<less>";
            		}
            		this.expanded=!this.expanded;

                    // update the webmarkupcontainer:
                    if(target!=null)  {
                    	
                    	target.add(entryContainer);
                    	target.add(this);
                    }
            }
        };
        togglelink.add( new Label("toggle-text",new PropertyModel<List<T>>(this,"toggleText")));
        togglelink.setVisible( !matchesFit );
        add( togglelink );

		add( entryContainer );
	}

}
