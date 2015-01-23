package nl.ru.cmbi.whynot.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.ru.cmbi.whynot.model.Databank;

public class Utils {

	public static Pattern pWHATIFLIST = Pattern.compile("^WHATIF_([A-Z]+)_([a-z0-9]+)$"),
							pSCENE = Pattern.compile("^([A-Z]+)_SCENES_([a-z0-9]+)$");
	
	public static String hierarchyName(Databank db) {
		
		String parentName = db.getParent().getName().replace("PDB_REDO", "REDO");
		
		Matcher mWHATIFLIST = Utils.pWHATIFLIST.matcher(db.getName()),
				mParentWHATIFLIST = Utils.pWHATIFLIST.matcher(parentName),
				mSCENE = Utils.pSCENE.matcher(db.getName());
		
		if(mWHATIFLIST.matches() && mWHATIFLIST.group(1).equals(parentName))
			
			return parentName+":"+mWHATIFLIST.group(2).toUpperCase();
		
		else if(mSCENE.matches() && mParentWHATIFLIST.matches()
				&& mSCENE.group(1).equals(mParentWHATIFLIST.group(1))
				&& mSCENE.group(2).equals(mParentWHATIFLIST.group(2)))
			
			return "scenes";
		else
			return db.getName();
	}
	
	public static String title(Databank db) {

		String inputType;
		Matcher mSCENE = Utils.pSCENE.matcher(db.getName()),
				mWHATIFLIST = Utils.pWHATIFLIST.matcher(db.getName());
		
		if(mSCENE.matches()) {
			
			inputType=mSCENE.group(1).replace("REDO", "PDB_REDO");
			
			return inputType+":"+mSCENE.group(2).toUpperCase()+":SCENES";
		}
		else if(mWHATIFLIST.matches()) {

			inputType=mWHATIFLIST.group(1).replace("REDO", "PDB_REDO");
			
			return inputType+":"+mWHATIFLIST.group(2).toUpperCase()+":WHATIF";
		}
		else
			return db.getName();
	}
}
