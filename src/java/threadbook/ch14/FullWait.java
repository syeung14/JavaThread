package threadbook.ch14;

public class FullWait extends Object {
	private volatile int value;

	public FullWait(int initialValue) {
		value = initialValue;
	}

	public synchronized void setValue(int newValue) {
		if ( value != newValue ) {
			value = newValue;
			notifyAll();
		}
	}

	public synchronized boolean waitUntilAtLeast(
				int minValue,
				long msTimeout
			) throws InterruptedException {

		if ( msTimeout == 0L ) {
			while ( value < minValue ) {
				wait();  // wait indefinitely until notified
			}

			// condition has finally been met
			return true;
		} 

		// only wait for the specified amount of time
		long endTime = System.currentTimeMillis() + msTimeout;
		long msRemaining = msTimeout;

		while ( ( value < minValue ) && ( msRemaining > 0L ) ) {
			wait(msRemaining);
			msRemaining = endTime - System.currentTimeMillis();
		}

		// May have timed out, or may have met value, 
		// calc return value.
		return ( value >= minValue );
	}

	public String toString() {
		return getClass().getName() + "[value=" + value + "]";
	}
}
