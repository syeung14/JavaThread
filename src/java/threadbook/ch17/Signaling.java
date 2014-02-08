package threadbook.ch17;

public class Signaling extends Object {
	private BooleanLock readyLock;

	public Signaling(BooleanLock readyLock) {
		this.readyLock = readyLock;

		Runnable r = new Runnable() {
				public void run() {
					try {
						runWork();
					} catch ( Exception x ) {
						// in case ANY exception slips through
						x.printStackTrace(); 
					}
				}
			};

		Thread internalThread = new Thread(r, "internal");
		internalThread.start();
	}
	
	private void runWork() {
		try {
			print("about to wait for readyLock to be true");
			readyLock.waitUntilTrue(0);  // 0 - wait forever
			print("readyLock is now true");
		} catch ( InterruptedException x ) {
			print("interrupted while waiting for readyLock " +
					"to become true");
		}
	}

	private static void print(String msg) {
		String name = Thread.currentThread().getName();
		System.err.println(name + ": " + msg);
	}

	public static void main(String[] args) {
		try {
			print("creating BooleanLock instance");
			BooleanLock ready = new BooleanLock(false);
		
			print("creating Signaling instance");
			new Signaling(ready);

			print("about to sleep for 3 seconds");
			Thread.sleep(3000);

			print("about to setValue to true");
			ready.setValue(true);
			print("ready.isTrue()=" + ready.isTrue());
		} catch ( InterruptedException x ) {
			x.printStackTrace();
		}
	}
}
