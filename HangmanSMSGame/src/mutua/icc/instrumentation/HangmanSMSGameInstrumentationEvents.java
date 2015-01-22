package mutua.icc.instrumentation;


import static mutua.icc.instrumentation.HangmanSMSGameInstrumentationProperties.*;

/** <pre>
 * InstrumentationEvents.java
 * ==========================
 * (created by luiz, Jan 21, 2015)
 *
 * Defines the available events that can participate on instrumentation logs
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class HangmanSMSGameInstrumentationEvents {
	
	public static IInstrumentableEvent IE_REQUEST_FROM_NEW_USER       = new IInstrumentableEvent("Request from new user", IP_PHONE);

	public static IInstrumentableEvent IE_REQUEST_FROM_EXISTING_USER  = new IInstrumentableEvent("Request from existing user", IP_PHONE);

	public static IInstrumentableEvent IE_PROCESSING_COMMAND          = new IInstrumentableEvent("Processing command", IP_COMMAND_INVOCATION);
	
	public static IInstrumentableEvent IE_ANSWER_FROM_COMMAND         = new IInstrumentableEvent("Answer from command", IP_COMMAND_ANSWER);

}
