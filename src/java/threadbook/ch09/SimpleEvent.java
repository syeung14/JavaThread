package threadbook.ch09;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class SimpleEvent extends Object {
	private static void print(String msg) {
		String name = Thread.currentThread().getName();
		System.out.println(name + ": " + msg);
		
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					runIt();

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}
		}).start();
	}

	static JLabel label = new JLabel("--------");
	public static void main(String[] args) {
		JButton button = new JButton("Click Here");

		JPanel panel = new JPanel(new FlowLayout());
		panel.add(button);
		panel.add(label);

		button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					print("in actionPerformed()");
					label.setText("CLICKED!");
				}
			});

		JFrame f = new JFrame("SimpleEvent");
		f.setContentPane(panel);
		f.setSize(300, 100);
		f.setVisible(true);
	}
	
	private static String key = "CLICKED!";
	private static String key2 = "CLICKED!!!!!!!!!!!!!!!";
	private static void runIt() {
		if (label.getText().equals(key)) {
			
			label.setText(key2);
		}
		else label.setText(key);
		
	}
}
