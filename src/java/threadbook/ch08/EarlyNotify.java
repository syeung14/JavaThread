package threadbook.ch08;

import java.util.*;

public class EarlyNotify extends Object {
	private List list;

	public EarlyNotify() {
		list = Collections.synchronizedList(new LinkedList());
	}

	public String removeItem() throws InterruptedException {
		print("in removeItem() - entering");

		synchronized ( list ) {
			if ( list.isEmpty() ) {  // dangerous to use 'if'!
				print("in removeItem() - about to wait()");
				list.wait();
				print("in removeItem() - done with wait()");
			}

			// extract the new first item
			String item = (String) list.remove(0);

			print("in removeItem() - leaving");
			return item;
		}
	}

	public void addItem(String item) {
		print("in addItem() - entering");
		synchronized ( list ) {
			// There'll always be room to add to this List 
			// because it expands as needed. 
			list.add(item);
			print("in addItem() - just added: '" + item + "'");

			// After adding, notify any and all waiting 
			// threads that the list has changed.
			list.notifyAll();
			print("in addItem() - just notified");
		}
		print("in addItem() - leaving");
	}

	private static void print(String msg) {
		String name = Thread.currentThread().getName();
		System.out.println(name + ": " + msg);
	}

	public static void main(String[] args) {
		final EarlyNotify en = new EarlyNotify();

		Runnable runA = new Runnable() {
				public void run() {
					try {
						String item = en.removeItem();
						print("in run() - returned: '" + 
								item + "'");
					} catch ( InterruptedException ix ) {
						print("interrupted!");
					} catch ( Exception x ) {
						print("threw an Exception!!!\n" + x);
					}
				}
			};

		Runnable runB = new Runnable() {
				public void run() {
					en.addItem("Hello!");
				}
			};

		try {
			Thread threadA1 = new Thread(runA, "threadA1");
			threadA1.start();

			Thread.sleep(500);
	
			// start a *second* thread trying to remove
			Thread threadA2 = new Thread(runA, "threadA2");
			threadA2.start();

			Thread.sleep(500);
	
			Thread threadB = new Thread(runB, "threadB");
			threadB.start();

			Thread.sleep(10000); // wait 10 seconds

			threadA1.interrupt();
			threadA2.interrupt();
		} catch ( InterruptedException x ) {
			// ignore
		}
	}
}
