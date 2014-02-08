package threadbook.ch15;

import java.io.*;

public class DefiantStream extends Object {
	public static void main(String[] args) {
		final InputStream in = System.in;

		Runnable r = new Runnable() {
				public void run() {
					try {
						System.err.println(
								"about to try to read from in");
						in.read();
						System.err.println("just read from in");
					} catch ( InterruptedIOException iiox ) {
						iiox.printStackTrace();
					} catch ( IOException iox ) {
						iox.printStackTrace();
					//} catch ( InterruptedException ix ) { 
					//  InterruptedException is never thrown!
					//	ix.printStackTrace();
					} catch ( Exception x ) {
						x.printStackTrace();
					} finally {
						Thread currThread = 
								Thread.currentThread();
						System.err.println("inside finally:\n" +
							"  currThread=" + currThread + "\n" +
							"  currThread.isAlive()=" + 
							currThread.isAlive());
					}
				}
			};
		
		Thread t = new Thread(r);
		t.start();

		try { Thread.sleep(2000); } 
		catch ( InterruptedException x ) { }

		System.err.println("about to interrupt thread");
		t.interrupt();
		System.err.println("just interrupted thread");

		try { Thread.sleep(2000); } 
		catch ( InterruptedException x ) { }

		System.err.println("about to stop thread");
		// stop() is being used here to show that the extreme
		// action of stopping a thread is also ineffective. 
		// Because stop() is deprecated, the compiler issues
		// a warning.
		t.stop(); 
		System.err.println("just stopped thread, t.isAlive()=" +
				t.isAlive());

		try { Thread.sleep(2000); } 
		catch ( InterruptedException x ) { }

		System.err.println("t.isAlive()=" + t.isAlive());
		System.err.println("leaving main()");
	}
}
