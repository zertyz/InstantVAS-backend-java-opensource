package mutua.smsappmodule.i18n;

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
 * Serviced classes should use the "Instant VAS SMSApp Phrasing" design pattern, described
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

// TODO 20160525 -- aplicar o novo algoritmo de substituições demonstrado em 'PhrasesTests'

public class Phrase {
	
	
	/*********************
	** INSTANCE METHODS **
	*********************/
	
	private final String[] phrasesTemplates;
	
	/** Typically a phrase is a single string. It can be two or more strings to specify two or more messages --
	 * possibly to be sent in sequence, possibly to be picked randomly to alter between different wordings */
	protected Phrase(String... phrasesTemplates) {
		this.phrasesTemplates = phrasesTemplates;
	}
	
	/** This constructor is used to promptly expand the given parameters at instance construction time, allowing
	 *  the #getPhrases() or #getPhrase() methods to be called without the need of passing the same parameters again.
	 *  In other words: in order for this constructor to be useful, the passed parameters should be constant through
	 *  the lifetime of this instance. */
	protected Phrase(String[] parameters, String... phrasesTemplates) {
		this.phrasesTemplates = getExpandedPhrases(phrasesTemplates, parameters);
	}
	
	/** Return the phrases defined in this instance, without any parameter expansion */
	protected String[] getPhrases() {
		return phrasesTemplates;
	}

	/** Returns the phrase passed to the constructor, without any additional parameter expansion */
	protected String getPhrase() {
		return phrasesTemplates[0];
	}
	
	/** @see #getExpandedPhrase(String, String...) */
	protected String[] getPhrases(String... parameters) {
		return getExpandedPhrases(phrasesTemplates, parameters);
	}

	/** @see #getExpandedPhrase(String, String...) */
	protected String getPhrase(String... parameters) {
		return getExpandedPhrase(phrasesTemplates[0], parameters);
	}
	
	
	/*******************
	** GLOBAL METHODS **
	*******************/
	
	/** retrieves the messages for the current 'phraseTemplate', fulfilling all parameters, where
	 *  'parameters' := {"name1", "value1", "name2", "value2", ...}, or 'null' if 'phraseName' wasn't found */
	public static String getExpandedPhrase(String phraseTemplate, String... parameters) {
		if (phraseTemplate == null) {
			return null;
		}
		String expandedPhrase = phraseTemplate;
		// fulfill the parameters
		for (int j=0; j<(parameters.length-1); j+=2) {
			String parameterName  = parameters[j+0];
			String parameterValue = parameters[j+1];
			if (parameterValue != null) {
				expandedPhrase = expandedPhrase.replaceAll("\\{\\{" + parameterName + "\\}\\}", Matcher.quoteReplacement(parameterValue));
			}
		}
		return expandedPhrase;
	}
	
	/** @see #getExpandedPhrase(String, String...) */
	public static  String[] getExpandedPhrases(String[] phraseTemplates, String[] parameters) {
		if (phraseTemplates == null) {
			return null;
		}
		String[] expandedPhrases = new String[phraseTemplates.length];
		for (int i=0; i<phraseTemplates.length; i++) {
			expandedPhrases[i] = getExpandedPhrase(phraseTemplates[i], parameters);
		}
		return expandedPhrases;
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
