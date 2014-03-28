package nl.ru.cmbi.whynot.comment;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.util.*;

import nl.ru.cmbi.whynot.model.Comment;
import nl.ru.cmbi.whynot.model.Entry;

import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class CommentTreeProvider implements ITreeProvider<String> {
	
	private Map<Comment, List<Entry>> commentMap; // must be serializable
	
	public static final String separator=":";
	
	public CommentTreeProvider(Map<Comment, List<Entry>> commentMap) {
		
		this.commentMap = commentMap;
	}

	@Override
	public void detach() {
	}

	@Override
	public Iterator<? extends String> getChildren(String comment) {
		
		// Prevent the formation of branches where there's only one child !
		
		Map<String,String> children = new HashMap<String,String>();
		for(Comment c : this.commentMap.keySet()) {
			
			String	fullText = c.getText(),
					prefix=comment + this.separator;
			
			if (fullText.startsWith(prefix)) {
				
				int i=fullText.indexOf(this.separator, prefix.length());
				if(i==-1) i=fullText.length();
				
				String childText=fullText.substring(0,i);
				
				if(children.containsKey(childText)) // there's already a child with this prefix, means we must make a branch
					
					children.put(childText, childText);
				
				else // first child with this prefix, assume it's the only one for now ..
					
					children.put(childText, fullText);
			}
		}		
		
		return children.values().iterator();
	}

	@Override
	public Iterator<? extends String> getRoots() {
		
		// Prevent the formation of branches where there's only one child !
		
		Map<String,String> rootNames = new HashMap<String,String>();
		for(Comment c : this.commentMap.keySet()) {

			String fullText = c.getText();
			
			String rootText=fullText;
			
			if(fullText.contains(separator)) {
				
				rootText = fullText.split(this.separator)[0];
			}
			
			if(rootNames.containsKey(rootText)) // there's already a child with this root, means we must make a branch
				
				rootNames.put(rootText,rootText);
			
			else // first child with this root, assume it's the only one for now ..
				
				rootNames.put(rootText,fullText);
		}
		
		return rootNames.values().iterator();
	}

	@Override
	public boolean hasChildren(String comment) {
		
		for(Comment c : this.commentMap.keySet()) {
			
			String	fullText = c.getText(),
					prefix=comment + this.separator;
			
			if (fullText.startsWith(prefix)) {
				
				return true;
			}
		}
		return false;
	}

	@Override
	public IModel<String> model(String comment) {
		
		return new Model<String>(comment);
	}

}
