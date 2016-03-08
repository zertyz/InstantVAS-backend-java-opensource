package mutua.smsappmodule.config;

import java.util.Arrays;

import mutua.icc.instrumentation.Instrumentation;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryHangman;
import mutua.smsappmodule.dal.SMSAppModuleDALFactoryProfile;
import mutua.smsappmodule.i18n.SMSAppModulePhrasingsHangman;
import mutua.smsappmodule.smslogic.SMSAppModuleCommandsHangman;
import mutua.smsappmodule.smslogic.SMSAppModuleEventsSubscription;
import mutua.smsappmodule.smslogic.navigationstates.SMSAppModuleNavigationStatesHangman;

/** <pre>
 * SMSAppModuleConfigurationHangman.java
 * =====================================
 * (created by luiz, Sep 18, 2015)
 *
 * Configure the classes' default values for new instances of the "Hangman SMS Module".
 * 
 * The methods of this class are the ones external users should call in order to create the instances needed to use the module.
 * 
 * Follows the "Instant VAS SMS Modules" pattern described bellow:
 *
 * @see InstantVASSMSAppModuleConfiguration
 * @version $Id$
 * @author luiz
 */

public class SMSAppModuleConfigurationHangman {

	/** Constructs the simple version of this SMS Module, with default values, for testing purposes.<pre>
	 *  @param shortCode                &
	 *  @param appName                  see {@link SMSAppModulePhrasingsHangman#SMSAppModulePhrasingsHangman(String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String)}
	 *  @param subscriptionEventsServer one of the values returned by {@link SMSAppModuleConfigurationSubscription#getSubscriptionModuleInstances}
	 *  @param baseModuleDAL            &
	 *  @param profileModuleDAL         &
	 *  @param hangmanModuleDAL         &
	 *  @param defaultNicknamePrefix    see {@link SMSAppModuleCommandsHangman#SMSAppModuleCommandsHangman(SMSAppModulePhrasingsHangman, SMSAppModuleDALFactory, SMSAppModuleDALFactoryProfile, SMSAppModuleDALFactoryHangman, String)}
	 *  @returns {(SMSAppModuleNavigationStatesHangman)navigationStates, (SMSAppModuleCommandsHangman)commands, (SMSAppModulePhrasingsHangman)phrasings} */
	public static Object[] getHangmanModuleInstances(String shortCode, String appName,
	                                                 SMSAppModuleEventsSubscription subscriptionEventsServer,
	                                                 SMSAppModuleDALFactory         baseModuleDAL,
	          	                                     SMSAppModuleDALFactoryProfile  profileModuleDAL,
	        	                                     SMSAppModuleDALFactoryHangman  hangmanModuleDAL,
	        	                                     String defaultNicknamePrefix) {
		SMSAppModulePhrasingsHangman        phrasings        = new SMSAppModulePhrasingsHangman(shortCode, appName);
		SMSAppModuleCommandsHangman         commands         = new SMSAppModuleCommandsHangman(phrasings, subscriptionEventsServer, baseModuleDAL, profileModuleDAL, hangmanModuleDAL, defaultNicknamePrefix);
		SMSAppModuleNavigationStatesHangman navigationStates = new SMSAppModuleNavigationStatesHangman(commands);
		
		System.err.println(SMSAppModuleConfigurationHangman.class.getCanonicalName() + ": test configuration loaded.");
		
		return new Object[] {navigationStates, commands, phrasings};
	}

