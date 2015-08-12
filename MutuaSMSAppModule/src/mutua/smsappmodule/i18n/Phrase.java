package mutua.smsappmodule.i18n;

import java.util.Arrays;
import java.util.regex.Matcher;

/** <pre>
 * Phrase.java
 * ===========
 * (created by luiz, Jul 13, 2015)
 *
 * This class represents a single phrase on the universe of the SMS
 * Application's phrasing. It also specifies methods of how to replace
 * parameters.
 * 
 * Serviced classes should use the Mutua SMSApp Phrasing design pattern, described
 * bellow:
 * 
 * {@code
 * 	get it from the help module by now
 * }
 *
 * @see ??IPhrasingCenter??
 * @version $Id$
 * @author luiz
 */

public class Phrase {
	
	
	/*********************
	** INSTANCE METHODS **
	*********************/
	
	/** Typically a phrase is a single string. It can be two or more strings to specify two or more messages --
	 * possibly to be sent in sequence, possibly to be picked randomly to alter between different wordings */
	private String[] phrases;
	
	protected Phrase(String... phrases) {
		this.phrases = phrases;
	}
	
	/** This method is intended to be used by configuration classes to allow changes on the default wordings.
	 * When changed, the new phrase(s) will be readly available. */
	public void setPhrases(String... phrases) {
		this.phrases = phrases;
	}
	
	/** Return the phrases defined in this instance */
	public String[] getPhrases() {
		return phrases;
	}

	
	/*******************
	** GLOBAL METHODS **
	*******************/
	
	/** retrieves the messages for the current 'phrase', fulfilling all parameters, where
	 *  'parameters' := {"name1", "value1", "name2", "value2", ...}, or 'null' if 'phraseName' wasn't found */
	protected String[] getPhrases(String... parameters) {
		if (phrases == null) {
			return null;
		}
		if ((parameters != null) && (parameters.length >= 2)) {
			String[] replacedPhrases = new String[phrases.length];
			for (int i=0; i<phrases.length; i++) {
				String originalMessage = phrases[i];
				String fulfilledMessage = originalMessage;
				// fulfill the parameters
				for (int j=0; j<(parameters.length-1); j+=2) {
					String parameterName  = parameters[j+0];
					String parameterValue = parameters[j+1];
					if (parameterValue != null) {
						fulfilledMessage = fulfilledMessage.replaceAll("\\{\\{" + parameterName + "\\}\\}", Matcher.quoteReplacement(parameterValue));
					}
				}
				replacedPhrases[i] = fulfilledMessage;
			}
			phrases = replacedPhrases;
		}
		return phrases;
	}
	
	/** @see IPhraseology#getPhrases(EPhraseNames, String[][]) */
	protected String getPhrase(String... parameters) {
		String[] fullfiledPhrases = getPhrases(parameters);
		if (fullfiledPhrases == null) {
			return null;
		} else {
			return fullfiledPhrases[0];
		}
	}
	
	/** @see IPhraseology#getPhrases(EPhraseNames, String[][]) */
	protected String getPhrase() {
		return getPhrase((String[])null);
	}


	/*********************
	** AUXILIAR METHODS **
	*********************/
	
	/** Concatenates all 'elements' separating them with 'pairSeparator' and 'lastPairSeparator' */
	protected static String getList(String[] elements, String pairSeparator, String lastPairSeparator) {
        String list = "";
        for (int i=0; i<elements.length; i++) {
                list += elements[i];
                if (i == (elements.length-2)) {
                        list += lastPairSeparator;
                } else if (i != (elements.length-1)) {
                        list += pairSeparator;
                }
        }
        return list;
	}
}
