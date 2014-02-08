package threadbook.ch07;

import java.util.*;

public class SafeCollectionIteration extends Object {
	public static void main(String[] args) {
		// To be safe, only keep a reference to the 
		// *synchronized* list so that you are sure 
		// that all accesses are controlled.

		// The collection *must* be synchronized 
		// (a List in this case).
		List wordList = 
				Collections.synchronizedList(new ArrayList());

		wordList.add("Iterators");
		wordList.add("require");
		wordList.add("special");
		wordList.add("handling");

		// All of this must be in a synchronized block to 
		// block other threads from modifying wordList while 
		// the iteration is in progress.
		synchronized ( wordList ) {
			Iterator iter = wordList.iterator();
			while ( iter.hasNext() ) {
				String s = (String) iter.next();
				System.out.println("found string: " + s + 
					", length=" + s.length());
			}
		}
	}
}
