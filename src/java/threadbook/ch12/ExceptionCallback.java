package threadbook.ch12;

import java.io.*;
import java.util.*;

public class ExceptionCallback extends Object {
	private Set exceptionListeners;
	private Thread internalThread;
	private volatile boolean noStopRequested;

	public ExceptionCallback(ExceptionListener[] initialGroup) {
		init(initialGroup);
	}

	public ExceptionCallback(ExceptionListener initialListener) {
		ExceptionListener[] group = new ExceptionListener[1];
		group[0] = initialListener;
		init(group);
	}

	public ExceptionCallback() {
		init(null);
	}

	private void init(ExceptionListener[] initialGroup) {
		System.out.println("in constructor - initializing...");

		exceptionListeners = 
				Collections.synchronizedSet(new HashSet());

		// If any listeners should be added before the internal
		// thread starts, add them now.
		if ( initialGroup != null ) {
			for ( int i = 0; i < initialGroup.length; i++ ) {
				addExceptionListener(initialGroup[i]);
			}
		}

		// Just before returning from the constructor, 
		// the thread should be created and started.
		noStopRequested = true;

		Runnable r = new Runnable() {
				public void run() {
					try {
						runWork();
					} catch ( Exception x ) {
						// in case ANY exception slips through
						sendException(x);
					}
				}
			};

		internalThread = new Thread(r);
		internalThread.start();
	}

	private void runWork() {
		try {
			makeConnection(); // will throw an IOException
		} catch ( IOException x ) {
			sendException(x);
			// Probably in a real scenario, a "return" 
			// statement should be here.
		} 

		String str = null;
		int len = determineLength(str); // NullPointerException
	}

	private void makeConnection() throws IOException {
		// A NumberFormatException will be thrown when
		// this String is parsed.
		String portStr = "j20"; 
		int port = 0;

		try {
			port = Integer.parseInt(portStr);
		} catch ( NumberFormatException x ) {
			sendException(x);
			port = 80; // use default;
		} 

		connectToPort(port); // will throw an IOException
	}

	private void connectToPort(int portNum) throws IOException {
		throw new IOException("connection refused");
	}

	private int determineLength(String s) {
		return s.length();
	}

	public void stopRequest() {
		noStopRequested = false;
		internalThread.interrupt();
	}

	public boolean isAlive() {
		return internalThread.isAlive();
	}

	private void sendException(Exception x) {
		if ( exceptionListeners.size() == 0 ) {
			// If there aren't any listeners, dump the stack
			// trace to the console.
			x.printStackTrace();
			return;
		}

		// Used "synchronized" to make sure that other threads
		// do not make changes to the Set while iterating.
		synchronized ( exceptionListeners ) {
			Iterator iter = exceptionListeners.iterator();
			while ( iter.hasNext() ) {
				ExceptionListener l = 
						(ExceptionListener) iter.next();

				l.exceptionOccurred(x, this);
			}
		}
	}

	public void addExceptionListener(ExceptionListener l) {
		// Silently ignore a request to add a "null" listener.
		if ( l != null ) {
			// If a listener was already in the Set, it will
			// silently replace itself so that no duplicates
			// accumulate.
			exceptionListeners.add(l);
		}
	}

	public void removeExceptionListener(ExceptionListener l) {
		// Silently ignore a request to remove a listener
		// that is not in the Set.
		exceptionListeners.remove(l);
	}

	public String toString() {
		return getClass().getName() + 
			"[isAlive()=" + isAlive() + "]";
	}
}
