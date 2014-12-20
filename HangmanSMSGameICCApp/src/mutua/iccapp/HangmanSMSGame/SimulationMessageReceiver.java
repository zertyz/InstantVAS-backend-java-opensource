package mutua.iccapp.HangmanSMSGame;

import java.util.ArrayList;

import mutua.hangmansmsgame.dispatcher.IResponseReceiver;
import mutua.smsin.dto.IncomingSMSDto;
import mutua.smsout.dto.OutgoingSMSDto;

/* SimulationMessageReceiver.java  --  $Id$
 * ==============================
 * (created by luiz, Feb 4, 2011)
 *
 * Turn the application able to receive simulation messages
 */

public class SimulationMessageReceiver implements IResponseReceiver {

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
