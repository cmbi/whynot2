import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;

public class AnnotateStructureFactorsOnExperimentTypeTest {

	@Test
	public void test() throws Exception {
		Scanner scn = new Scanner(new File("src/test/resources/STRUCTUREFACTORS_Unannotated.txt"));
		List<String> args = new ArrayList<String>();
		while (scn.hasNextLine())
			args.add(scn.nextLine());
		AnnotateStructureFactorsOnExperimentType.main(args.toArray(new String[0]));
	}
}
