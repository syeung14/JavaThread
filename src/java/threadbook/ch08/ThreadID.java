package threadbook.ch08;

public class ThreadID extends ThreadLocal {
	private int nextID;

	public ThreadID() {
		nextID = 10001;
	}

	private synchronized Integer getNewID() {
		Integer id = new Integer(nextID);
		nextID++;
		return id;
	}

	// override ThreadLocal's version
	protected Object initialValue() {
		print("in initialValue()");
		return getNewID();
	}

	public int getThreadID() {
		// Call get() in ThreadLocal to get the calling
		// thread's unique ID.
		Integer id = (Integer) get(); 
		return id.intValue();
	}

	private static void print(String msg) {
		String name = Thread.currentThread().getName();
		System.out.println(name + ": " + msg);
	}
}
