package threadbook.ch08;

public class CubbyHoleMain extends Object {
	private static void print(String msg) {
		String name = Thread.currentThread().getName();
		System.out.println(name + ": " + msg);
	}

	public static void main(String[] args) {
		final CubbyHole ch = new CubbyHole();

		Runnable runA = new Runnable() {
				public void run() {
					try {
						String str;
						Thread.sleep(500);

						str = "multithreaded";
						ch.putIn(str);
						print("in run() - just put in: '" + 
								str + "'");

						str = "programming";
						ch.putIn(str);
						print("in run() - just put in: '" + 
								str + "'");

						str = "with Java";
						ch.putIn(str);
						print("in run() - just put in: '" + 
								str + "'");
					} catch ( InterruptedException x ) {
						x.printStackTrace();
					}
				}
			};

		Runnable runB = new Runnable() {
				public void run() {
					try {
						Object obj;

						obj = ch.takeOut();
						print("in run() - just took out: '" + 
								obj + "'");

						Thread.sleep(500);

						obj = ch.takeOut();
						print("in run() - just took out: '" + 
								obj + "'");

						obj = ch.takeOut();
						print("in run() - just took out: '" + 
								obj + "'");
					} catch ( InterruptedException x ) {
						x.printStackTrace();
					}
				}
			};

		Thread threadA = new Thread(runA, "threadA");
		threadA.start();

		Thread threadB = new Thread(runB, "threadB");
		threadB.start();
	}
}
