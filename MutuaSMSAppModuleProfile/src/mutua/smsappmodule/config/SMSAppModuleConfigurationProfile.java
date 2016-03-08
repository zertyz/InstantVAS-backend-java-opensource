package mutua.smsappmodule.config;

import java.util.Arrays;

import mutua.icc.instrumentation.Instrumentation;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryProfile;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsProfile;
import mutua.smsappmodule.smslogic.SMSAppModuleCommandsProfile;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesProfile;

/** <pre>
 * SMSAppModuleConfigurationProfile.java
 * =====================================
 * (created by luiz, Aug 3, 2015)
 *
 * Configure the classes' default values for new instances of the "Profile SMS Module".
 * 
 * The methods of this class are the ones external users should call in order to create the instances needed to use the module.
 * 
 * Follows the "Instant VAS SMS Modules" pattern described bellow:
 *
 * @see InstantVASSMSAppModuleConfiguration
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleConfigurationProfile {

	/** Constructs the simple version of this SMS Module, with default values, for testing purposes.<pre>
	 *  @param shortCode         &
	 *  @param appName           see {@link SMSAppModulePhrasingsProfile#SMSAppModulePhrasingsProfile(String, String, String, String, String, String, String, String)}
	 *  @param profileModuleDAL  see {@link SMSAppModuleCommandsProfile#SMSAppModuleCommandsProfile}
	 *  @returns {(SMSAppModuleNavigationStatesProfile)navigationStates, (SMSAppModuleCommandsProfile)commands, (SMSAppModulePhrasingsProfile)phrasings} */
	public static Object[] getProfileModuleInstances(String shortCode, String appName,
	                                                 SMSAppModuleDALFactoryProfile profileModuleDAL) {
		SMSAppModulePhrasingsProfile        phrasings        = new SMSAppModulePhrasingsProfile(shortCode, appName);
		SMSAppModuleCommandsProfile         commands         = new SMSAppModuleCommandsProfile(phrasings, profileModuleDAL);
		SMSAppModuleNavigationStatesProfile navigationStates = new SMSAppModuleNavigationStatesProfile(commands);
		
		System.err.println(SMSAppModuleConfigurationProfile.class.getCanonicalName() + ": test configuration loaded.");
		
		return new Object[] {navigationStates, commands, phrasings};
	}
	
	/** Constructs the full version of this SMS Module, with all options set programmatically.<pre>
	 *  @param log
	 *  @param shortCode                            &
	 *  @param appName                              &
	 *  @param phrAskForFirstNickname               &
	 *  @param phrAskForNewNickname                 &
	 *  @param phrAskForNicknameCancelation         & 
	 *  @param phrNicknameRegistrationNotification  &
	 *  @param phrUserProfilePresentation           &
	 *  @param phrNicknameNotFound                  see {@link SMSAppModulePhrasingsProfile#SMSAppModulePhrasingsProfile(String, String, String, String, String, String, String, String)}
	 *  @param profileModuleDAL                     see {@link SMSAppModuleCommandsProfile#SMSAppModuleCommandsProfile}
	 *  @param nstRegisteringNicknameTriggers       see {@link SMSAppModuleNavigationStatesProfile#SMSAppModuleNavigationStatesProfile(SMSAppModuleCommandsProfile, Object[][])}
	 *  @returns {(SMSAppModuleNavigationStatesProfile)navigationStates, (SMSAppModuleCommandsProfile)commands, (SMSAppModulePhrasingsProfile)phrasings} */
	public static Object[] getProfileModuleInstances(Instrumentation<?, ?> log, String shortCode, String appName,
		                                             String phrAskForFirstNickname,
		                                             String phrAskForNewNickname,
		                                             String phrAskForNicknameCancelation,
		                                             String phrNicknameRegistrationNotification,
		                                             String phrUserProfilePresentation,
		                                             String phrNicknameNotFound,
		                                             SMSAppModuleDALFactoryProfile profileModuleDAL,
		                                             Object[][] nstRegisteringNicknameTriggers) {
		
		SMSAppModulePhrasingsProfile        phrasings        = new SMSAppModulePhrasingsProfile(shortCode, appName,
			phrAskForFirstNickname, phrAskForNewNickname, phrAskForNicknameCancelation, phrNicknameRegistrationNotification,
			phrUserProfilePresentation, phrNicknameNotFound);
		SMSAppModuleCommandsProfile         commands         = new SMSAppModuleCommandsProfile(phrasings, profileModuleDAL);
		SMSAppModuleNavigationStatesProfile navigationStates = new SMSAppModuleNavigationStatesProfile(commands, nstRegisteringNicknameTriggers);
		
		// log
		String logPrefix = "Profile Module";
		log.reportDebug(logPrefix + ": new instances:");
		Object[][] logPhrasings = {
			{"phrAskForFirstNickname",              phrAskForFirstNickname},
			{"phrAskForNewNickname",                phrAskForNewNickname},
			{"phrAskForNicknameCancelation",        phrAskForNicknameCancelation},
			{"phrNicknameRegistrationNotification", phrNicknameRegistrationNotification},
			{"phrUserProfilePresentation",          phrUserProfilePresentation},
			{"phrNicknameNotFound",                 phrNicknameNotFound},
		};
		log.reportDebug(logPrefix + ": Phrasings        : " + Arrays.deepToString(logPhrasings));
		Object[][] logCommands = {
			{"profileModuleDAL",      profileModuleDAL},
		};
		log.reportDebug(logPrefix + ": Commands         : " + Arrays.deepToString(logCommands));
		Object[][] logCommandTriggers = {
			{"nstRegisteringNicknameTriggers", nstRegisteringNicknameTriggers},	
		};
		log.reportDebug(logPrefix + ": Navigation States: " + Arrays.deepToString(logCommandTriggers));

		return new Object[] {navigationStates, commands, phrasings};
	}
}
