package threadbook.ch03;

public class TwoThreadSleep extends Thread {
	public void run() {
		loop();
	}

	public void loop() {
		// get a reference to the thread running this
		Thread t = Thread.currentThread();
		String name = t.getName();

		System.out.println("just entered loop() - " + name);

		for ( int i = 0; i < 10; i++ ) {
			try {
				Thread.sleep(200);
			} catch ( InterruptedException x ) {
				// ignore
			}

			System.out.println("name=" + name);
		}

		System.out.println("about to leave loop() - " + name);
	}

	public static void main(String[] args) {
		TwoThreadSleep tt = new TwoThreadSleep();
		tt.setName("my worker thread");
		tt.start();

		// pause for a bit
		try {
			Thread.sleep(700);
		} catch ( InterruptedException x ) {
			// ignore
		}

		tt.loop();
	}
}
