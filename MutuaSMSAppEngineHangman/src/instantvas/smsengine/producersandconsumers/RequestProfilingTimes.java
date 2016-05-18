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
	public long   moReceivedMillis;				// time of the MO reception
	public long   moQueueAdditionMillis;		// time after adding the MO to the queue
	public long   moProcessingStart;			// time of the MO dequeuing
	public long   moProcessingCompleteMillis;	// time of the first generated MT  (in case it is a multi-mt message)
	public long   mtQueueAdditionMillis;		// time after the last MT addition to the queue (in case it is a multi-mt message)
	public long   mtDeliveryStartMillis;		// time of the first MT delivery attempt (in case it is a multi-mt message)
	public long   mtDeliveredMillis;			// time after the last MT delivery is complete (in case it is a multi-mt message)
	
	public long moEnqueuingTime  = -1;
	public long moDequeuingTime  = -1;
	public long moProcessingTime = -1;
	public long mtEnqueuingTime  = -1;
	public long mtDequeuingTime  = -1;
	public long mtDeliveryTime   = -1;
	public long totalDuration    = -1;

	public void computeTimes() {
		if (mtDeliveredMillis != 0) {
			// normal request, which started and finished normally
			totalDuration    = mtDeliveredMillis          - moReceivedMillis;
		} else {
			// a timed out event
			totalDuration    = System.currentTimeMillis() - moReceivedMillis;
		}
		if ((moQueueAdditionMillis != 0) && (moReceivedMillis != 0)) {
			moEnqueuingTime = moQueueAdditionMillis - moReceivedMillis;
			if (moProcessingStart != 0) {
				moDequeuingTime = moProcessingStart - moQueueAdditionMillis;
				if (moProcessingCompleteMillis!= 0) {
					moProcessingTime = moProcessingCompleteMillis - moProcessingStart;
					if (mtQueueAdditionMillis != 0) {
						mtEnqueuingTime = mtQueueAdditionMillis - moProcessingCompleteMillis;
						if (mtDeliveryStartMillis != 0) {
							mtDequeuingTime  = mtDeliveryStartMillis - mtQueueAdditionMillis;
							if (mtDeliveredMillis != 0) {
								mtDeliveryTime = mtDeliveredMillis - mtDeliveryStartMillis;
							}
						}
					}
				}
			}
		}
	}
	
	@EfficientTextualSerializationMethod
	public void toString(StringBuffer buffer) {
		
		computeTimes();
		
		buffer.
			append("{moId=").append(moId).
			append(", phone='").append(phone);
		
		// dump everything
		if (phone == null) {
			buffer.
				append("', moReceivedMillis=").append(moReceivedMillis).
				append("ms, moQueueAdditionMillis=").append(moQueueAdditionMillis).
				append("ms, moProcessingStart=").append(moProcessingStart).
				append("ms, moProcessingCompleteMillis=").append(moProcessingCompleteMillis).
				append("ms, mtQueueAdditionMillis=").append(mtQueueAdditionMillis).
				append("ms, mtDeliveryStartMillis=").append(mtDeliveryStartMillis).
				append("ms, mtDeliveredMillis=").append(mtDeliveredMillis).append("ms, '");
		}
		
		if ((moReceivedMillis != 0) && (mtDeliveredMillis != 0)) {
			// normal request, which started and finished normally
			buffer.	append("', totalDuration=").append(totalDuration);
		} else if (moReceivedMillis != 0) {
			// a timed out event
			buffer.append("', timedOutAfter=").append(totalDuration);
		} else {
			// an event that completed after the timeout
			buffer.append("', zombieRequest (completed after timing out -- please, increase timeout)}");
			return;
		}
		buffer.
			append("ms -- moEnqueuingTime=").append(moEnqueuingTime).
			append("ms, moDequeuingTime=").append(moDequeuingTime).
			append("ms, moProcessingTime=").append(moProcessingTime).
			append("ms, mtEnqueuingTime=").append(mtEnqueuingTime).
			append("ms, mtDequeuingTime=").append(mtDequeuingTime).
			append("ms, mtDeliveryTime=").append(mtDeliveryTime).
			append("ms}");
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer(64);
		toString(buffer);
		return buffer.toString();
	}
	
	
}