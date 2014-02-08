package threadbook.ch11;

public class InnerSelfRunMain extends Object {
	public static void main(String[] args) {
		InnerSelfRun sr = new InnerSelfRun();

		try { Thread.sleep(3000); } catch ( InterruptedException x ) { }

		sr.stopRequest();
	}
}
