package mutua.hangmansmsgame.smslogic;

import java.util.ArrayList;

import mutua.hangmansmsgame.dispatcher.IResponseReceiver;
import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsout.dto.OutgoingSMSDto;

/** <pre>
 *  TestResponseReceiver.java
 * =========================
 * (created by luiz, Feb 13, 2011)
 *
 * Allows test classes to receive SMSes
 */

public class TestResponseReceiver implements IResponseReceiver {

	private ArrayList<OutgoingSMSDto> outgoingSMSes = new ArrayList<OutgoingSMSDto>();
	private int outgoingSMSesIndex = 0;

	@Override
	public void onMessage(OutgoingSMSDto outgoingMessage, IncomingSMSDto incomingMessage) {
		outgoingSMSes.add(outgoingMessage);
	}
	
	public OutgoingSMSDto[] getLastOutgoingSMSes() {
		OutgoingSMSDto[] lastOutgoingSMSes = new OutgoingSMSDto[outgoingSMSes.size()-outgoingSMSesIndex];
		int i=0;
		while (outgoingSMSesIndex<outgoingSMSes.size()) {
			lastOutgoingSMSes[i] = outgoingSMSes.get(outgoingSMSesIndex);
			outgoingSMSesIndex++;
			i++;
		}
		return lastOutgoingSMSes;
	}
	
}
