package instantvas.smsengine.producersandconsumers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import config.InstantVASApplicationConfiguration;
import mutua.events.EventClient;
import mutua.events.EventServer;
import mutua.events.IEventLink;
import mutua.imi.IndirectMethodNotFoundException;
import mutua.smsin.dto.IncomingSMSDto;

/** <pre>
 * MOProducer.java
 * ===============
 * (created by luiz, Mar 16, 2016)
 *
 * Registers the reception of MOs and make them available for processing through an instance of {@link MOConsumer},
 * transmitted via an instance of {@link IEventLink}.
 *
 * @version $Id$
 * @author luiz
*/

public class MOProducer extends EventServer<EInstantVASMOEvents> {
	
	@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.METHOD) public @interface InstantVASMOEvent {
		EInstantVASMOEvents[] value();
	}
	
	public MOProducer(InstantVASApplicationConfiguration ivac,
	                  EventClient<EInstantVASMOEvents> moConsumer) {
		super(ivac.MOpcLink);
		try {
			setConsumer(moConsumer);
		} catch (IndirectMethodNotFoundException e) {
			ivac.log.reportThrowable(e, "Error while setting moConsumer");
		}
	}
	
	public int dispatchMOForProcessing(IncomingSMSDto mo) {
		return dispatchConsumableEvent(EInstantVASMOEvents.MO_ARRIVED, mo);
	}
	
//	public boolean addToSubscribeUserQueue(String phone) {
//		return dispatchNeedToBeConsumedEvent(EHangmanGameStates.WON, phone);
//	}

}
