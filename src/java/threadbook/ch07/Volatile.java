package threadbook.ch07;

public class Volatile extends Object implements Runnable {
	// not marked as 'volatile', but it should be!
	private int value;  

	private volatile boolean missedIt;

	// doesn't need to be volatile-doesn't change
	private long creationTime; 

	public Volatile() {
		value = 10;
		missedIt = false;
		creationTime = System.currentTimeMillis();
	}

	public void run() {
		print("entering run()");

		// each time, check to see if 'value' is different
		while ( value < 20 ) {

			// Used to break out of the loop if change to 
			// value is missed.
			if  ( missedIt ) {
				int currValue = value;

				// Simply execute a synchronized statement on an
				// arbitrary object to see the effect.
				Object lock = new Object();
				synchronized ( lock ) {
					// do nothing!
				}

				int valueAfterSync = value;

				print("in run() - see value=" + currValue +
					", but rumor has it that it changed!");
				print("in run() - valueAfterSync=" + 
					valueAfterSync);

				break; 
			}
		}

		print("leaving run()");
	}

	public void workMethod() throws InterruptedException {
		print("entering workMethod()");

		print("in workMethod() - about to sleep for 2 seconds");
		Thread.sleep(2000);

		value = 50;
		print("in workMethod() - just set value=" + value);

		print("in workMethod() - about to sleep for 5 seconds");
		Thread.sleep(5000);

		missedIt = true;
		print("in workMethod() - just set missedIt=" + missedIt);

		print("in workMethod() - about to sleep for 3 seconds");
		Thread.sleep(3000);

		print("leaving workMethod()");
	}

	private void print(String msg) {
		// This method could have been simplified by using 
		// functionality present in the java.text package, 
		// but did not take advantage of it since that package 
		// is not present in JDK1.0.

		long interval = System.currentTimeMillis() - 
						creationTime;

		String tmpStr = "    " + ( interval / 1000.0 ) + "000";
		
		int pos = tmpStr.indexOf(".");
		String secStr = tmpStr.substring(pos - 2, pos + 4);

		String nameStr = "        " + 
				Thread.currentThread().getName();

		nameStr = nameStr.substring(nameStr.length() - 8, 
									nameStr.length());
		
		System.out.println(secStr + " " + nameStr + ": " + msg);
	}

	public static void main(String[] args) {
		try {
			Volatile vol = new Volatile();

			// slight pause to let some time elapse
			Thread.sleep(100);  

			Thread t = new Thread(vol);
			t.start();

			// slight pause to allow run() to go first
			Thread.sleep(100);  

			vol.workMethod();
		} catch ( InterruptedException x ) {
			System.err.println(
				"one of the sleeps was interrupted");
		}
	}
}
