package threadbook.ch12;

public class ExceptionCallbackMain 
		extends Object 
		implements ExceptionListener {

	private int exceptionCount;

	public ExceptionCallbackMain() {
		exceptionCount = 0;
	}

	public void exceptionOccurred(Exception x, Object source) {
		exceptionCount++;
		System.err.println("EXCEPTION #" + exceptionCount +
				", source=" + source);
		x.printStackTrace();
	}

	public static void main(String[] args) {
		ExceptionListener xListener = new ExceptionCallbackMain();
		ExceptionCallback ec = new ExceptionCallback(xListener);
	}
}
