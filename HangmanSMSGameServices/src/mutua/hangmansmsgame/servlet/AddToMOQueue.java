package mutua.hangmansmsgame.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mutua.hangmansmsgame.dispatcher.IResponseReceiver;
import mutua.hangmansmsgame.smslogic.HangmanSMSGameProcessor;
import mutua.hangmansmsgame.smslogic.SMSProcessorException;
import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsin.parsers.SMSInCelltick;
import mutua.smsin.parsers.SMSInParser;
import mutua.smsin.parsers.SMSInParser.ESMSInParserSMSAcceptionStatus;
import mutua.smsout.dto.OutgoingSMSDto;
import mutua.smsout.senders.SMSOutCelltick;
import mutua.smsout.senders.SMSOutSender;



/******************************
** IResponseReceiver CLASSES **
******************************/

class InteractiveReceiver implements IResponseReceiver {

	private static SMSOutSender smsSender = new SMSOutCelltick(AddToMOQueue.HANGMAN_APPID + " interaction", AddToMOQueue.HANGMAN_SHORTCODE);
	
	@Override
	public void onMessage(OutgoingSMSDto outgoingMessage, IncomingSMSDto incomingMessage) {
		System.out.println("Hangman: sending interactive SMS -- " + outgoingMessage);
		smsSender.sendMessage(outgoingMessage);
	}
	
}


/************
** SERVLET **
************/

public class AddToMOQueue extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	
	// CONFIGURATION
	////////////////
	
	public static String HANGMAN_APPID     = "HANGMAN";
	public static String HANGMAN_SHORTCODE = "XXXX";
	
	
	// MO
	/////
	
	private static SMSInParser<HttpServletRequest, HttpServletResponse>  smsParser = new SMSInCelltick(HANGMAN_APPID);
	
	
	// SMS APP
	//////////
	
	private static HangmanSMSGameProcessor processor = new HangmanSMSGameProcessor(new InteractiveReceiver());

	private void process(HttpServletRequest request, HttpServletResponse response) {
		try {
			IncomingSMSDto mo = smsParser.parseIncomingSMS(request);
			if (mo == null) {
				System.out.println("Hangman: received an incorrect MO request -- " + request.getQueryString());
				smsParser.sendReply(ESMSInParserSMSAcceptionStatus.REJECTED, response);
			} else {
				System.out.println("Hangman: received interactive SMS -- " + mo);
				processor.process(mo);
				smsParser.sendReply(ESMSInParserSMSAcceptionStatus.ACCEPTED, response);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

}
