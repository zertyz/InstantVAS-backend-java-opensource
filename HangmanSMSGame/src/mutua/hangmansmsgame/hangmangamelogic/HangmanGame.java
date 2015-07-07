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

	public HangmanGame(String serializedGame) {
		desserializeGameState(serializedGame);
	}

	public boolean suggestLetter(char letter) {
		if (gameState != EHangmanGameStates.PLAYING) {
			throw new RuntimeException("Attempted to play a hangman game which is not in the 'PLAYING' state. The game state is "+gameState);
		}
		letter = Character.toUpperCase(letter);
		if (word.matches(".*" + letter + ".*")) {
			attemptedLetters.put(letter, true);
			computeGameState();
			return true;
		} else {
			if (!getAttemptedLettersSoFar().matches(".*" + letter + ".*")) {
				numberOfWrongTriesLeft--;
				attemptedLetters.put(letter, true);
				computeGameState();
				return false;
			}
			return true;
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
	
	public String getAttemptedLettersSoFar() {
		char[] possibleCharacters = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
		char[] usedCharacters = new char[possibleCharacters.length];
		int usedCharactersLength = 0;
		for (char possibleCharacter : possibleCharacters) {
			if (attemptedLetters.containsKey(possibleCharacter)) {
				usedCharacters[usedCharactersLength++] = possibleCharacter;
			}
		}
		return new String(usedCharacters, 0, usedCharactersLength);
	}
	
	private void computeGameState() {
		if (numberOfWrongTriesLeft <= 0) {
			gameState = EHangmanGameStates.LOST;
		}
		if (word.equals(getGuessedWordSoFar())) {
			gameState = EHangmanGameStates.WON;
		}
	}
	
	public EHangmanGameStates getGameState() {
		return gameState;
	}

	public String getWord() {
		return word;
	}

	private void desserializeGameState(String state) {
		String[] variables = state.split(";");
		gameState          = EHangmanGameStates.valueOf(variables[0]);
		word               = variables[1];
		numberOfWrongTries = Integer.parseInt(variables[2]);
		attemptedLetters   = new Hashtable<Character, Boolean>();
		for (int i=0; i<variables[3].length(); i++) {
			attemptedLetters.put(variables[3].charAt(i), true);
		}
		numberOfWrongTriesLeft = Integer.parseInt(variables[4]);
	}
	
	public String serializeGameState() {
		return new StringBuffer().
		       append(gameState.name()).append(';').
		       append(word).append(';').
		       append(numberOfWrongTries).append(';').
		       append(getAttemptedLettersSoFar()).append(';').
		       append(numberOfWrongTriesLeft).toString();
	}

}
