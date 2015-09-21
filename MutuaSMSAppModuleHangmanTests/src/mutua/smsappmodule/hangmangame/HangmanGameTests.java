package mutua.smsappmodule.hangmangame;

import static org.junit.Assert.*;
import mutua.smsappmodule.hangmangame.HangmanGame;
import mutua.smsappmodule.hangmangame.HangmanGame.EHangmanGameStates;

import org.junit.Test;

/** <pre>
 * HangmanGameTests.java
 * =====================
 * (created by luiz, Dec 18, 2014)
 *
 * This class tests the Hangman Logic
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class HangmanGameTests {
	
	private static void suggestLetter(HangmanGame game, char letter) {
		Boolean isPartOfTheWord = game.suggestLetter(letter);
		System.out.println(Character.toString(letter) + " --> " + game.getGuessedWordSoFar() + (isPartOfTheWord?" Good!":" Wrong... "+game.getNumberOfWrongTriesLeft()));
		switch (game.getGameState()) {
			case WON:
				System.out.println("You won!!");
				break;
			case LOST:
				System.out.println("You lost!!");
				break;
			case PLAYING:
				break;
			default:
				throw new RuntimeException("Unknown gameState "+game.getGameState());
		}
	}

	@Test
	public void testSimpleGameWinningFlow() {
		String word = "anycharsequence";
		int totalNumberOfWrongTries = 6;
		HangmanGame game = new HangmanGame(word, totalNumberOfWrongTries);
		int numberOfWrongTriesLeft = game.getNumberOfWrongTriesLeft();
		String guessedWordSoFar = game.getGuessedWordSoFar();
		assertEquals("Wrong 'number of wrong tries left'", totalNumberOfWrongTries, numberOfWrongTriesLeft);
		System.out.println(game.getGuessedWordSoFar());
		suggestLetter(game, 'n');
		suggestLetter(game, 'n');
		suggestLetter(game, 'n');
		suggestLetter(game, 'n');
		suggestLetter(game, 'n');
		suggestLetter(game, 'n');
		suggestLetter(game, 'i');
		suggestLetter(game, 'o');
		suggestLetter(game, 'u');
		suggestLetter(game, 'y');
		suggestLetter(game, 'c');
		suggestLetter(game, 'h');
		suggestLetter(game, 'r');
		suggestLetter(game, 's');
		suggestLetter(game, 'q');
		assertEquals("Game did not report as being on the WON state", EHangmanGameStates.WON, game.getGameState());
		try {
			suggestLetter(game, 'x');
			fail("Game didn't throw an exception after attempting to play a finished game");
		} catch (RuntimeException e) {}
		// TODO test getAttemptedLettersSoFar as well
		// TODO test for cases like "COCO", where the game is presented in an already solved manner -- the game should refuse the creation of the match, pointing that
		// TODO also, words with spaces or special characters should not be accepted
	}
	
	@Test
	public void testSimpleGameLosingFlow() {
		String word = "wrongword";
		int totalNumberOfWrongTries = 2;
		HangmanGame game = new HangmanGame(word, totalNumberOfWrongTries);
		game.suggestLetter('x');
		game.suggestLetter('x');
		game.suggestLetter('x');
		game.suggestLetter('x');
		game.suggestLetter('y');
		assertEquals("Game did not report as being on the LOST state", EHangmanGameStates.LOST, game.getGameState());
	}
	
	@Test
	public void testGameSerialization() {
		HangmanGame game      = new HangmanGame("coconuts", 6);
		String serializedGame = game.serializeGameState();
		System.out.println("Restarting game with state '"+serializedGame+"'");
		game = new HangmanGame(serializedGame);
		suggestLetter(game, 'o');
		serializedGame = game.serializeGameState();
		game = new HangmanGame(serializedGame);
		suggestLetter(game, 'a');
		serializedGame = game.serializeGameState();
		System.out.println("Restarting game with state '"+serializedGame+"'");
	}
	
	@Test
	public void testUnusualWord() {
		String word = "12345anywordABCDEFGHIJKLMNOPQRSTUVWXYZ67890";
		int totalNumberOfWrongTries = 1;
		HangmanGame game = new HangmanGame(word, totalNumberOfWrongTries);
		game.suggestLetter('x');
		assertEquals("Wrong game state", EHangmanGameStates.PLAYING, game.getGameState());
		assertEquals("Wrong attempted letters so far", "X", game.getAttemptedLettersSoFar());
		assertEquals("Wrong guessed word so far", word.replaceAll("[^01Xx]", "-"), game.getGuessedWordSoFar());
	}

}
