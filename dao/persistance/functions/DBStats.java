package persistance.functions;

public class DBStats {
	private int indball;
	private int indbcorrect;
	private int indbincorrect;

	private int notindball;
	private int notindbcorrect;
	private int notindbincorrect;

	public DBStats(int[] results) {
		this.indball = results[0];
		this.indbcorrect = results[1];
		this.indbincorrect = results[2];

		this.notindball = results[3];
		this.notindbcorrect = results[4];
		this.notindbincorrect = results[5];
	}

	public int getIndball() {
		return indball;
	}

	public int getIndbcorrect() {
		return indbcorrect;
	}

	public int getIndbincorrect() {
		return indbincorrect;
	}

	public int getNotindball() {
		return notindball;
	}

	public int getNotindbcorrect() {
		return notindbcorrect;
	}

	public int getNotindbincorrect() {
		return notindbincorrect;
	}
}
