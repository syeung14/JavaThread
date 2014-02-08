package threadbook.ch09;

import java.awt.*;
import java.lang.reflect.*;

import javax.swing.*;

public class InvokeAndWaitDemo extends Object {
	private static void print(String msg) {
		String name = Thread.currentThread().getName();
		System.out.println(name + ": " + msg);
	}

	public static void main(String[] args) {
		final JLabel label = new JLabel("--------");

		JPanel panel = new JPanel(new FlowLayout());
		panel.add(label);

		JFrame f = new JFrame("InvokeAndWaitDemo");
		f.setContentPane(panel);
		f.setSize(300, 100);
		f.setVisible(true);

		try {
			print("sleeping for 3 seconds");
			Thread.sleep(3000);

			print("creating code block for event thread");
			Runnable setTextRun = new Runnable() {
					public void run() {
						print("about wait to do setText()");
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						print("about to do setText()");
						label.setText("New text!");
					}
				};
			
			print("about to invokeAndWait()");
			SwingUtilities.invokeAndWait(setTextRun);
			

			print("back from invokeAndWait()");
		} catch ( InterruptedException ix ) {
			print("interrupted while waiting on invokeAndWait()");
		} catch ( InvocationTargetException x ) {
			print("exception thrown from run()");
		}
	}
}
