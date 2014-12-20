package mutua.hangmansmsgame.hangmangamelogic;

import java.util.Hashtable;

/** <pre>
 * HangmanGame.java
 * ================
 * (created by luiz, Dec 18, 2014)
 *
 * This class implements the Hangman Game logic with state persistence -- suitable for an SMS Application
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class HangmanGame {
	
	public enum EHangmanGameStates {PLAYING, LOST, WON};
	
	private EHangmanGameStates gameState;
	private String word;
	private int numberOfWrongTries;
	private Hashtable<Character, Boolean> attemptedLetters;
	private int numberOfWrongTriesLeft;

	public HangmanGame(String word, int numberOfWrongTries) {
		gameState = EHangmanGameStates.PLAYING;
		this.numberOfWrongTries = numberOfWrongTries;
		numberOfWrongTriesLeft = numberOfWrongTries;
		this.word = word.toUpperCase();
		attemptedLetters = new Hashtable<Character, Boolean>();
		suggestLetter(word.substring(0, 1).toCharArray()[0]);
		suggestLetter(word.substring(word.length()-1).toCharArray()[0]);
	}

	public boolean suggestLetter(char letter) {
		if (gameState != EHangmanGameStates.PLAYING) {
			throw new RuntimeException("Attempted to play a hangman game which is not in the 'PLAYING' state. The game state is "+gameState);
		}
		letter = Character.toUpperCase(letter);
		attemptedLetters.put(letter, true);
		if (word.matches(".*" + letter + ".*")) {
			computeGameState();
			return true;
		} else {
			numberOfWrongTriesLeft--;
			computeGameState();
			return false;
		}
	}

	public int getNumberOfWrongTriesLeft() {
		return numberOfWrongTriesLeft;
	}
	
	public String getGuessedWordSoFar() {
		char[] wordCharacters = word.toCharArray();
		char[] guessedWordSoFar = new char[wordCharacters.length];
		for (int i=0; i<wordCharacters.length; i++) {
			if (attemptedLetters.containsKey(wordCharacters[i])) {
				guessedWordSoFar[i] = wordCharacters[i];
			} else {
				guessedWordSoFar[i] = '-';
			}
		}
		return new String(guessedWordSoFar);
	}
	
	private void computeGameState() {
		if (numberOfWrongTries <= 0) {
			gameState = EHangmanGameStates.LOST;
		}
		if (word.equals(getGuessedWordSoFar())) {
			gameState = EHangmanGameStates.WON;
		}
	}
	
	public EHangmanGameStates getGameState() {
		return gameState;
	}

}
