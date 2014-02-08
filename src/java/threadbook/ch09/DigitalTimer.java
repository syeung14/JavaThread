package threadbook.ch09;

import java.awt.*;
import java.lang.reflect.*;
import java.text.*;

import javax.swing.*;

public class DigitalTimer extends JLabel {
	private volatile String timeText;

	private Thread internalThread;
	private volatile boolean noStopRequested;

	public DigitalTimer() {
		setBorder(BorderFactory.createLineBorder(Color.black));
		setHorizontalAlignment(SwingConstants.RIGHT);
		setFont(new Font("SansSerif", Font.BOLD, 16));
		setText("00000.0"); // use to size component
		setMinimumSize(getPreferredSize());
		setPreferredSize(getPreferredSize());
		setSize(getPreferredSize());

		timeText = "0.0";
		setText(timeText);

		noStopRequested = true;
		Runnable r = new Runnable() {
				public void run() {
					try {
						runWork();
					} catch ( Exception x ) {
						x.printStackTrace(); 
					}
				}
			};

		internalThread = new Thread(r, "DigitalTimer");
		internalThread.start();
	}

	private void runWork() {
		long startTime = System.currentTimeMillis();
		int tenths = 0;
		long normalSleepTime = 100;
		long nextSleepTime = 100;
		DecimalFormat fmt = new DecimalFormat("0.0");

		Runnable updateText = new Runnable() {
				public void run() {
					setText(timeText);
				}
			};

		while ( noStopRequested ) {
			try {
				Thread.sleep(nextSleepTime);

				tenths++;
				long currTime = System.currentTimeMillis();
				long elapsedTime = currTime - startTime;

				nextSleepTime = normalSleepTime + 
					( ( tenths * 100 ) - elapsedTime );

				if ( nextSleepTime < 0 ) {
					nextSleepTime = 0;
				}

				timeText = fmt.format(elapsedTime / 1000.0);
				SwingUtilities.invokeAndWait(updateText);
			} catch ( InterruptedException ix ) {
				// stop running
				return;
			} catch ( InvocationTargetException x ) {
				// If an exception was thrown inside the
				// run() method of the updateText Runnable.
				x.printStackTrace();
			}
		}
	}

	public void stopRequest() {
		noStopRequested = false;
		internalThread.interrupt();
	}

	public boolean isAlive() {
		return internalThread.isAlive();
	}

	public static void main(String[] args) {
		DigitalTimer dt = new DigitalTimer();

		JPanel p = new JPanel(new FlowLayout());
		p.add(dt);

		JFrame f = new JFrame("DigitalTimer Demo");
		f.setContentPane(p);
		f.setSize(250, 100);
		f.setVisible(true);
	}
}
