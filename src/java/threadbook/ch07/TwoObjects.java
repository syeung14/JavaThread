package threadbook.ch07;

public class TwoObjects extends Object {
	private String objID;

	public TwoObjects(String objID) {
		this.objID = objID;
	}

	public synchronized void doStuff(int val) {
		print("entering doStuff()");
		int num = val * 2 + objID.length();
		print("in doStuff() - local variable num=" + num);

		// slow things down to make observations
		try { Thread.sleep(2000); } catch ( InterruptedException x ) { }

		print("leaving doStuff()");
	}

	public void print(String msg) {
		threadPrint("objID=" + objID + " - " + msg);
	}

	public static void threadPrint(String msg) {
		String threadName = Thread.currentThread().getName();
		System.out.println(threadName + ": " + msg);
	}

	public static void main(String[] args) {
		final TwoObjects obj1 = new TwoObjects("obj1");
		final TwoObjects obj2 = new TwoObjects("obj2");

		Runnable runA = new Runnable() {
				public void run() {
					obj1.doStuff(3);
				}
			};

		Thread threadA = new Thread(runA, "threadA");
		threadA.start();

		try { Thread.sleep(200); } catch ( InterruptedException x ) { }

		Runnable runB = new Runnable() {
				public void run() {
					obj2.doStuff(7);
				}
			};

		Thread threadB = new Thread(runB, "threadB");
		threadB.start();
	}
}
