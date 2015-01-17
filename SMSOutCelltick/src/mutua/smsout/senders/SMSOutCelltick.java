package mutua.smsout.senders;

import java.io.IOException;

import adapters.HTTPClientAdapter;
import adapters.dto.HTTPRequestDto;
import mutua.smsout.dto.OutgoingSMSDto;

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
	
	public static String MT_URL = "http://localhost:15001/cgi-bin/sendsms";
	public static int    NUMBER_OF_RETRY_ATTEMPTS = 5;
	public static long   DELAY_BETWEEN_ATTEMPTS   = 5000;
	
	public static String ACCOUNT;
	public static String VALIDITY;
	public static String SMSC;

	// TODO fix this fix for the "we don't know the short code problem", removing the public and static and relying solo on the constructor passed info
	public static String shortCode;
	
	public SMSOutCelltick(String smsAppId, String shortCode) {
		super("SMSOutCelltick", smsAppId, NUMBER_OF_RETRY_ATTEMPTS, DELAY_BETWEEN_ATTEMPTS);
		SMSOutCelltick.shortCode = shortCode;
	}

	@Override
	public EOutgoingSMSAcceptionStatus rawSendMessage(OutgoingSMSDto smsOut) throws IOException {
		HTTPRequestDto requestData = new HTTPRequestDto();
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
		System.out.println(HTTPClientAdapter.toString(MT_URL, requestData));
		String response = HTTPClientAdapter.requestGet(MT_URL, requestData, "UTF-8");
		if (response.indexOf("Sent") != -1) {
			return EOutgoingSMSAcceptionStatus.ACCEPTED;
		} else {
			return EOutgoingSMSAcceptionStatus.POSTPONED;
		}
	}

}
