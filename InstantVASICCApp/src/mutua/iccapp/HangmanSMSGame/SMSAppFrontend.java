package mutua.iccapp.HangmanSMSGame;

import static instantvas.smsengine.SMSAppEngineInstrumentationMethods.reportMOQueueAddition;
import instantvas.nativewebserver.InstantVASConfigurationLoader;
import instantvas.smsengine.producersandconsumers.EInstantVASEvents;
import config.InstantVASInstanceConfiguration;
import mutua.hangmansmsgame.smslogic.SMSProcessor;
import mutua.hangmansmsgame.smslogic.SMSProcessorException;
import mutua.icc.instrumentation.Instrumentation;
import mutua.imi.IndirectMethodInvocationInfo;
import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;
import mutua.smsout.dto.OutgoingSMSDto;

/** <pre>
 * SMSAppFrontend.java  --  $Id$
 * ===================
 * (created by luiz, Feb 4, 2011)
 *
 * Do the interfacing between the graphical application and the SMS service engine
 * 
 */

public class SMSAppFrontend {

	private static InstantVASInstanceConfiguration ivac;
	
	private SMSProcessor                   smsP;
	private SimulationMessageReceiver      responseReceiver;
	private static String                  subscriptionChannel = "InstantVASSimulator";

    static {
		try {
			// code based on NativeHTTPServer#main
			InstantVASConfigurationLoader.applyConfigurationFromLicenseClass();
			ivac = new InstantVASInstanceConfiguration();
		} catch (Throwable t) {
			t.printStackTrace();
		}
    }

    
    public SMSAppFrontend() {
    	responseReceiver = new SimulationMessageReceiver();
    	
    	// code based on 'MOConsumer'
    	smsP = new SMSProcessor(responseReceiver, ivac.modulesNavigationStates, ivac.modulesCommandProcessors);
	}

    public String[][] process(String phone, String inputText, String carrier) throws Throwable {

        IncomingSMSDto mo = new IncomingSMSDto(-1, phone, inputText, ESMSInParserCarrier.valueOf(carrier), "1234");
        
    	int moId = ivac.MOpcLink.reportConsumableEvent(new IndirectMethodInvocationInfo<EInstantVASEvents>(EInstantVASEvents.MO_ARRIVED, mo));
    	reportMOQueueAddition(moId, mo);
    	
    	// only MOs with an moId may be processed (requirement by the chat module)
    	mo = new IncomingSMSDto(moId, phone, inputText, ESMSInParserCarrier.valueOf(carrier), "1234");
    	
    	// process the MO -- code based on 'MOConsumer'
		smsP.process(mo);
        
		OutgoingSMSDto[] observedResponses = responseReceiver.getLastOutgoingSMSes();
		String[][] response = new String[observedResponses.length][3];
		for (int i=0; i<response.length; i++) {
			response[i][0] = observedResponses[i].getBillingType().toString();
			response[i][1] = observedResponses[i].getPhone();
			response[i][2] = observedResponses[i].getText().replaceAll("\n", "<br/>");
			
			OutgoingSMSDto mt = new OutgoingSMSDto(moId, observedResponses[i].getPhone(), observedResponses[i].getText(), null);
			// add to the MT Queue -- code based on 'MTProducer'
			ivac.MTpcLink.reportConsumableEvents(new IndirectMethodInvocationInfo<EInstantVASEvents>(EInstantVASEvents.INTERACTIVE_MT, mt));
			
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