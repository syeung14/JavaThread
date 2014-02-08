package threadbook.ch08;

public class MissedNotifyFix extends Object {
	private Object proceedLock;
	private boolean okToProceed;

	public MissedNotifyFix() {
		print("in MissedNotify()");
		proceedLock = new Object();
		okToProceed = false;
	}

	public void waitToProceed() throws InterruptedException {
		print("in waitToProceed() - entered");

		synchronized ( proceedLock ) {
			print("in waitToProceed() - entered sync block");

			while ( okToProceed == false ) {
				print("in waitToProceed() - about to wait()");
				proceedLock.wait();
				print("in waitToProceed() - back from wait()");
			}

			print("in waitToProceed() - leaving sync block");
		}

		print("in waitToProceed() - leaving");
	}

	public void proceed() {
		print("in proceed() - entered");

		synchronized ( proceedLock ) {
			print("in proceed() - entered sync block");

			okToProceed = true;
			print("in proceed() - changed okToProceed to true");
			proceedLock.notifyAll();
			print("in proceed() - just did notifyAll()");

			print("in proceed() - leaving sync block");
		}

		print("in proceed() - leaving");
	}

	private static void print(String msg) {
		String name = Thread.currentThread().getName();
		System.out.println(name + ": " + msg);
	}

	public static void main(String[] args) {
		final MissedNotifyFix mnf = new MissedNotifyFix();

		Runnable runA = new Runnable() {
				public void run() {
					try {
						Thread.sleep(1000);
						mnf.waitToProceed();
					} catch ( InterruptedException x ) {
						x.printStackTrace();
					}
				}
			};

		Thread threadA = new Thread(runA, "threadA");
		threadA.start();

		Runnable runB = new Runnable() {
				public void run() {
					try {
						Thread.sleep(500);
						mnf.proceed();
					} catch ( InterruptedException x ) {
						x.printStackTrace();
					}
				}
			};

		Thread threadB = new Thread(runB, "threadB");
		threadB.start();

		try { 
			Thread.sleep(10000);
		} catch ( InterruptedException x ) {
		}

		print("about to invoke interrupt() on threadA");
		threadA.interrupt();
	}
}