	/** Constructs the simple version of this SMS Module, with all options set programmatically.<pre>
	 *  @param shortCode                                                    &
	 *  @param appName                                                      &
	 *  @param winningArt                                                   &
	 *  @param losingArt                                                    &
	 *  @param headCharacter                                                &
	 *  @param leftArmCharacter                                             &
	 *  @param chestCharacter                                               &
	 *  @param rightArmCharacter                                            &
	 *  @param leftLegCharacter                                             &
	 *  @param rightLegCharacter                                            &
	 *  @param phr_gallowsArt                                               &
	 *  @param phrAskOpponentNicknameOrPhone                                &
	 *  @param phrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation &
	 *  @param phrAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation    &
	 *  @param phrInvitationResponseForInvitingPlayer                       &
	 *  @param phrInvitationNotificationForInvitedPlayer                    &
	 *  @param phrTimeoutNotificationForInvitingPlayer                      &
	 *  @param phrInvitationRefusalResponseForInvitedPlayer                 &
	 *  @param phrInvitationRefusalNotificationForInvitingPlayer            &
	 *  @param phrNotAGoodWord                                              &
	 *  @param phrWordProvidingPlayerMatchStart                             &
	 *  @param phrWordGuessingPlayerMatchStart                              &
	 *  @param phrWordProvidingPlayerStatus                                 &
	 *  @param phrWordGuessingPlayerStatus                                  &
	 *  @param phrWinningMessageForWordGuessingPlayer                       &
	 *  @param phrWinningMessageForWordProvidingPlayer                      &
	 *  @param phrLosingMessageForWordGuessingPlayer                        &
	 *  @param phrLosingMessageForWordProvidingPlayer                       &
	 *  @param phrMatchGiveupNotificationForWordGuessingPlayer              &
	 *  @param phrMatchGiveupNotificationForWordProvidingPlayer             &
	 *  @param phrGuessingWordHelp                                          see {@link SMSAppModulePhrasingsHangman#SMSAppModulePhrasingsHangman(String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String)}
	 *  @param subscriptionEventsServer one of the values returned by {@link SMSAppModuleConfigurationSubscription#getSubscriptionModuleInstances}
	 *  @param baseModuleDAL            &
	 *  @param profileModuleDAL         &
	 *  @param hangmanModuleDAL         &
	 *  @param defaultNicknamePrefix    see {@link SMSAppModuleCommandsHangman#SMSAppModuleCommandsHangman(SMSAppModulePhrasingsHangman, SMSAppModuleDALFactory, SMSAppModuleDALFactoryProfile, SMSAppModuleDALFactoryHangman, String)}
	 *  @param nstEnteringMatchWordTriggers                    &
	 *  @param nstAnsweringToHangmanMatchInvitationTriggers    &
	 *  @param nstGuessingWordFromHangmanHumanOpponentTriggers &
	 *  @param nstGuessingWordFromHangmanBotOpponentTriggers   see {@link SMSAppModuleNavigationStatesHangman#SMSAppModuleNavigationStatesHangman(SMSAppModuleCommandsHangman, Object[][], Object[][], Object[][], Object[][])}
	 *  @returns {(SMSAppModuleNavigationStatesHangman)navigationStates, (SMSAppModuleCommandsHangman)commands, (SMSAppModulePhrasingsHangman)phrasings} */
	public static Object[] getHangmanModuleInstances(Instrumentation<?, ?> log, String shortCode, String appName,
	                                                 String winningArt,
	                                                 String losingArt,
	                                                 String headCharacter,
	                                                 String leftArmCharacter,
	                                                 String chestCharacter,
	                                                 String rightArmCharacter,
	                                                 String leftLegCharacter,
	                                                 String rightLegCharacter,
	                                                 String phr_gallowsArt,
	                                                 String phrAskOpponentNicknameOrPhone,
	                                                 String phrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation,
	                                                 String phrAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation,
	                                                 String phrInvitationResponseForInvitingPlayer,
	                                                 String phrInvitationNotificationForInvitedPlayer,
	                                                 String phrTimeoutNotificationForInvitingPlayer,
	                                                 String phrInvitationRefusalResponseForInvitedPlayer,
	                                                 String phrInvitationRefusalNotificationForInvitingPlayer,
	                                                 String phrNotAGoodWord,
	                                                 String phrWordProvidingPlayerMatchStart,
	                                                 String phrWordGuessingPlayerMatchStart,
	                                                 String phrWordProvidingPlayerStatus,
	                                                 String phrWordGuessingPlayerStatus,
	                                                 String phrWinningMessageForWordGuessingPlayer,
	                                                 String phrWinningMessageForWordProvidingPlayer,
	                                                 String phrLosingMessageForWordGuessingPlayer,
	                                                 String phrLosingMessageForWordProvidingPlayer,
	                                                 String phrMatchGiveupNotificationForWordGuessingPlayer,
	                                                 String phrMatchGiveupNotificationForWordProvidingPlayer,
	                                                 String phrGuessingWordHelp,
	                                                 SMSAppModuleEventsSubscription subscriptionEventsServer,
	                                                 SMSAppModuleDALFactory         baseModuleDAL,
	          	                                     SMSAppModuleDALFactoryProfile  profileModuleDAL,
	        	                                     SMSAppModuleDALFactoryHangman  hangmanModuleDAL,
	        	                                     String defaultNicknamePrefix,
	        	                                     Object[][] nstEnteringMatchWordTriggers,
	        	                                     Object[][] nstAnsweringToHangmanMatchInvitationTriggers,
	        	                                     Object[][] nstGuessingWordFromHangmanHumanOpponentTriggers,
	        	                                     Object[][] nstGuessingWordFromHangmanBotOpponentTriggers) {
		SMSAppModulePhrasingsHangman        phrasings        = new SMSAppModulePhrasingsHangman(shortCode, appName,
			winningArt, losingArt, headCharacter, leftArmCharacter, chestCharacter, rightArmCharacter, leftLegCharacter, rightLegCharacter, 
			phr_gallowsArt, phrAskOpponentNicknameOrPhone, phrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation,
			phrAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation, phrInvitationResponseForInvitingPlayer,                      
			phrInvitationNotificationForInvitedPlayer, phrTimeoutNotificationForInvitingPlayer,                     
			phrInvitationRefusalResponseForInvitedPlayer, phrInvitationRefusalNotificationForInvitingPlayer,           
			phrNotAGoodWord, phrWordProvidingPlayerMatchStart, phrWordGuessingPlayerMatchStart, phrWordProvidingPlayerStatus,                                
			phrWordGuessingPlayerStatus, phrWinningMessageForWordGuessingPlayer, phrWinningMessageForWordProvidingPlayer,                     
			phrLosingMessageForWordGuessingPlayer, phrLosingMessageForWordProvidingPlayer, phrMatchGiveupNotificationForWordGuessingPlayer,             
			phrMatchGiveupNotificationForWordProvidingPlayer, phrGuessingWordHelp);
		SMSAppModuleCommandsHangman         commands         = new SMSAppModuleCommandsHangman(phrasings, subscriptionEventsServer, baseModuleDAL, profileModuleDAL, hangmanModuleDAL, defaultNicknamePrefix);
		SMSAppModuleNavigationStatesHangman navigationStates = new SMSAppModuleNavigationStatesHangman(commands,
			nstEnteringMatchWordTriggers,nstAnsweringToHangmanMatchInvitationTriggers,
			nstGuessingWordFromHangmanHumanOpponentTriggers, nstGuessingWordFromHangmanBotOpponentTriggers);
		
		// log
		String logPrefix = "Chat Module";
		log.reportDebug(logPrefix + ": new instances:");
		Object[][] logPhrasings = {
			{"winningArt",                                                   winningArt},
			{"losingArt",                                                    losingArt},
			{"headCharacter",                                                headCharacter},
			{"leftArmCharacter",                                             leftArmCharacter},
			{"chestCharacter",                                               chestCharacter},
			{"rightArmCharacter",                                            rightArmCharacter},
			{"leftLegCharacter",                                             leftLegCharacter},
			{"rightLegCharacter",                                            rightLegCharacter},
			{"phr_gallowsArt",                                               phr_gallowsArt},
			{"phrAskOpponentNicknameOrPhone",                                phrAskOpponentNicknameOrPhone},
			{"phrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation", phrAskForAWordToStartAMatchBasedOnOpponentNicknameInvitation},
			{"phrAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation",    phrAskForAWordToStartAMatchBasedOnOpponentPhoneInvitation},
			{"phrInvitationResponseForInvitingPlayer",                       phrInvitationResponseForInvitingPlayer},
			{"phrInvitationNotificationForInvitedPlayer",                    phrInvitationNotificationForInvitedPlayer},
			{"phrTimeoutNotificationForInvitingPlayer",                      phrTimeoutNotificationForInvitingPlayer},
			{"phrInvitationRefusalResponseForInvitedPlayer",                 phrInvitationRefusalResponseForInvitedPlayer},
			{"phrInvitationRefusalNotificationForInvitingPlayer",            phrInvitationRefusalNotificationForInvitingPlayer},
			{"phrNotAGoodWord",                                              phrNotAGoodWord},
			{"phrWordProvidingPlayerMatchStart",                             phrWordProvidingPlayerMatchStart},
			{"phrWordGuessingPlayerMatchStart",                              phrWordGuessingPlayerMatchStart},
			{"phrWordProvidingPlayerStatus",                                 phrWordProvidingPlayerStatus},
			{"phrWordGuessingPlayerStatus",                                  phrWordGuessingPlayerStatus},
			{"phrWinningMessageForWordGuessingPlayer",                       phrWinningMessageForWordGuessingPlayer},
			{"phrWinningMessageForWordProvidingPlayer",                      phrWinningMessageForWordProvidingPlayer},
			{"phrLosingMessageForWordGuessingPlayer",                        phrLosingMessageForWordGuessingPlayer},
			{"phrLosingMessageForWordProvidingPlayer",                       phrLosingMessageForWordProvidingPlayer},
			{"phrMatchGiveupNotificationForWordGuessingPlayer",              phrMatchGiveupNotificationForWordGuessingPlayer},
			{"phrMatchGiveupNotificationForWordProvidingPlayer",             phrMatchGiveupNotificationForWordProvidingPlayer},
			{"phrGuessingWordHelp",                                          phrGuessingWordHelp},
		};
		log.reportDebug(logPrefix + ": Phrasings        : " + Arrays.deepToString(logPhrasings));
		Object[][] logCommands = {
			{"subscriptionEventsServer", subscriptionEventsServer},
			{"baseModuleDAL",            baseModuleDAL},
			{"profileModuleDAL",         profileModuleDAL},
			{"hangmanModuleDAL",         hangmanModuleDAL},
			{"defaultNicknamePrefix",    defaultNicknamePrefix},
		};
		log.reportDebug(logPrefix + ": Commands         : " + Arrays.deepToString(logCommands));
		Object[][] logCommandTriggers = {
			{"nstEnteringMatchWordTriggers",                    nstEnteringMatchWordTriggers},
			{"nstAnsweringToHangmanMatchInvitationTriggers",    nstAnsweringToHangmanMatchInvitationTriggers},
			{"nstGuessingWordFromHangmanHumanOpponentTriggers", nstGuessingWordFromHangmanHumanOpponentTriggers},
			{"nstGuessingWordFromHangmanBotOpponentTriggers",	nstGuessingWordFromHangmanBotOpponentTriggers},
		};
		log.reportDebug(logPrefix + ": Navigation States: " + Arrays.deepToString(logCommandTriggers));
		
		
		return new Object[] {navigationStates, commands, phrasings};
	}
}