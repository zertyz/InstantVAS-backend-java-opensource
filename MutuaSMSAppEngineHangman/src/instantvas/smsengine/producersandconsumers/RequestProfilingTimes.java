package instantvas.smsengine.producersandconsumers;

import mutua.serialization.SerializationRepository.EfficientTextualSerializationMethod;

/** <pre>
 * RequestProfilingTimes.java
 * ==========================
 * (created by luiz, May 12, 2016)
 *
 * Represents profile times associated with MO/MT processing
 *
 * @see MOAndMTProfileInstrumentationHandler
 * @version $Id$
 * @author luiz
*/

public class RequestProfilingTimes {
	public int    moId;
	public String phone;
	public long   moReceivedMillis;			// time of the MO reception
	public long   moQueueAdditionMillis;		// time after adding the MO to the queue
	public long   moProcessingStart;			// time of the MO dequeuing
	public long   moProcessingCompleteMillis;	// time of the first generated MT  (in case it is a multi-mt message)
	public long   mtQueueAdditionMillis;		// time after the last MT addition to the queue (in case it is a multi-mt message)
	public long   mtDeliveryStartMillis;		// time of the first MT delivery attempt (in case it is a multi-mt message)
	public long   mtDeliveredMillis;			// time after the last MT delivery is complete (in case it is a multi-mt message)
	
	@EfficientTextualSerializationMethod
	public void toString(StringBuffer buffer) {
		
		if ((moReceivedMillis != 0) && (mtDeliveredMillis != 0)) {
			// a normal request, which started and finished normally
			long enqueuedMOTime   = moQueueAdditionMillis      - moReceivedMillis;
			long dequeuedMOTime   = moProcessingStart          - moQueueAdditionMillis;
			long moProcessingTime = moProcessingCompleteMillis - moProcessingStart;
			long enqueuedMTTime   = mtQueueAdditionMillis      - moProcessingCompleteMillis;
			long dequeuedMTTime   = mtDeliveryStartMillis      - mtQueueAdditionMillis;
			long sentMTTime       = mtDeliveredMillis          - mtDeliveryStartMillis;
			long totalDuration    = mtDeliveredMillis          - moReceivedMillis;
	
			buffer.
				append("{moId=").append(moId).
				append(", phone='").append(phone).
				append("', totalDuration=").append(totalDuration).
				append("ms -- enqueuedMOTime=").append(enqueuedMOTime).
				append("ms, dequeuedMOTime=").append(dequeuedMOTime).
				append("ms, moProcessingTime=").append(moProcessingTime).
				append("ms, enqueuedMTTime=").append(enqueuedMTTime).
				append("ms, dequeuedMTTime=").append(dequeuedMTTime).
				append("ms, sentMTTime=").append(sentMTTime).
				append("ms}");
		} else if (moReceivedMillis != 0) {
			// a timed out event
			buffer.
				append("{moId=").append(moId).
				append(", phone='").append(phone).
				append("', timedOutAfter=").append(System.currentTimeMillis() - moReceivedMillis).
				append("ms -- ");
			if (moQueueAdditionMillis != 0) {
				buffer.append("enqueuedMOTime=").append(moQueueAdditionMillis - moReceivedMillis).append("ms");
				if (moProcessingStart != 0) {
					buffer.append(", dequeuedMOTime=").append(moProcessingStart - moQueueAdditionMillis).append("ms");
					if (moProcessingCompleteMillis != 0) {
						buffer.append(", moProcessingTime=").append(moProcessingCompleteMillis - moProcessingStart).append("ms");
						if (mtQueueAdditionMillis != 0) {
							buffer.append(", enqueuedMTTime=").append(mtQueueAdditionMillis - moProcessingCompleteMillis).append("ms");
							if (mtDeliveryStartMillis != 0) {
								buffer.append(", dequeuedMTTime=").append(mtDeliveryStartMillis - mtQueueAdditionMillis).append("ms");
							}
						}
					}
				}
			}
			buffer.append('}');
		} else {
			// an event that completed after the timeout
			buffer.
				append("{moId=").append(moId).
				append(", phone='").append(phone).
				append("', zombieRequest (previously timedout but now completed)}");
		}
	}
}