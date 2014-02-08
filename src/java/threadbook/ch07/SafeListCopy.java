package threadbook.ch07;

import java.util.*;

public class SafeListCopy extends Object {
	public static void printWords(String[] word) {
		System.out.println("word.length=" + word.length);
		for ( int i = 0; i < word.length; i++ ) {
			System.out.println("word[" + i + "]=" + word[i]);
		}
	}

	public static void main(String[] args) {
		// To be safe, only keep a reference to the 
		// *synchronized* list so that you are sure that 
		// all accesses are controlled.
		List wordList = 
				Collections.synchronizedList(new ArrayList());

		wordList.add("Synchronization");
		wordList.add("is");
		wordList.add("important");

		// First technique (favorite)
		String[] wordA = 
				(String[]) wordList.toArray(new String[0]);

		printWords(wordA);

		// Second technique
		String[] wordB;
		synchronized ( wordList ) {
			int size = wordList.size();
			wordB = new String[size];
			wordList.toArray(wordB);
		}

		printWords(wordB);

		// Third technique (the 'synchronized' *is* necessary)
		String[] wordC;
		synchronized ( wordList ) {
			wordC = (String[]) wordList.toArray(
								new String[wordList.size()]);
		}

		printWords(wordC);
	}
}
