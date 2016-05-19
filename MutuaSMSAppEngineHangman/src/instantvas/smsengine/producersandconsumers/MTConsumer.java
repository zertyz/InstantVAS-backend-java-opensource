package instantvas.smsengine.producersandconsumers;

import static config.InstantVASLicense.*;
import static instantvas.smsengine.SMSAppEngineInstrumentationMethods.*;

import config.InstantVASInstanceConfiguration;
import mutua.events.EventClient;
import mutua.events.IEventLink;
import mutua.icc.instrumentation.Instrumentation;
import mutua.smsout.dto.OutgoingSMSDto;
import mutua.smsout.senders.SMSOutSender;

/** <pre>
 * MTConsumer.java
 * ===============
 * (created by luiz, Mar 16, 2016)
 *
 * Processes an MT generated via an instance of {@link MTProducer}, transmitted via an instance of {@link IEventLink}
 *
 * @version $Id$
 * @author luiz
*/

public class MTConsumer implements EventClient<EInstantVASEvents> {
	
	private SMSOutSender mtSender;
	
	public MTConsumer(InstantVASInstanceConfiguration ivac) {
		this.mtSender = ivac.mtSender;
	}
	
	// TODO 18/5/2016 -- corrigir a arquitetura de Instancias: Filas devem ter um IFDEF para incluir um campo equivalente a AUTHENTICATION_TOKEN, e
	//      MO/MT Producers & Consumers devem consultar este campo para descobrir para onde as mensagens devem ser direcionadas. Este campo pode ser
	//      IFDEFed com o fato de se ter definido como estático mais de um AUTHENTICATION_TOKEN
	
	@InstantVASEvent(EInstantVASEvents.INTERACTIVE_MT)
	public void sendMT(OutgoingSMSDto mt) {
		
		startMTDeliveryRequest(mt);
		
		// deliver the MT
		try {
			mtSender.sendMessage(mt);
		} catch (Throwable t) {
			Instrumentation.reportThrowable(t, "Error while sending MT -- " + mt.toString());
		}
		
		finishRequest();
		
	}
	
}