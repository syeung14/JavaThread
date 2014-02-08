package threadbook.ch09;

import java.awt.*;

import javax.swing.*;

public class InvokeLaterDemo extends Object {
	private static void print(String msg) {
		String name = Thread.currentThread().getName();
		System.out.println(name + ": " + msg);
	}

	public static void main(String[] args) {
		final JLabel label = new JLabel("--------");

		JPanel panel = new JPanel(new FlowLayout());
		panel.add(label);

		JFrame f = new JFrame("InvokeLaterDemo");
		f.setContentPane(panel);
		f.setSize(300, 100);
		f.setVisible(true);

		try {
			print("sleeping for 3 seconds");
			Thread.sleep(3000);
		} catch ( InterruptedException ix ) {
			print("interrupted while sleeping");
		}

		print("creating code block for event thread");
		Runnable setTextRun = new Runnable() {
				public void run() {
					try {
						Thread.sleep(100); // for emphasis
						print("about to do setText()");
						label.setText("New text!");
					} catch ( Exception x ) {
						x.printStackTrace();
					}
				}
			};
			
		print("about to invokeLater()");
		SwingUtilities.invokeLater(setTextRun);
		print("back from invokeLater()");
	}
}
