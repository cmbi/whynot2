package inout;

public class Progress {
	private String action = "";

	private int noSucces = 0;

	private int noFailed = 0;

	public Progress(String action) {
		this.action = action;
	}

	public int increaseNoSucces() {
		this.noSucces++;
		return this.noSucces;
	}

	public int increaseNoFailed() {
		this.noFailed++;
		return this.noFailed;
	}

	private int getNoCompleted() {
		return this.noSucces + this.noFailed;
	}

	public void printAction() {
		CommandlineWriter.cmd.printProgress(this.action, "");
	}

	public void printProgress() {
		CommandlineWriter.cmd.printProgress("Completed", this.getNoCompleted()
				+ " files");
	}

	public void printResults() {
		CommandlineWriter.cmd.printProgress("Done", this.noSucces
				+ " succeeded, " + this.noFailed + " failed, "
				+ this.getNoCompleted() + " files total.");
	}
}
