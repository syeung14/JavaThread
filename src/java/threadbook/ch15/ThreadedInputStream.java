package threadbook.ch15;

import java.io.*;

import threadbook.ch16.*;
import threadbook.ch18.*;

// uses SureStop from chapter 16
// uses ByteFIFO from chapter 18

public class ThreadedInputStream extends FilterInputStream {
	private ByteFIFO buffer;

	private volatile boolean closeRequested;
	private volatile boolean eofDetected;
	private volatile boolean ioxDetected;
	private volatile String ioxMessage;

	private Thread internalThread;
	private volatile boolean noStopRequested;

	public ThreadedInputStream(InputStream in, int bufferSize) {
		super(in);

		buffer = new ByteFIFO(bufferSize);

		closeRequested = false;
		eofDetected = false;
		ioxDetected = false;
		ioxMessage = null;

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
		internalThread.setDaemon(true);
		internalThread.start();
	}

	public ThreadedInputStream(InputStream in) {
		this(in, 2048);
	}

	private void runWork() {
		byte[] workBuf = new byte[buffer.getCapacity()];

		try {
			while ( noStopRequested ) {
				int readCount = in.read(workBuf);

				if ( readCount == -1 ) {
					signalEOF();
					stopRequest();
				} else if ( readCount > 0 ) {
					addToBuffer(workBuf, readCount);
				}
			}
		} catch ( IOException iox ) {
			if ( !closeRequested ) {
				ioxMessage = iox.getMessage();
				signalIOX();
			}
		} catch ( InterruptedException x ) {
			// ignore
		} finally {
			// no matter what, make sure that eofDetected is set
			signalEOF();
		}
	}

	private void signalEOF() {
		synchronized ( buffer ) {
			eofDetected = true;
			buffer.notifyAll();
		}
	}

	private void signalIOX() {
		synchronized ( buffer ) {
			ioxDetected = true;
			buffer.notifyAll();
		}
	}
	
	private void signalClose() {
		synchronized ( buffer ) {
			closeRequested = true;
			buffer.notifyAll();
		}
	}

	private void addToBuffer(byte[] workBuf, int readCount) 
			throws InterruptedException {

		// Create an array exactly as large as the number of
		// bytes read and copy the data into it.
		byte[] addBuf = new byte[readCount];
		System.arraycopy(workBuf, 0, addBuf, 0, addBuf.length);

		buffer.add(addBuf);
	}

	private void stopRequest() {
		if ( noStopRequested ) {
			noStopRequested = false;
			internalThread.interrupt();
		}
	}

	public void close() throws IOException {
		if ( closeRequested ) {
			// already closeRequested, just return
			return;
		}
		signalClose();

		SureStop.ensureStop(internalThread, 10000);
		stopRequest();

		// Use a new thread to close "in" in case it blocks
		final InputStream localIn = in;
		Runnable r = new Runnable() {
				public void run() {
					try {
						localIn.close();
					} catch ( IOException iox ) {
						// ignore
					}
				}
			};

		Thread t = new Thread(r, "in-close");
		// give up when all other non-daemon threads die
		t.setDaemon(true);  
		t.start();
	}

	private void throwExceptionIfClosed() throws IOException {
		if ( closeRequested ) {
			throw new IOException("stream is closed");
		}
	}

	// Throws InterruptedIOException if the thread blocked on
	// read() is interrupted while waiting for data to arrive.
	public int read() 
			throws InterruptedIOException, IOException {

		// Using read(byte[]) to keep code in one place --makes
		// single-byte read less efficient, but simplifies 
		// the coding.
		byte[] data = new byte[1];
		int ret = read(data, 0, 1);

		if ( ret != 1 ) {
			return -1;
		}

		return data[0] & 0x000000FF;
	}

	// Throws InterruptedIOException if the thread blocked on
	// read() is interrupted while waiting for data to arrive.
	public int read(byte[] dest) 
			throws InterruptedIOException, IOException {

		return read(dest, 0, dest.length);
	}

	// Throws InterruptedIOException if the thread blocked on
	// read() is interrupted while waiting for data to arrive.
	public int read(
				byte[] dest, 
				int offset, 
				int length
			) throws InterruptedIOException, IOException {

		throwExceptionIfClosed();

		if ( length < 1 ) {
			return 0;
		}

		if ( ( offset < 0 ) || 
			 ( ( offset + length ) > dest.length ) 
		   ) {

			throw new IllegalArgumentException(
				"offset must be at least 0, and " +
				"(offset + length) must be less than or " +
				"equal to dest.length. " +
				"offset=" + offset + 
				", (offset + length )=" + ( offset + length ) +
				", dest.length=" + dest.length);
		}

		byte[] data = removeUpTo(length);

		if ( data.length > 0 ) {
			System.arraycopy(data, 0, dest, offset, data.length);
			return data.length;
		}

		// no data
		if ( eofDetected ) {
			return -1;
		}

		// no data and not end of file, must be exception
		stopRequest();

		if ( ioxMessage == null ) {
			ioxMessage = "stream cannot be read";
		}

		throw new IOException(ioxMessage);
	}

	private byte[] removeUpTo(int maxRead) throws IOException {
		// Convenience method to assist read(byte[], int, int).
		// Waits until at least one byte is ready, EOF is 
		// detected,  an IOException is thrown, or the 
		// stream is closed.
		try {
			synchronized ( buffer ) {
				while ( buffer.isEmpty() && 
						!eofDetected && 
						!ioxDetected &&
						!closeRequested
					  ) {
	
					buffer.wait();
				}
	
				// If stream was closed while waiting, 
				// get out right away.
				throwExceptionIfClosed();
	
				// Ignore eof and exception flags for now, see 
				// if any data remains.
				byte[] data = buffer.removeAll();
	
				if ( data.length > maxRead ) {
					// Pulled out too many bytes, 
					// put excess back.
					byte[] putBackData = 
							new byte[data.length - maxRead];
					System.arraycopy(data, maxRead, 
							putBackData, 0, putBackData.length);
					buffer.add(putBackData);
	
					byte[] keepData = new byte[maxRead];
					System.arraycopy(data, 0, 
							keepData, 0, keepData.length);
					data = keepData;
				}
	
				return data;
			}
		} catch ( InterruptedException ix ) {
			// convert to an IOException
			throw new InterruptedIOException("interrupted " +
				"while waiting for data to arrive for reading");
		}
	}

	public long skip(long n) throws IOException {
		throwExceptionIfClosed();

		if ( n <= 0 ) {
			return 0;
		}

		int skipLen = (int) Math.min(n, Integer.MAX_VALUE);
		int readCount = read(new byte[skipLen]);

		if ( readCount < 0 ) {
			return 0;
		}

		return readCount;
	}

	public int available() throws IOException {
		throwExceptionIfClosed();
		return buffer.getSize();
	}

	public boolean markSupported() {
		return false;
	}

	public synchronized void mark(int readLimit) {
		// ignore method calls, mark not supported
	}

	public synchronized void reset() throws IOException {
		throw new IOException(
				"mark-reset not supported on this stream");
	}
}
