package threadbook.ch07;

public class DirtyRead extends Object {
	private String fname;
	private String lname;

	public String getNames() {
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
		final DirtyRead dr = new DirtyRead();
		dr.setNames("George", "Washington"); // initially 

		Runnable runA = new Runnable() {
				public void run() {
					dr.setNames("Abe", "Lincoln");
				}
			};

		Thread threadA = new Thread(runA, "threadA");
		threadA.start();

		try { Thread.sleep(200); } 
		catch ( InterruptedException x ) { }

		Runnable runB = new Runnable() {
				public void run() {
					print("getNames()=" + dr.getNames());
				}
			};

		Thread threadB = new Thread(runB, "threadB");
		threadB.start();
	}
}
