package nl.ru.cmbi.whynot.entries;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Data;

import nl.ru.cmbi.whynot.model.Entry;

import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class CommentTreeProvider implements ITreeProvider<CommentTreeProvider.CommentTreeNode> {

	@Data
	public class CommentTreeNode implements Serializable {
		
		private String title;
		private List<String> members;
		
		private List<CommentTreeNode> children; 
		
		public CommentTreeNode(String title) {
			
			this.title=new String(title);
			this.members=new ArrayList();
			this.children=new ArrayList<CommentTreeNode>();
		}
		
		public boolean equals(CommentTreeNode other){
			
			return other.title.equals(this.title);
		}
	}
	
	private List<CommentTreeNode> buildTrees( String atRootString, Collection<String> comments ) {

		Map<String,CommentTreeNode> rootMap = new HashMap<String,CommentTreeNode>();
		
		// Get the roots at this depth level:
		
		final String prefix = atRootString + ":";
		
		for(String c : comments) {

			String	fullText = removeTags( c ), 
					rootText = fullText;
			
			if(prefix.length()>1 && !fullText.startsWith(prefix)) {
				continue;
			}
			
			int i=fullText.indexOf(this.separator,prefix.length());
			if(i==-1) {
				
				rootText = fullText;
				
			} else {
				
				rootText = fullText.substring(0,i);
			}
			
			if(!rootMap.containsKey(rootText))
				rootMap.put(rootText, new CommentTreeNode( rootText ));
				
			rootMap.get(rootText).getMembers().add(c);
		}
		
		// Do the recursive part:
		
		for(CommentTreeNode n : rootMap.values()) {
			
			if( n.getMembers().size()>1 ) {
			
				Collection<CommentTreeNode> children = buildTrees( n.getTitle(), n.getMembers() );
			
				n.getChildren().addAll(children);
			}
		}
		
		return new ArrayList<CommentTreeNode>(rootMap.values());
	}
	
	public static final String separator=":",
	
			pTagEnclosedText = "\\<(\\w+)(\\s+.+?|\\s+\".+?\")*\\>(.*)\\<\\/\\1\\>",
			pSingleTag = "\\<\\w+(\\s+\\w+\\=.+)*\\/\\>";
	
	public static String removeTags(String text) {

		final Pattern doubleTagP = Pattern.compile(pTagEnclosedText);
		Matcher m;
		do {
			
			text = text.replaceAll(pTagEnclosedText, "$3") ;
			
			m = doubleTagP.matcher(text) ;
			
		} while(m.find());
		
		
		final Pattern singleTagP = Pattern.compile(pSingleTag);
		
		do {
			
			text = text.replaceAll(pSingleTag, "") ;
			
			m = singleTagP.matcher(text) ;
			
		} while(m.find());
		
		
		return text;
	}
	
	private Collection<CommentTreeNode> roots ; // must be serializable
	
	public CommentTreeProvider(Collection<String> comments) {
		
		this.roots = buildTrees( "", comments );
	}

	public void detach() {
	}

	public Iterator<? extends CommentTreeNode> getChildren(CommentTreeNode node) {
		
		return node.getChildren().iterator();
	}

	public Iterator<? extends CommentTreeNode> getRoots() {
		
		return this.roots.iterator();
	}

	public boolean hasChildren(CommentTreeNode node) {
		
		return node.getChildren().size()>0;
	}

	public IModel<CommentTreeNode> model(CommentTreeNode node) {
		
		return new Model<CommentTreeNode>(node);
	}

}
