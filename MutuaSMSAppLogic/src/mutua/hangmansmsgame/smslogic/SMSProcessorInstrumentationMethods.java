package mutua.hangmansmsgame.smslogic;

import mutua.icc.instrumentation.InstrumentableEvent;
import mutua.icc.instrumentation.InstrumentableProperty;
import mutua.icc.instrumentation.InstrumentableEvent.ELogSeverity;
import mutua.icc.instrumentation.Instrumentation;
import mutua.smsappmodule.smslogic.commands.CommandAnswerDto;
import mutua.smsappmodule.smslogic.commands.CommandInvocationDto;
import mutua.smsappmodule.smslogic.navigationstates.NavigationState;

/** <pre>
 * SMSProcessorInstrumentationMethods.java
 * =======================================
 * (created by luiz, May 5, 2016)
 *
 * Helper Instrumentation class concentrating definitions & calls to all
 * instrumentation events used by this project
 *
 * @version $Id$
 * @author luiz
 */

public class SMSProcessorInstrumentationMethods {
	
	// 'InstrumentableEvent's
	private static final InstrumentableEvent requestFromNewUserEvent;
	private static final InstrumentableEvent requestFromExistingUserEvent;
	private static final InstrumentableEvent answerFromCommandEvent;
	
	// 'InstrumentableProperty'ies
	private static final InstrumentableProperty phoneProperty;
	private static final InstrumentableProperty textProperty;
	private static final InstrumentableProperty navigationStateProperty;
	private static final InstrumentableProperty commandInvocationProperty;
	private static final InstrumentableProperty commandAnswerProperty;
	
	static {
		phoneProperty             = new InstrumentableProperty("moPhone",                  String.class);
		textProperty              = new InstrumentableProperty("moText",                   String.class);
		navigationStateProperty   = new InstrumentableProperty("navigationState",          NavigationState.class);
		commandInvocationProperty = new InstrumentableProperty("commandInvocationHandler", CommandInvocationDto.class);
		commandAnswerProperty     = new InstrumentableProperty("commandAnswer",            CommandAnswerDto.class);

		requestFromNewUserEvent      = new InstrumentableEvent("Request from new user",       ELogSeverity.CRITICAL);
		requestFromExistingUserEvent = new InstrumentableEvent("Request from existing user",  ELogSeverity.CRITICAL);
		answerFromCommandEvent       = new InstrumentableEvent("Answer from command",         ELogSeverity.CRITICAL, commandInvocationProperty);
	}
	
	public static void reportRequestFromNewUser(String moPhone, String moText) {
		Instrumentation.logAndCompute(requestFromNewUserEvent, phoneProperty, moPhone, textProperty, moText);
	}
	
	public static void reportRequestFromExistingUser(String moPhone, String moText, NavigationState navigationState) {
		Instrumentation.logAndCompute(requestFromExistingUserEvent, navigationStateProperty, navigationState, phoneProperty, moPhone, textProperty, moText);
	}

	public static void reportAnswerFromCommand(String moPhone, CommandInvocationDto invocationHandler, CommandAnswerDto commandAnswer) {
		Instrumentation.logAndCompute(answerFromCommandEvent, phoneProperty, moPhone, commandInvocationProperty, invocationHandler, commandAnswerProperty, commandAnswer);
	}
}
