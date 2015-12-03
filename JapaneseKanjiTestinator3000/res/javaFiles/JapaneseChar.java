package javaFiles;
import java.util.HashSet;

public class JapaneseChar {
	private int myRate;
	
	public int myXPos,myYPos;
	public String myKanji;
	public HashSet<String> myRomaji;
	
	public JapaneseChar(String kanji, HashSet<String> romaji){
		myRomaji = romaji;
		myKanji = kanji;
	}
	
	public JapaneseChar(String kanji, HashSet<String> romaji, int xPos, int yPos, int rate){
		myRate = rate;
		myRomaji = romaji;
		myKanji = kanji;
		myXPos = xPos;
		myYPos = yPos;
	}
	
	public void posUpdate(){
		myYPos += myRate;
	}
	
	@Override
	public boolean equals(Object other){
		JapaneseChar test = (JapaneseChar) other;
		if (myKanji.equals(test.myKanji) && myRomaji.equals(test.myRomaji)){
			return true;
		}
		
		return false;
	}
	
}
