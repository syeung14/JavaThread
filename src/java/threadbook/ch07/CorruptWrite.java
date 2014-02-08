package threadbook.ch07;

public class CorruptWrite extends Object {
	private String fname;
	private String lname;

	public void setNames(String firstName, String lastName) {
		print("entering setNames()");
		fname = firstName;

		// A thread might be swapped out here, and may stay 
		// out for a varying amount of time. The different 
		// sleep times exaggerate this.
		if ( fname.length() < 5 ) {
			try { Thread.sleep(1000); } 
			catch ( InterruptedException x ) { }
		} else {
			try { Thread.sleep(2000); } 
			catch ( InterruptedException x ) { }
		}

		lname = lastName;

		print("leaving setNames() - " + lname + ", " + fname);
	}

	public static void print(String msg) {
		String threadName = Thread.currentThread().getName();
		System.out.println(threadName + ": " + msg);
	}

	public static void main(String[] args) {
		final CorruptWrite cw = new CorruptWrite();

		Runnable runA = new Runnable() {
				public void run() {
					cw.setNames("George", "Washington");
				}
			};

		Thread threadA = new Thread(runA, "threadA");
		threadA.start();

		try { Thread.sleep(200); } 
		catch ( InterruptedException x ) { }

		Runnable runB = new Runnable() {
				public void run() {
					cw.setNames("Abe", "Lincoln");
				}
			};

		Thread threadB = new Thread(runB, "threadB");
		threadB.start();
	}
}
