package instantvas.smsengine.producersandconsumers;

import static config.InstantVASLicense.*;
import static instantvas.smsengine.SMSAppEngineInstrumentationMethods.reportMOQueueAddition;

import java.util.Arrays;

import config.InstantVASInstanceConfiguration;
import mutua.events.EventClient;
import mutua.events.EventServer;
import mutua.events.IEventLink;
import mutua.icc.instrumentation.Instrumentation;
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

public class MOProducer extends EventServer<EInstantVASEvents> implements IMOProducer {
	
	public MOProducer(InstantVASInstanceConfiguration ivac,
	                  EventClient<EInstantVASEvents> moConsumer) {
		super(ivac.MOpcLink);
		try {
			setConsumer(moConsumer);
		} catch (IndirectMethodNotFoundException e) {
			Instrumentation.reportThrowable(e, "Error while setting moConsumer");
		}
	}
	
	@Override
	public int dispatchMOForProcessing(IncomingSMSDto mo) {
		
		int moId;

		moId = dispatchConsumableEvent(EInstantVASEvents.MO_ARRIVED, mo);

		reportMOQueueAddition(moId, mo);

		return moId;
	}

	@Override
	public int[] dispatchMOsForProcessing(IncomingSMSDto[] moSet) {
		int[] results = dispatchConsumableEvents(EInstantVASEvents.MO_ARRIVED, moSet);
		System.out.println("#### while inserting     : " + Arrays.toString(moSet));
		System.out.println("#### batch MO results are: " + Arrays.toString(results));
		return results;
	}

}