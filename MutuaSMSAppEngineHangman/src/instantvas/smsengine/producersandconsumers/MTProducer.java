package instantvas.smsengine.producersandconsumers;

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

public class MTProducer extends EventServer<EInstantVASEvents> implements IResponseReceiver {

	public MTProducer(InstantVASApplicationConfiguration ivac,
	                  EventClient<EInstantVASEvents> mtConsumer) {
		super(ivac.MTpcLink);
		try {
			setConsumer(mtConsumer);
		} catch (IndirectMethodNotFoundException e) {
			ivac.log.reportThrowable(e, "Error while adding mtConsumer");
		}
	}

	@Override
	public void onMessage(OutgoingSMSDto outgoingMessage, IncomingSMSDto incomingMessage) {
		dispatchConsumableEvent(EInstantVASEvents.INTERACTIVE_MT, outgoingMessage);
	}
	
}
