package mutua.icc.instrumentation;

import static mutua.icc.instrumentation.DefaultInstrumentationProperties.*;
import static mutua.icc.instrumentation.JDBCAdapterInstrumentationProperties.*;

/** <pre>
 * JDBCAdapterInstrumentationEvents.java
 * =====================================
 * (created by luiz, Jan 26, 2015)
 *
 * Defines the available events that can participate on instrumentation logs
 *
 * @see JDBCAdapterInstrumentationProperties
 * @version $Id$
 * @author luiz
 */

public enum JDBCAdapterInstrumentationEvents implements IInstrumentableEvent {


	// warnings
	///////////
	
	IE_DATABASE_ADMINISTRATION_WARNING  ("JDBCAdapter Database Administration warning", DIP_MSG),
	
	
	// communications
	
	IE_DATABASE_QUERY ("Database Query", IP_SQL_TEMPLATE, IP_SQL_TEMPLATE_PARAMETERS),

	
	;
	
	
	private InstrumentableEvent instrumentableEvent;
	
	private JDBCAdapterInstrumentationEvents(String name, IInstrumentableProperty property) {
		instrumentableEvent = new InstrumentableEvent(name, property);
	}
	
	private JDBCAdapterInstrumentationEvents(String name, IInstrumentableProperty property1, IInstrumentableProperty property2) {
		instrumentableEvent = new InstrumentableEvent(name, property1, property2);
	}
	
	private JDBCAdapterInstrumentationEvents(String name) {
		instrumentableEvent = new InstrumentableEvent(name);
	}

	@Override
	public InstrumentableEvent getInstrumentableEvent() {
		return instrumentableEvent;
	}

}