package threadbook.ch15;

import java.io.*;
import java.net.*;
import java.util.*;

public class CalcServerTwo extends Object {
	private ServerSocket ss;
	private List workerList;
	
	private Thread internalThread;
	private volatile boolean noStopRequested;

	public CalcServerTwo(int port) throws IOException {
		ss = new ServerSocket(port);
		workerList = new LinkedList();

		noStopRequested = true;
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
		internalThread.start();
	}

	private void runWork() {
		System.out.println(
				"in CalcServer - ready to accept connections");

		while ( noStopRequested ) {
			try {
				System.out.println(
						"in CalcServer - about to block " +
						"waiting for a new connection");
				Socket sock = ss.accept();
				System.out.println(
					"in CalcServer - received new connection");
				workerList.add(new CalcWorkerTwo(sock));
			} catch ( IOException iox ) {
				if ( noStopRequested ) {
					iox.printStackTrace();
				}
			}
		}

		// stop all the workers that were created
		System.out.println("in CalcServer - putting in a " +
				"stop request to all the workers");
		Iterator iter = workerList.iterator();
		while ( iter.hasNext() ) {
			CalcWorkerTwo worker = (CalcWorkerTwo) iter.next();
			worker.stopRequest();
		}

		System.out.println("in CalcServer - leaving runWork()");
	}

	public void stopRequest() {
		System.out.println(
				"in CalcServer - entering stopRequest()");
		noStopRequested = false;
		internalThread.interrupt();

		if ( ss != null ) {
			try {
				ss.close();
			} catch ( IOException x ) {
				// ignore
			} finally {
				ss = null;
			}
		}
	}

	public boolean isAlive() {
		return internalThread.isAlive();
	}

	public static void main(String[] args) {
		int port = 2001;

		try {
			CalcServerTwo server = new CalcServerTwo(port);
			Thread.sleep(15000);
			server.stopRequest();
		} catch ( IOException x ) {
			x.printStackTrace();
		} catch ( InterruptedException x ) {
			// ignore
		}
	}
}
