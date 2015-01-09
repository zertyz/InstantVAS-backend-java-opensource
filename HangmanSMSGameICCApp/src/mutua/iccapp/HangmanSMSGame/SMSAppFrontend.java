package mutua.iccapp.HangmanSMSGame;

import java.util.logging.Level;
import java.util.logging.Logger;

import mutua.hangmansmsgame.celltick.CelltickLiveScreenAPI;
import mutua.hangmansmsgame.smslogic.HangmanSMSGameProcessor;
import mutua.hangmansmsgame.smslogic.SMSProcessorException;
import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;
import mutua.smsout.dto.OutgoingSMSDto;

/* SMSAppFrontend.java  --  $Id$
 * ===================
 * (created by luiz, Feb 4, 2011)
 *
 * Do the interfacing between the graphical application and the SMS service engine
 * 
 */

public class SMSAppFrontend {
	
    private static SimulationMessageReceiver simulationMessageReceiver = new SimulationMessageReceiver();
    private static HangmanSMSGameProcessor processor = new HangmanSMSGameProcessor(simulationMessageReceiver);
    
    
    static {
    	CelltickLiveScreenAPI.REGISTER_SUBSCRIBER_URL = null;
    	CelltickLiveScreenAPI.REGISTER_SUBSCRIBER_URL = null;
    }

    
    public static String[][] process(String phone, String inputText, String carrier) {
        IncomingSMSDto mo = new IncomingSMSDto(phone, inputText, ESMSInParserCarrier.valueOf(carrier), "1234", "null");
        try {
			processor.process(mo);
		} catch (SMSProcessorException e) {
			Logger.getLogger(HangmanSMSGameICCAppView.class.getName()).log(Level.SEVERE, null, e);
			e.printStackTrace();
			System.exit(1);
		}
		OutgoingSMSDto[] observedResponses = simulationMessageReceiver.getLastOutgoingSMSes();
		String[][] response = new String[observedResponses.length][3];
		for (int i=0; i<response.length; i++) {
			response[i][0] = observedResponses[i].getBillingType().toString();
			response[i][1] = observedResponses[i].getPhone();
			response[i][2] = observedResponses[i].getText();
		}
		return response;
	}

    public static int getAppVersionsLength() {
//    	return AppVersionInfo.APP_VERSIONS.length;
    	return -1;
    }
    
    public static String getAppVersionModuleName(int id) {
//    	return AppVersionInfo.APP_VERSIONS[id][0];
    	return "---";
    }
    
    private static String[][] cycleNameMapping = {
    	{"DEV",     "Desenvolvimento"},	
    	{"HOMOLOG", "Homologação"},	
    	{"PROD",    "Produção"},
    	{"NCMT",    "Não Comitada"},
    };
    public static String getAppVersionCycle(int id) {
//    	String cycle = AppVersionInfo.APP_VERSIONS[id][1].replaceAll("\\-.*", "");
//    	for (int i=0; i<cycleNameMapping.length; i++) {
//    		cycle = cycle.replace(cycleNameMapping[i][0], cycleNameMapping[i][1]);
//    	}
//    	return cycle;
    	return "---";
    }
    
    public static String getAppVersionTag(int id) {
//    	return AppVersionInfo.APP_VERSIONS[id][1];
    	return "---";
    }

}
