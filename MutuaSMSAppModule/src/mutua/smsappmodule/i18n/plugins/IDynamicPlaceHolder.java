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
 * Generic Phrase plugins must count only on 'phraseParameters' and other globally available information --
 * regarding the MO user -- to generate it's value replacement.
 *
// TODO 20160525 -- O mecanismo de plugins não está totalmente implementado. Um real mecanismo de plugins deve ser livremente adicionado ou não
//      -- isto é: deve ter o módulo Phrases totalmente desacoplado das n implementações -- e deve permitir a adição de qualquer (quaisquer) das
//      n implementações 

 * @version $Id$
 * @author luiz
 */


public interface IDynamicPlaceHolder {
	
	/** Called whenever a {@link Phrase} instance wants to resolve a {@link IDynamicPlaceHolder} previously set with Phrase#addDynamicPlaceHolder.
	 *  Should return the String value to be placed where 'placeHolderName' appears on the text.
	 *  For the calculation, the implemented method will have the same parameters the Phrase object receives and other globally accessible information,
	 *  such as the MO UserDto, database instances, session, etc.
	 *  @param phraseParameters := {Param1Name, Param1Val, ...} */
	String getPlaceHolderValue(String placeHolderName, String[] phraseParameters);

}
