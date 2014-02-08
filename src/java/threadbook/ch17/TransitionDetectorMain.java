package threadbook.ch17;

public class TransitionDetectorMain extends Object {
	private static Thread startTrueWaiter(
				final TransitionDetector td,
				String name
			) {

		Runnable r = new Runnable() {
				public void run() {
					try {
						while ( true ) {
							print("about to wait for false-to-" +
								"true transition, td=" + td);

							td.waitForFalseToTrueTransition();

							print("just noticed for false-to-" +
								"true transition, td=" + td);
						}
					} catch ( InterruptedException ix ) {
						return;
					}
				}
			};

		Thread t = new Thread(r, name);
		t.start();

		return t;
	}

	private static Thread startFalseWaiter(
				final TransitionDetector td,
				String name
			) {

		Runnable r = new Runnable() {
				public void run() {
					try {
						while ( true ) {
							print("about to wait for true-to-" +
								"false transition, td=" + td);

							td.waitForTrueToFalseTransition();

							print("just noticed for true-to-" +
								"false transition, td=" + td);
						}
					} catch ( InterruptedException ix ) {
						return;
					}
				}
			};

		Thread t = new Thread(r, name);
		t.start();

		return t;
	}

	private static void print(String msg) {
		String name = Thread.currentThread().getName();
		System.err.println(name + ": " + msg);
	}

	public static void main(String[] args) {
		try {
			TransitionDetector td = 
					new TransitionDetector(false);

			Thread threadA = startTrueWaiter(td, "threadA");
			Thread threadB = startFalseWaiter(td, "threadB");

			Thread.sleep(200);
			print("td=" + td + ", about to set to 'false'");
			td.setValue(false);

			Thread.sleep(200);
			print("td=" + td + ", about to set to 'true'");
			td.setValue(true);

			Thread.sleep(200);
			print("td=" + td + ", about to pulse value");
			td.pulseValue();

			Thread.sleep(200);
			threadA.interrupt();
			threadB.interrupt();
		} catch ( InterruptedException x ) {
			x.printStackTrace();
		}
	}
}
