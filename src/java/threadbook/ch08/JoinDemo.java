package threadbook.ch08;

public class JoinDemo extends Object {
	public static Thread launch(String name, long napTime) {
		final long sleepTime = napTime;

		Runnable r = new Runnable() {
				public void run() {
					try {
						print("in run() - entering");
						Thread.sleep(sleepTime);
					} catch ( InterruptedException x ) {
						print("interrupted!");
					} finally {
						print("in run() - leaving");
					}
				}
			};
	
		Thread t = new Thread(r, name);
		t.start();

		return t;
	}

	private static void print(String msg) {
		String name = Thread.currentThread().getName();
		System.out.println(name + ": " + msg);
	}

	public static void main(String[] args) {
		Thread[] t = new Thread[3];

		t[0] = launch("threadA", 2000);
		t[1] = launch("threadB", 1000);
		t[2] = launch("threadC", 3000);

		for ( int i = 0; i < t.length; i++ ) {
			try {
				String idxStr = "t[" + i + "]";
				String name = "[" + t[i].getName() + "]";

				print(idxStr + ".isAlive()=" + 
						t[i].isAlive() + " " + name);
				print("about to do: " + idxStr + 
						".join() " + name);

				long start = System.currentTimeMillis();
				t[i].join(); // wait for the thread to die
				long stop = System.currentTimeMillis();

				print(idxStr + ".join() - took " + 
						( stop - start ) + " ms " + name);
			} catch ( InterruptedException x ) {
				print("interrupted waiting on #" + i);
			}
		}
	}
}
