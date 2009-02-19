package inout;

public class ProgressWriter implements Runnable {
	private boolean running = false;

	private Thread thread = null;

	private Progress progress;

	private static final int sleeptime = 60000;

	public ProgressWriter(Progress progress) {
		this.progress = progress;

		this.thread = new Thread(this);
		this.thread.setDaemon(true);
		this.running = true;
		this.thread.start();
	}

	public void run() {
		this.progress.printAction();
		while (this.running)
			try {
				Thread.sleep(ProgressWriter.sleeptime);
				this.progress.printProgress();
			} catch (InterruptedException e) {
				this.running = false;
				this.progress.printResults();
			}
	}

	public void stop() {
		this.thread.interrupt();
		try {
			this.thread.join();
		} catch (InterruptedException e) {
		}
		this.thread = null;
	}

}
