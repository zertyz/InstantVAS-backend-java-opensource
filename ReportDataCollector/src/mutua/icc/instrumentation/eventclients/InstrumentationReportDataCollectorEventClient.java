package mutua.icc.instrumentation.eventclients;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Hashtable;

import mutua.icc.instrumentation.IInstrumentableEvent;
import mutua.icc.instrumentation.IInstrumentableProperty;
import mutua.icc.instrumentation.InstrumentableEvent;
import mutua.icc.instrumentation.Instrumentation;
import mutua.icc.instrumentation.Instrumentation.EInstrumentationPropagableEvents;
import mutua.icc.instrumentation.Instrumentation.InstrumentationPropagableEvent;
import mutua.icc.instrumentation.dto.InstrumentationEventDto;
import mutua.icc.instrumentation.pour.IInstrumentationPour;
import mutua.icc.instrumentation.pour.PourFactory;
import mutua.icc.instrumentation.pour.PourFactory.EInstrumentationDataPours;
import mutua.serialization.SerializationRepository;

import static mutua.icc.instrumentation.eventclients.EReportDataCollectorInstrumentationEvents.*;
import static mutua.icc.instrumentation.eventclients.EReportDataCollectorInstrumentationProperties.*;

/** <pre>
 * InstrumentationReportDataCollectorEventClient.java
 * ==================================================
 * (created by luiz, Feb 1, 2015)
 *
 * Implements the report data collection instrumentation mechanism
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class InstrumentationReportDataCollectorEventClient implements InstrumentationPropagableEventsClient<EInstrumentationPropagableEvents> {
	private final Instrumentation<?, ?> log;
	private final IInstrumentationPour pour;
	
	private final Hashtable<IInstrumentableEvent, Integer> eventsOfInterest;
	
	
	public InstrumentationReportDataCollectorEventClient(Instrumentation<?, ?> log, EInstrumentationDataPours pourType, String descriptorReference,
	                                                     IInstrumentableEvent... eventsOfInterest) {
		this.log  = log;
		this.pour = PourFactory.getInstrumentationPour(pourType, descriptorReference, EReportDataCollectorInstrumentationProperties.values());
		this.eventsOfInterest = new Hashtable<IInstrumentableEvent, Integer>();
		for (IInstrumentableEvent eventOfInterest : eventsOfInterest) {
			this.eventsOfInterest.put(eventOfInterest, 1);
		}
	}

	@InstrumentationPropagableEvent(EInstrumentationPropagableEvents.APPLICATION_INSTRUMENTATION_EVENT)
	public void handleApplicationInstrumentationEventNotification(InstrumentationEventDto applicationEvent) throws IOException {
		InstrumentableEvent instrumentableEvent = applicationEvent.getEvent();
		String applicationName = applicationEvent.getApplicationName();
		Thread currentThread = Thread.currentThread();
		
		// should we take a hit for this event?
		if (eventsOfInterest.containsKey(instrumentableEvent)) {
			IInstrumentableProperty[] instrumentableProperties = instrumentableEvent.getProperties();
			int numberOfParameters = instrumentableProperties.length;
			InstrumentationEventDto reportDataCollectionEvent = null;
			if (numberOfParameters == 0) {
				reportDataCollectionEvent = new InstrumentationEventDto(System.currentTimeMillis(), applicationName, currentThread,
				                                                        IE_NO_PARAMETER_HIT.getInstrumentableEvent(),
				                                                        IP_HIT_NAME, instrumentableEvent.getName());
			} else if (numberOfParameters == 1) {
				reportDataCollectionEvent = new InstrumentationEventDto(System.currentTimeMillis(), applicationName, currentThread,
				                                                        IE_ONE_PARAMETER_HIT.getInstrumentableEvent(),
				                                                        IP_HIT_NAME,       instrumentableEvent.getName(),
				                                                        IP_HIT_PARAMETER1, applicationEvent.getValue(instrumentableProperties[0]));
			}
			if (reportDataCollectionEvent != null) {
				pour.storeInstrumentableEvent(reportDataCollectionEvent);
			} else {
				log.reportDebug("InstrumentationReportDataCollectorEventClient: don't know how to collect report data for event " + applicationEvent);
			}
		}
	}
	
}


//instrumentation events & properties
//////////////////////////////////////

enum EReportDataCollectorInstrumentationProperties implements IInstrumentableProperty {

	
	IP_HIT_NAME        ("hitName",   String.class),
	IP_HIT_PARAMETER1  ("param1",    String.class),
	IP_HIT_PARAMETER2  ("param2",    String.class),
	IP_HIT_PARAMETER3  ("param3",    String.class),
	
	
	;

	
	private String instrumentationPropertyName;
	private Class<?> instrumentationPropertyType;
	
	
	private EReportDataCollectorInstrumentationProperties(String instrumentationPropertyName, Class<?> instrumentationPropertyType) {
		this.instrumentationPropertyName = instrumentationPropertyName;
		this.instrumentationPropertyType = instrumentationPropertyType;
	}

	
	// IInstrumentableProperty implementation
	/////////////////////////////////////////
	
	@Override
	public String getInstrumentationPropertyName() {
		return instrumentationPropertyName;
	}

	
	// ISerializationRule implementation
	////////////////////////////////////
	
	@Override
	public Class<?> getInstrumentationPropertyType() {
		return instrumentationPropertyType;
	}

	@Override
	public Method getTextualSerializationMethod() {
		return SerializationRepository.getSerializationMethod(instrumentationPropertyType);
	}

}

enum EReportDataCollectorInstrumentationEvents implements IInstrumentableEvent {

	
	IE_NO_PARAMETER_HIT  ("REPORT DATA: NO PARAMETER HIT",  IP_HIT_NAME),
	IE_ONE_PARAMETER_HIT ("REPORT DATA: ONE PARAMETER HIT", IP_HIT_NAME, IP_HIT_PARAMETER1),
	//IE_TWO_PARAMETER_HIT ("REPORT DATA: TWO PARAMETER HIT", IP_HIT_NAME, IP_HIT_PARAMETER1, IP_HIT_PARAMETER2),
	
	
	;
	
	
	private InstrumentableEvent instrumentableEvent;
	
	private EReportDataCollectorInstrumentationEvents(String name, IInstrumentableProperty property) {
		instrumentableEvent = new InstrumentableEvent(name, property);
	}
	
	private EReportDataCollectorInstrumentationEvents(String name, IInstrumentableProperty property1, IInstrumentableProperty property2) {
		instrumentableEvent = new InstrumentableEvent(name, property1, property2);
	}
	
	private EReportDataCollectorInstrumentationEvents(String name) {
		instrumentableEvent = new InstrumentableEvent(name);
	}

	@Override
	public InstrumentableEvent getInstrumentableEvent() {
		return instrumentableEvent;
	}
}