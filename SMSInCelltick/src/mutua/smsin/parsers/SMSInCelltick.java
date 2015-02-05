package mutua.smsin.parsers;

import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsin.dto.IncomingSMSDto.ESMSInParserCarrier;
import mutua.smsout.senders.SMSOutCelltick;

/** <pre>
 * SMSInCelltick.java
 * ==================
 * (created by luiz, Jan 8, 2015)
 *
 * This class is responsible for implementing the passive MO notification api for Celltick
 * gateways
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class SMSInCelltick extends SMSInParser<HttpServletRequest, HttpServletResponse> {

	public SMSInCelltick(String smsAppId) {
		super("SMSInCelltick", smsAppId);
	}

	@Override
	public IncomingSMSDto parseIncomingSMS(HttpServletRequest request) {
		// http://localhost:8080/HangmanSMSGameServices/AddToMOQueue?AUTHENTICATION_TOKEN=...&MSISDN=(+55)?DDN?NNNNNNNN&CARRIER_NAME=...&LA=...&MO_ID=...&TEXT=...
		String msisdn       = request.getParameter("MSISDN");
		String carrierName  = request.getParameter("CARRIER_NAME");
		String largeAccount = request.getParameter("LA");
		String originalMoId = request.getParameter("MO_ID");
		String text         = request.getParameter("TEXT");
		
		if ((msisdn == null) || (carrierName == null) || (largeAccount == null) || (originalMoId == null) || (text == null)) {
			return null;
		}
		
		// extra parameters
		String account  = request.getParameter("account");
		String validity = request.getParameter("validity");
		String smsc     = request.getParameter("smsc");
		
		// TODO fix this workarround (if it is really necessary and if not, remove the dependency from SMSOutCelltick project)
		SMSOutCelltick.ACCOUNT  = account;
		SMSOutCelltick.VALIDITY = validity;
		SMSOutCelltick.SMSC     = smsc;
		
		
		ESMSInParserCarrier carrier = ESMSInParserCarrier.valueOf(carrierName.toUpperCase());
		if (carrier == null) {
			carrier = ESMSInParserCarrier.UNKNOWN;	// probably will make 'sendReply' return 'REJECTED'
		}
		
		return new IncomingSMSDto(originalMoId, msisdn, text, carrier, largeAccount);
	}

	@Override
	public void sendReply(ESMSInParserSMSAcceptionStatus status, HttpServletResponse response) {
		try {
			response.setContentType("text/plain");
			PrintStream out = new PrintStream(response.getOutputStream());
			switch (status) {
				case ACCEPTED:
					out.print("ACCEPTED");
					break;
				case POSTPONED:
					out.print("POSTPONED");
					break;
				default:
					//Instrumentation.reportLocalopInconsistency("Unimplemented switch case statement '"+status.toString()+"'");
			        // now go on performing as rejected
				case REJECTED:
					out.print("REJECTED");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("IOException while acknowledging to an MO", e);
		}
	}

}
