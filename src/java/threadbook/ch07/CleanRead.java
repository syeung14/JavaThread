package threadbook.ch07;

public class CleanRead extends Object {
	private String fname;
	private String lname;

	public synchronized String getNames() {
		return lname + ", " + fname;
	}

	public synchronized void setNames(
				String firstName, 
				String lastName
			) {

		print("entering setNames()");
		fname = firstName;

		try { Thread.sleep(1000); } 
		catch ( InterruptedException x ) { }

		lname = lastName;
		print("leaving setNames() - " + lname + ", " + fname);
	}

	public static void print(String msg) {
		String threadName = Thread.currentThread().getName();
		System.out.println(threadName + ": " + msg);
	}

	public static void main(String[] args) {
		final CleanRead cr = new CleanRead();
		cr.setNames("George", "Washington"); // initially

		Runnable runA = new Runnable() {
				public void run() {
					cr.setNames("Abe", "Lincoln");
				}
			};

		Thread threadA = new Thread(runA, "threadA");
		threadA.start();

		try { Thread.sleep(200); } 
		catch ( InterruptedException x ) { }

		Runnable runB = new Runnable() {
				public void run() {
					print("getNames()=" + cr.getNames());
				}
			};

		Thread threadB = new Thread(runB, "threadB");
		threadB.start();
	}
}
