package mutua.smsappmodule;

import mutua.smsappmodule.config.SMSAppModuleConfigurationHelp;
import mutua.smsappmodule.smslogic.commands.CommandTriggersDto;
import mutua.smsappmodule.smslogic.commands.ICommandProcessor;
import mutua.smsappmodule.smslogic.navigationstates.INavigationState;
import mutua.smsappmodule.smslogic.navigationstates.NavigationStateCommons;

/** <pre>
 * HelpModuleTestCommons.java
 * ==========================
 * (created by luiz, Jul 23, 2015)
 *
 * Contains entities common to more than 1 test classes
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class HelpModuleTestCommons {
	
	public enum ENavStates implements INavigationState {
		STATE1,
		STATE2,
		STATE3,
		;
		private NavigationStateCommons nsc;
		private ENavStates() {
			nsc = new NavigationStateCommons(this);
		}		
		public String getNavigationStateName() {return this.name();}
		public CommandTriggersDto[] getCommandTriggers() {return null;}
		public void setCommandTriggers(Object[][] commandsTriggersData) {}
		public String[] serializeCommandTrigger(ICommandProcessor command) {return null;}
		public void deserializeCommandTrigger(String[] serializedData) {}
		public void setCommandTriggersFromConfigurationValues() {}
	}
	
	/** navigation-state-aware help message for STATE1 */
	public static String expectedSTATE1HelpMessage = "This is the help for STATE1";
	/** navigation-state-aware help message for STATE2 */
	public static String expectedSTATE2HelpMessage = "This is the help for STATE2";
	/** prepares the navigation-state-aware help messages for STATE1 and STATE2 */
	public static void setStatefulHelpMessages() {
		SMSAppModuleConfigurationHelp.setStatefulHelpMessages(new Object[][] {
			{ENavStates.STATE1, expectedSTATE1HelpMessage},
			{ENavStates.STATE2, expectedSTATE2HelpMessage},
		});		
	}
	


}
