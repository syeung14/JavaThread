package threadbook.ch15;

import java.io.*;
import java.net.*;

public class CalcWorkerTwo extends Object {
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
	
	private Thread internalThread;
	private volatile boolean noStopRequested;

	public CalcWorkerTwo(Socket sock) throws IOException {
		dataIn = new DataInputStream(
			new BufferedThreadedInputStream(
				sock.getInputStream()));
		dataOut = new DataOutputStream(
			new BufferedOutputStream(
				sock.getOutputStream()));

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
		while ( noStopRequested ) {
			try {
				System.out.println("in CalcWorker - about to " +
						"block waiting to read a double");
				double val = dataIn.readDouble();
				System.out.println(
						"in CalcWorker - read a double!");
				dataOut.writeDouble(Math.sqrt(val));
				dataOut.flush();
			} catch ( InterruptedIOException iiox ) {
				System.out.println("in CalcWorker - blocked " +
						"read was interrupted!!!");
			} catch ( IOException x ) {
				if ( noStopRequested ) {
					x.printStackTrace();
					stopRequest();
				}
			}
		}

		// In real-world code, be sure to close other streams 
		// and the socket as part of the clean-up. Omitted here 
		// for brevity.

		System.out.println("in CalcWorker - leaving runWork()");
	}

	public void stopRequest() {
		System.out.println(
				"in CalcWorker - entering stopRequest()");
		noStopRequested = false;
		internalThread.interrupt();
		System.out.println(
				"in CalcWorker - leaving stopRequest()");
	}

	public boolean isAlive() {
		return internalThread.isAlive();
	}
}
