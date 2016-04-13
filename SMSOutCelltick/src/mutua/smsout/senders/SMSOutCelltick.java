package mutua.smsout.senders;

import static mutua.smsout.senders.ESMSOutSenderInstrumentationEvents.*;
import static mutua.smsout.senders.ESMSOutSenderInstrumentationProperties.*;

import java.io.IOException;

import mutua.icc.instrumentation.Instrumentation;
import mutua.smsout.dto.OutgoingSMSDto;
import adapters.HTTPClientAdapter;

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
	
	private final String            mtServiceBaseUrl /*= "http://localhost:15001/cgi-bin/sendsms"*/;
	private final HTTPClientAdapter mtClient;
	private final String            shortCode;
	private final String            smsc;

	public SMSOutCelltick(Instrumentation<?, ?> log, String smsAppId, String shortCode, String smsc, String mtServiceBaseUrl, int numberOfRetryAttempts, long delayBetweenAttempts) {
		super(log, "SMSOutCelltick", smsAppId, numberOfRetryAttempts, delayBetweenAttempts);
		this.shortCode        = shortCode;
		this.smsc             = smsc;
		this.mtServiceBaseUrl = mtServiceBaseUrl;
		HTTPClientAdapter.configureDefaultValuesForNewInstances(-1, -1, false, "User-Agent", "InstantVAS.com MT client");
		mtClient = new HTTPClientAdapter(mtServiceBaseUrl);

	}
	
	private int smsCount = 0;
	private synchronized int getNextSMSCount() {
		return smsCount++;
	}
	
	private String[] getConcatenatedSMSes(String text) {
		if (text.length() > CELLTICK_MAX_MT_CHARS) {
			String[] SMSes = new String[(int)Math.ceil((double)text.length() / (double)CELLTICK_MAX_MT_CHARS)];
			int i = 0;
			String remainingText = text;
			while (remainingText.length() > 0) {
				int partLength = Math.min(remainingText.length(), CELLTICK_MAX_MT_CHARS);
				SMSes[i++] = remainingText.substring(0, partLength);
				remainingText = remainingText.substring(partLength);
			}
			return SMSes;
		} else {
			return new String[] {text};
		}
	}

	@Override
	public EOutgoingSMSAcceptionStatus rawSendMessage(OutgoingSMSDto smsOut) throws IOException {
		
		String[] SMSes = getConcatenatedSMSes(smsOut.getText());
		
		// send normal SMS
		if (SMSes.length == 1) {
			String[] request = {
				"coding",    "1",
				"to",        smsOut.getPhone(),
				"total",     "1",
				"text",      smsOut.getText(),
				"subAct",    "1",
				"from",      shortCode,
				"smsc",      smsc,
				"password",  "celltick",
				"username",  "celltick",
				"bundle",    "R1A",
				"dlrmask",   "2",
				"reportDLR", "0",
			};
			String response = mtClient.requestGet(request);
			if (response.indexOf("Sent") != -1) {
				log.reportEvent(SMSOUT_ACCEPTED, BASE_URL, mtServiceBaseUrl, REQUEST, request, RESPONSE, response);
				return EOutgoingSMSAcceptionStatus.ACCEPTED;
			} else {
				log.reportEvent(SMSOUT_POSTPONED, BASE_URL, mtServiceBaseUrl, REQUEST, request, RESPONSE, response);
				return EOutgoingSMSAcceptionStatus.POSTPONED;
			}
		}
		
		// send concatenated SMSes
		String CSMSHexReferenceNumber = toHex(getNextSMSCount() % 256);
		String hexTotal               = toHex(SMSes.length);
		String total = Integer.toString(SMSes.length);
		for (int i=0; i<SMSes.length; i++) {
			String hexPartNumber = toHex(i+1);
			String preEncodedUdh = "%05%00%03%"+CSMSHexReferenceNumber+"%"+hexTotal+"%"+hexPartNumber;	// this value should not be URLEncoded
			
			String[] request = {
				"udh",       "",	// param udh at #0 will be set to 'preEncodedUdh' after URLEncoding all other values
				"coding",    "1",
				"subAct",    Integer.toString(i+1),
				"total",     total,
				"to",        smsOut.getPhone(),
				"text",      SMSes[i],
				"from",      shortCode,
				"smsc",      smsc,
				"password",  "celltick",
				"username",  "celltick",
				"bundle",    "R1A",
				"dlrmask",   "2",
				"reportDLR", "0",
			};
			mtClient.encodeParameterValues(request);
			request[0] = preEncodedUdh;		// set the value that will not be URLEncoded
			
			String response = mtClient.requestGetWithAlreadyEncodedValues(request);
			
			if (response.indexOf("Sent") != -1) {
				log.reportEvent(SMSOUT_ACCEPTED, BASE_URL, mtServiceBaseUrl, REQUEST, request, RESPONSE, response);
			} else {
				log.reportEvent(SMSOUT_POSTPONED, BASE_URL, mtServiceBaseUrl, REQUEST, request, RESPONSE, response);
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
