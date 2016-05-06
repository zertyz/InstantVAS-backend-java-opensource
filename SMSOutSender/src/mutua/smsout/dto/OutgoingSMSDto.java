package mutua.smsout.dto;

import mutua.serialization.SerializationRepository.EfficientTextualSerializationMethod;

/** <pre>
 * OutgoingSMSDto.java
 * ===================
 * (created by luiz, Dec 9, 2008)
 *
 * Represents an outgoing SMS from an SMS Application, that is, an MT
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class OutgoingSMSDto {

	public enum EBillingType {
		FREE,			// R$ 0,00
		SMS,			// R$ 0,31+imp
		PREMIUM,		// R$ 4,00+imp
	};

	private final int moId;
	private final String phone;
	private final String text;
	private final EBillingType billingType;

	/** Constructs a representation of an MT (outgoing sms) */
	public OutgoingSMSDto(int moId, String phone, String text, EBillingType billingType) {
		this.moId = moId;
		this.phone = phone;
		this.text = text;
		this.billingType = billingType;
	}
	
	public int getMoId() {
		return moId;
	}

	public String getPhone() {
		return phone;
	}

	public String getText() {
		return text;
	}

	public EBillingType getBillingType() {
		return billingType;
	}
	
	@EfficientTextualSerializationMethod
	public void toString(StringBuffer buffer) {
		buffer.append("phone='").append(phone).append("', text='").
		       append(text).append("', billingType=").append(billingType.name());
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		toString(buffer);
		return buffer.toString();
	}

}