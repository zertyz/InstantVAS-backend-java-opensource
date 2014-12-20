package mutua.hangmansmsgame.hangmangamelogic;

import static org.junit.Assert.*;
import mutua.hangmansmsgame.hangmangamelogic.HangmanGame;
import mutua.hangmansmsgame.hangmangamelogic.HangmanGame.EHangmanGameStates;

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
	public void testSimpleGameFlow() {
		String word = "anycharsequence";
		int totalNumberOfWrongTries = 6;
		HangmanGame game = new HangmanGame(word, 6);
		int numberOfWrongTriesLeft = game.getNumberOfWrongTriesLeft();
		String guessedWordSoFar = game.getGuessedWordSoFar();
		assertEquals("Wrong 'number of wrong tries left'", totalNumberOfWrongTries, numberOfWrongTriesLeft);
		System.out.println(game.getGuessedWordSoFar());
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
		//boolean isTheRightWord = game.tryToGuessTheWholeWord();
		suggestLetter(game, 'x');
	}

}
