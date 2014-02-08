package threadbook.ch16;

import java.util.*;

public class SureStopVerbose extends Object {
	// nested internal class for stop request entries
	private static class Entry extends Object {
		private Thread thread;
		private long stopTime;

		private Entry(Thread t, long stop) {
			thread = t;
			stopTime = stop;
		}
	}

	// static reference to the singleton instance
	private static SureStopVerbose ss;

	static {
		// When class is loaded, create exactly one instance 
		// using the private constructor.
		ss = new SureStopVerbose();
		print("SureStopVerbose instance created.");
	}

	private List stopList;
	private List pendingList;
	private Thread internalThread;

	private SureStopVerbose() {
		// using a linked list for fast deletions
		stopList = new LinkedList();

		// Enough initial capacity for 20 pending additions, 
		// will grow automatically if necessary to keep 
		// ensureStop() from blocking.
		pendingList = new ArrayList(20);

		Runnable r = new Runnable() {
				public void run() {
					try {
						runWork();
					} catch ( Exception x ) {
						// in case ANY exception slips through
						x.printStackTrace(); 
					}
				}
			};

		internalThread = new Thread(r);
		internalThread.setDaemon(true); // no need to run alone
		internalThread.setPriority(Thread.MAX_PRIORITY); // high
		internalThread.start();
	}

	private void runWork() {
		try {
			while ( true ) {
				// Since this is a super-high priority thread, 
				// be sure to give other threads a chance to 
				// run each time through in case the wait on 
				// pendingList is very short.
				print("about to sleep for 0.5 seconds");
				Thread.sleep(500);
				print("done with sleep for 0.5 seconds");

				long sleepTime = checkStopList();
				print("back from checkStopList(), sleepTime=" + 
						sleepTime);

				synchronized ( pendingList ) {
					if ( pendingList.size() < 1 ) {
						print("about to wait on pendingList " +
							"for " + sleepTime + " ms");
						long start = System.currentTimeMillis();
						pendingList.wait(sleepTime);
						long elapsedTime = 
							System.currentTimeMillis() - start;
						print("waited on pendingList for " + 
							elapsedTime + " ms");
					}


					if ( pendingList.size() > 0 ) {
						// copy into stopList and then remove 
						// from pendingList.
						print("copying " + pendingList.size() + 
							" elements from pendingList to " +
							"stopList");
						int oldSize = stopList.size();
						stopList.addAll(pendingList);
						pendingList.clear();
						int newSize = stopList.size();
						print("pendingList.size()=" + 
							pendingList.size() +
							", stopList grew by " + 
							(newSize - oldSize));
					}
				}
			} // while
		} catch ( InterruptedException x ) {
			// ignore
		} catch ( Exception x ) {
			// Never expect this, but print a trace in case 
			// it happens.
			x.printStackTrace();
		}
	}

	private long checkStopList() {
		print("entering checkStopList() - stopList.size()=" + 
				stopList.size());
		long currTime = System.currentTimeMillis();
		long minTime = Long.MAX_VALUE;

		Iterator iter = stopList.iterator();
		while ( iter.hasNext() ) {
			Entry entry = (Entry) iter.next();

			if ( entry.thread.isAlive() ) {
				print("thread is alive - " + 
						entry.thread.getName());
				if ( entry.stopTime < currTime ) {
					// timed out, stop it abruptly right now
					print("timed out, stopping - " + 
							entry.thread.getName());
					try {
						entry.thread.stop();
					} catch ( SecurityException x ) {
						// Catch this here so that other 
						// operations are not disrupted. Warn 
						// that thread could not be stopped.
						System.err.println(
							"SureStop was not permitted to " +
							"stop thread=" + entry.thread);
						x.printStackTrace();
					}

					// Since it's stopped, remove it 
					// from stopList.
					iter.remove();
				} else {
					// Not yet expired, check to see if this 
					// is the new minimum.
					minTime = Math.min(entry.stopTime, minTime);
					print("new minTime=" + minTime);
				}
			} else {
				print("thread died on its own - " + 
						entry.thread.getName());
				// Thread died on its own, remove it from 
				// stopList.
				iter.remove();
			} // if alive
		} // while

		long sleepTime = minTime - System.currentTimeMillis();

		// ensure that it is a least a little bit of time
		sleepTime = Math.max(50, sleepTime); 

		print("leaving checkStopList() - stopList.size()=" + 
				stopList.size());
		return sleepTime;
	}

	private void addEntry(Entry entry) {
		synchronized ( pendingList ) {
			pendingList.add(entry);

			// no need for notifyAll(), one waiter
			pendingList.notify(); 
			print("added entry to pendingList, name=" + 
				entry.thread.getName() + 
				", stopTime=" + entry.stopTime + ", in " + 
				( entry.stopTime - System.currentTimeMillis() ) +
				" ms");
		}
	}

	public static void ensureStop(Thread t, long msGracePeriod) {
		print("entering ensureStop() - name=" + t.getName() +
			", msGracePeriod=" + msGracePeriod);

		if ( !t.isAlive() ) {
			// thread is already stopped, return right away
			print("already stopped, not added to list - " + 
					t.getName());
			return;
		}

		long stopTime = 
				System.currentTimeMillis() + msGracePeriod;
		Entry entry = new Entry(t, stopTime);
		ss.addEntry(entry);
		print("leaving ensureStop() - name=" + t.getName());
	}

	private static void print(String msg) {
		Thread t = Thread.currentThread();
		String name = t.getName();
		if ( t == ss.internalThread ) {
			name = "SureStopThread";
		}

		System.out.println(name + ": " + msg);
	}
}
