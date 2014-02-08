package threadbook.ch08;

public class InheritableThreadID extends Object {
	public static final int UNIQUE  = 101;
	public static final int INHERIT = 102;
	public static final int SUFFIX  = 103;

	private ThreadLocal threadLocal;
	private int nextID;

	public InheritableThreadID(int type) {
		nextID = 201;

		switch ( type ) {
			case UNIQUE:
				threadLocal = new ThreadLocal() {
						// override from ThreadLocal
						protected Object initialValue() {
							print("in initialValue()");
							return getNewID();
						}
					};
				break;

			case INHERIT:
				threadLocal = new InheritableThreadLocal() {
						// override from ThreadLocal
						protected Object initialValue() {
							print("in initialValue()");
							return getNewID();
						}
					};
				break;

			case SUFFIX:
				threadLocal = new InheritableThreadLocal() {
						// override from ThreadLocal
						protected Object initialValue() {
							print("in initialValue()");
							return getNewID();
						}

						// override from InheritableThreadLocal
						protected Object childValue(
									Object parentValue
								) {

							print("in childValue() - " +
								"parentValue=" + parentValue);

							return parentValue + "-CH";
						}
					};
				break;
			default:
				break;
		}
	}

	private synchronized String getNewID() {
		String id = "ID" + nextID;
		nextID++;
		return id;
	}

	public String getID() {
		return (String) threadLocal.get();
	}

	public static void print(String msg) {
		String name = Thread.currentThread().getName();
		System.out.println(name + ": " + msg);
	}

	public static Runnable createTarget(InheritableThreadID id) {
		final InheritableThreadID var = id;

		Runnable parentRun = new Runnable() {
			public void run() {
				print("var.getID()=" + var.getID());
				print("var.getID()=" + var.getID());
				print("var.getID()=" + var.getID());

				Runnable childRun = new Runnable() {
						public void run() {
							print("var.getID()=" + var.getID());
							print("var.getID()=" + var.getID());
							print("var.getID()=" + var.getID());
						}
					};

				Thread parentT = Thread.currentThread();
				String parentName = parentT.getName();
				print("creating a child thread of " +
					parentName);

				Thread childT = new Thread(childRun, 
						parentName + "-child");
				childT.start();
			}
		};

		return parentRun;
	}

	public static void main(String[] args) {
		try {
			System.out.println("======= ThreadLocal =======");
			InheritableThreadID varA = 
				new InheritableThreadID(UNIQUE);

			Runnable targetA = createTarget(varA);
			Thread threadA = new Thread(targetA, "threadA");
			threadA.start();

			Thread.sleep(2500);
			System.out.println("\n======= " +
				"InheritableThreadLocal =======");

			InheritableThreadID varB = 
				new InheritableThreadID(INHERIT);

			Runnable targetB = createTarget(varB);
			Thread threadB = new Thread(targetB, "threadB");
			threadB.start();

			Thread.sleep(2500);
			System.out.println("\n======= " +
				"InheritableThreadLocal - custom childValue()" +
				" =======");

			InheritableThreadID varC = 
				new InheritableThreadID(SUFFIX);

			Runnable targetC = createTarget(varC);
			Thread threadC = new Thread(targetC, "threadC");
			threadC.start();
		} catch ( InterruptedException x ) {
			// ignore
		}

	}
}
