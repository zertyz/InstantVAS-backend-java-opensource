package mutua.smsout.dto;

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

	private final String phone;
	private final String text;
	private final EBillingType billingType;

	/**
	 * Constructs a representation of an MT (outgoing sms)
	 */
	public OutgoingSMSDto(String phone, String text, EBillingType billingType) {
		this.phone = phone;
		this.text = text;
		this.billingType = billingType;
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

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof OutgoingSMSDto) {
			OutgoingSMSDto other = (OutgoingSMSDto) obj;
			return this.phone.equals(other.phone) &&
			       this.text.equals(other.text) &&
			       (this.billingType == other.billingType);
		} else {
			return false;
		}
	}

	// /*************************************
	// ** ILoggableResponse IMPLEMENTATION **
	// *************************************/
	//
	// public void buildResponseDetails(StringBuffer request_details) {
	// String[][] extra_parameters = getExtraParameters();
	// request_details.append("phone='");
	// request_details.append(getPhone());
	// request_details.append("', text='");
	// request_details.append(getText());
	// request_details.append("', carrier='");
	// request_details.append(getCarrierId());
	// request_details.append("', la='");
	// request_details.append(getLargeAccount());
	// if (extra_parameters != null) {
	// for (int i=0; i<extra_parameters.length; i++) {
	// String parameter_name = extra_parameters[i][0];
	// String parameter_value = extra_parameters[i][1];
	// request_details.append("', ");
	// request_details.append(parameter_name);
	// request_details.append("='");
	// request_details.append(parameter_value);
	// request_details.append("'");
	// }
	// }
	// request_details.append(", id='");
	// request_details.append(getMessageId());
	// request_details.append("'");
	// }

}
