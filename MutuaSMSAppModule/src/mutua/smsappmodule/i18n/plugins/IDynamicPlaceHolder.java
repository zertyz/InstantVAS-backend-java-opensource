package mutua.smsappmodule.i18n.plugins;

import mutua.smsappmodule.i18n.Phrase;

/** <pre>
 * IDynamicPlaceHolder.java
 * ========================
 * (created by luiz, May 25, 2016)
 *
 * Interface specifying {@link Phrase} plugins, that act as dynamic values
 * for phrase place holders.
 *
 * @version $Id$
 * @author luiz
 */

// TODO 20160525 -- Fatorar esta classe e todo o mecanismo de plugins para fora deste método e implementar um real mecanismo de plug-in
//      pois atualmente não se pode tirar nem por quantos você quiser 

public interface IDynamicPlaceHolder {
	
	/** Called whenever a {@link Phrase} instance wants to resolve a {@link IDynamicPlaceHolder} place holder previously set with Phrase#addDynamicPlaceHolder.
	 *  Should return the String value to be placed where 'placeHolderName' appears on the text.
	 *  For the calculation, the implemented method will have the same parameters the Phrase object receives.
	 *  @param phraseParameters := {Param1Name, Param1Val, ...} */
	String getPlaceHolderValue(String placeHolderName, String[] phraseParameters);

}
