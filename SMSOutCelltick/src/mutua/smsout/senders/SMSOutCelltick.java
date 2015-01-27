package mutua.smsout.senders;

import java.io.IOException;

import adapters.HTTPClientAdapter;
import adapters.dto.HTTPRequestDto;
import mutua.icc.instrumentation.Instrumentation;
import mutua.smsout.dto.OutgoingSMSDto;

import static mutua.smsout.senders.ESMSOutSenderInstrumentationProperties.*;
import static mutua.smsout.senders.ESMSOutSenderInstrumentationEvents.*;

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
		String response = HTTPClientAdapter.requestGet(mtServiceUrl, requestData, "UTF-8");
		if (response.indexOf("Sent") != -1) {
			log.reportEvent(SMSOUT_ACCEPTED, REQUEST, HTTPClientAdapter.toString(mtServiceUrl, requestData), RESPONSE, response);
			return EOutgoingSMSAcceptionStatus.ACCEPTED;
		} else {
			log.reportEvent(SMSOUT_POSTPONED, REQUEST, HTTPClientAdapter.toString(mtServiceUrl, requestData), RESPONSE, response);
			return EOutgoingSMSAcceptionStatus.POSTPONED;
		}
	}

}
