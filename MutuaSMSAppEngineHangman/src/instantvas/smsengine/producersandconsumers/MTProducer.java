package instantvas.smsengine.producersandconsumers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import config.InstantVASApplicationConfiguration;
import mutua.events.EventClient;
import mutua.events.EventServer;
import mutua.events.IEventLink;
import mutua.hangmansmsgame.dispatcher.IResponseReceiver;
import mutua.imi.IndirectMethodNotFoundException;
import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsout.dto.OutgoingSMSDto;

/** <pre>
 * MTProducer.java
 * ===============
 * (created by luiz, Mar 16, 2016)
 *
 * Registers the generation of MTs and make them available for delivering through an instance of {@link MTConsumer},
 * transmitted via an instance of {@link IEventLink}.
 *
 * @see IResponseReceiver
 * @version $Id$
 * @author luiz
*/

public class MTProducer extends EventServer<EInstantVASMTEvents> implements IResponseReceiver {
	
	@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.METHOD) public @interface InstantVASMTEvent {
		EInstantVASMTEvents[] value();
	}

	public MTProducer(InstantVASApplicationConfiguration ivac,
	                     EventClient<EInstantVASMTEvents> mtConsumer) {
		super(ivac.MTpcLink);
		try {
			addListener(mtConsumer);
		} catch (IndirectMethodNotFoundException e) {
			ivac.log.reportThrowable(e, "Error while adding mtConsumer");
		}
	}

	@Override
	public void onMessage(OutgoingSMSDto outgoingMessage, IncomingSMSDto incomingMessage) {
		dispatchConsumableEvent(EInstantVASMTEvents.INTERACTIVE_MT, outgoingMessage);
	}
	
}
