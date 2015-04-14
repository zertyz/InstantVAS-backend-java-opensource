package mutua.smsout.senders;

import static mutua.smsout.senders.ESMSOutSenderInstrumentationEvents.SMSOUT_ACCEPTED;
import static mutua.smsout.senders.ESMSOutSenderInstrumentationEvents.SMSOUT_POSTPONED;
import static mutua.smsout.senders.ESMSOutSenderInstrumentationProperties.REQUEST;
import static mutua.smsout.senders.ESMSOutSenderInstrumentationProperties.RESPONSE;

import java.io.IOException;
import java.util.ArrayList;

import mutua.icc.instrumentation.Instrumentation;
import mutua.smsout.dto.OutgoingSMSDto;
import adapters.HTTPClientAdapter;
import adapters.dto.HTTPRequestDto;

/** <pre>
 * SMSOutCelltick.java
 * ===================
 * (created by luiz, Jan 8, 2015)
 *
 * Send MT SMSes through Celltick gateways
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class SMSOutCelltick extends SMSOutSender {

	
	// CONFIGURATION
	////////////////
	
	public static int    CELLTICK_MAX_MT_CHARS = 134;
	public static String ACCOUNT;
	public static String VALIDITY;
	public static String SMSC;
	
	private final String mtServiceUrl /*= "http://localhost:15001/cgi-bin/sendsms"*/;
	private final String shortCode;

	public SMSOutCelltick(Instrumentation<?, ?> log, String smsAppId, String shortCode, String mtServiceUrl, int numberOfRetryAttempts, long delayBetweenAttempts) {
		super(log, "SMSOutCelltick", smsAppId, numberOfRetryAttempts, delayBetweenAttempts);
		this.shortCode = shortCode;
		this.mtServiceUrl = mtServiceUrl;
	}
	
	private static int smsCount = 0;
	private synchronized int getNextSMSCount() {
		return smsCount++;
	}
	
	private String[] getConcatenatedSMSes(String text) {
		if (text.length() > CELLTICK_MAX_MT_CHARS) {
			ArrayList<String> SMSes = new ArrayList<String>();
			String remainingText = text;
			while (remainingText.length() > 0) {
				int partLength = Math.min(remainingText.length(), CELLTICK_MAX_MT_CHARS);
				SMSes.add(remainingText.substring(0, partLength));
				remainingText = remainingText.substring(partLength);
			}
			return SMSes.toArray(new String[SMSes.size()]);
		} else {
			return new String[] {text};
		}
	}

	@Override
	public EOutgoingSMSAcceptionStatus rawSendMessage(OutgoingSMSDto smsOut) throws IOException {
		
		String[] SMSes = getConcatenatedSMSes(smsOut.getText());
		
		// send normal SMS
		if (SMSes.length == 1) {
			HTTPRequestDto requestData = new HTTPRequestDto();
			requestData.addParameter("coding",    "1");
			requestData.addParameter("to",        smsOut.getPhone());
			requestData.addParameter("total",     "1");
			requestData.addParameter("text",      smsOut.getText());
			requestData.addParameter("subAct",    "1");
			requestData.addParameter("from",      shortCode);
			requestData.addParameter("smsc",      SMSC);
			requestData.addParameter("password",  "celltick");
			requestData.addParameter("username",  "celltick");
			requestData.addParameter("validity",  VALIDITY);
			requestData.addParameter("bundle",    "R1A");
			requestData.addParameter("account",   ACCOUNT);
			requestData.addParameter("dlrmask",   "2");
			requestData.addParameter("reportDLR", "0");
			String response = HTTPClientAdapter.requestGet(mtServiceUrl, requestData, "UTF-8");
			if (response.indexOf("Sent") != -1) {
				log.reportEvent(SMSOUT_ACCEPTED, REQUEST, HTTPClientAdapter.toString(mtServiceUrl, requestData), RESPONSE, response);
				return EOutgoingSMSAcceptionStatus.ACCEPTED;
			} else {
				log.reportEvent(SMSOUT_POSTPONED, REQUEST, HTTPClientAdapter.toString(mtServiceUrl, requestData), RESPONSE, response);
				return EOutgoingSMSAcceptionStatus.POSTPONED;
			}
		}
		
		String CSMSHexReferenceNumber = toHex(getNextSMSCount() % 256);
		String hexTotal               = toHex(SMSes.length);
		String total = Integer.toString(SMSes.length);
		// send concatenated SMS
		for (int i=0; i<SMSes.length; i++) {
			String hexPartNumber = toHex(i+1);
			String udh           = "%05%00%03%"+CSMSHexReferenceNumber+"%"+hexTotal+"%"+hexPartNumber;
			HTTPRequestDto requestData = new HTTPRequestDto();
			requestData.addEncodedParameter("udh", udh);
			requestData.addParameter("coding",    "1");
			requestData.addParameter("subAct",    Integer.toString(i+1));
			requestData.addParameter("total",     total);
			requestData.addParameter("to",        smsOut.getPhone());
			requestData.addParameter("text",      SMSes[i]);
			requestData.addParameter("from",      shortCode);
			requestData.addParameter("smsc",      SMSC);
			requestData.addParameter("password",  "celltick");
			requestData.addParameter("username",  "celltick");
			requestData.addParameter("validity",  VALIDITY);
			requestData.addParameter("bundle",    "R1A");
			requestData.addParameter("account",   ACCOUNT);
			requestData.addParameter("dlrmask",   "2");
			requestData.addParameter("reportDLR", "0");
			String response = HTTPClientAdapter.requestGet(mtServiceUrl, requestData, "UTF-8");
			if (response.indexOf("Sent") != -1) {
				log.reportEvent(SMSOUT_ACCEPTED, REQUEST, HTTPClientAdapter.toString(mtServiceUrl, requestData), RESPONSE, response);
			} else {
				log.reportEvent(SMSOUT_POSTPONED, REQUEST, HTTPClientAdapter.toString(mtServiceUrl, requestData), RESPONSE, response);
				return EOutgoingSMSAcceptionStatus.POSTPONED;
			}
		}
		return EOutgoingSMSAcceptionStatus.ACCEPTED;
		
	}
	
	private static String toHex(int n) {
		String hex = Integer.toHexString(n).toUpperCase();
		if ((hex.length() % 2) == 1) {
			return "0" + hex;
		} else {
			return hex;
		}
	}
}
