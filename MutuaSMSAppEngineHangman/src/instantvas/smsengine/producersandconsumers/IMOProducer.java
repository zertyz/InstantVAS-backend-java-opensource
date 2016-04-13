package instantvas.smsengine.producersandconsumers;

import mutua.smsin.dto.IncomingSMSDto;

/** <pre>
 * IMOProducer.java
 * ================
 * (created by luiz, Mar 29, 2016)
 *
 * Specifies what constitutes a 'MOProducer' -- created to allow flexibility while jUnit testing
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
*/

public interface IMOProducer {
	
	/** Called to inform a 'MO Just Arrived'. Returns the registered event id */
	int dispatchMOForProcessing(IncomingSMSDto mo);
	
	/** Similar to {@link #dispatchMOForProcessing}, but perform all at once */
	int[] dispatchMOsForProcessing(IncomingSMSDto[] moSet);

}
