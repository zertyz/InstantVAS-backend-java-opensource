package mutua.smsappmodule.i18n;

import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

/** <pre>
 * PhrasesTests.java
 * =================
 * (created by luiz, May 25, 2016)
 *
 * Tests the main substitution & plugin architecture for the {@link PhrasesTests} infrastructure.
 *
 * @version $Id$
 * @author luiz
 */

public class PhrasesTests {

	@Test
	public void test() {
		fail("Not yet implemented");
	}

	
	/***********************
	** PERFORMANCE SPIKES **
	***********************/
	
	private static String[] performanceSpikesParameters = {
		"param1", "Val1",
		"param2", "Val2, not big",
		"param3", "Val3, a little bit bigger",
		"param4", "Val4",
	};
	
	private static String replaceAllAlgorithmLoop(int count, String subject) {
		long start = System.currentTimeMillis();
		StringBuffer buffer = new StringBuffer(subject.length()*2);
		for (int i=0; i<count; i++) {
			for (int p=0; p<performanceSpikesParameters.length; p+=2) {
				subject = subject.replaceAll("\\{\\{" + performanceSpikesParameters[p] + "\\}\\}", performanceSpikesParameters[p+1]); 
			}
			buffer.append(subject);
			buffer.setLength(0);
		}
		return (System.currentTimeMillis() - start) + " ms ('"+subject+"')";
	}
	
	static Pattern p = Pattern.compile("\\{\\{([^\\}]*)\\}\\}");
	private static String regexAlgorithmLoop(int count, String subject) {
		long start = System.currentTimeMillis();
		StringBuffer buffer = new StringBuffer(subject.length()*2);
		for (int i=0; i<count; i++) {
			buffer.setLength(0);
			Matcher m = p.matcher(subject);
			while (m.find()) {
				String placeHolderName = m.group(1);
				boolean found = false;
				for (int p=0; p<performanceSpikesParameters.length; p+=2) {
					if (placeHolderName.equals(performanceSpikesParameters[p])) {
						m.appendReplacement(buffer, performanceSpikesParameters[p+1]);
						found = true;
						break;
					}
				}
				if (!found) {
					m.appendReplacement(buffer, Matcher.quoteReplacement("{{"+placeHolderName+"}}"));
				}
			}
			m.appendTail(buffer);
		}
		return (System.currentTimeMillis() - start) + " ms ('"+buffer.toString()+"')";
	}

	@Test
	public void performanceTests() {
		String phraseTemplate = "You can play the {{param1}} game in 2 ways: guessing someone's word or inviting someone to play with your word You'll get {{param2}} lucky number(s) each word you guess. Whenever you invite a friend or user to play, you win another lucky number Every week, {{param3}} lucky numbers is(are) selected to win the prize. Send an option to {{param4}}: (J)Play online; (C)Invite a friend or user; (R)anking; (A)Help";
		int loopCount = 1000000;
		System.out.println("ReplaceAll Algorithm: " + replaceAllAlgorithmLoop(loopCount, phraseTemplate));
		System.out.println("Regex Algorithm:      " + regexAlgorithmLoop(loopCount, phraseTemplate));
	}

}
