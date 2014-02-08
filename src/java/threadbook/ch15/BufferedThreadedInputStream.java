package threadbook.ch15;

import java.io.*;

// uses ThreadedInputStream

public class BufferedThreadedInputStream 
		extends FilterInputStream {

	// fixed class that does *not* have a synchronized close()
	private static class BISFix extends BufferedInputStream {
		public BISFix(InputStream rawIn, int buffSize) {
			super(rawIn, buffSize);
		}

		public void close() throws IOException {
			if ( in != null ) {
				try {
					in.close();
				} finally {
					in = null;
				}
			}
		}
	}

	public BufferedThreadedInputStream(
				InputStream rawIn, 
				int bufferSize
			) {

		super(rawIn); // super-class' "in" is set below

		// rawIn -> BufferedIS -> ThreadedIS -> 
		//       BufferedIS -> read()

		BISFix bis = new BISFix(rawIn, bufferSize);
		ThreadedInputStream tis = 
				new ThreadedInputStream(bis, bufferSize);

		// Change the protected variable 'in' from the 
		// superclass from rawIn to the correct stream.
		in = new BISFix(tis, bufferSize);
	}

	public BufferedThreadedInputStream(InputStream rawIn) {
		this(rawIn, 2048);
	}

	// Overridden to show that InterruptedIOException might 
	// be thrown.
	public int read() 
			throws InterruptedIOException, IOException {

		return in.read();
	}

	// Overridden to show that InterruptedIOException might 
	// be thrown.
	public int read(byte[] b) 
			throws InterruptedIOException, IOException {

		return in.read(b);
	}

	// Overridden to show that InterruptedIOException might 
	// be thrown.
	public int read(byte[] b, int off, int len) 
			throws InterruptedIOException, IOException {

		return in.read(b, off, len);
	}

	// Overridden to show that InterruptedIOException might 
	// be thrown.
	public long skip(long n) 
			throws InterruptedIOException, IOException {

		return in.skip(n);
	}

	// The remainder of the methods are directly inherited from 
	// FilterInputStream and access "in" in the much the same 
	// way as the methods above do.
}
