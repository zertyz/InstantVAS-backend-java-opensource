package mutua.smsappmodule.smslogic.commands;

import mutua.serialization.SerializationRepository;
import mutua.serialization.SerializationRepository.EfficientTextualSerializationMethod;

/** <pre>
 * CommandMessageDto.java
 * ======================
 * (created by luiz, Jan 19, 2011)
 *
 * Represent a message that must be sent to an user in consequence of
 * a command processing.
 * 
 * This object will be translated (after applying the 'BillingRules') to a
 * 'OutgoingSMSDto' so to better represent outgoing SMSes.
 *
 * As a convention, if this object has a null 'phone', the message represented
 * by this object should routed to the originating phone number.
 */

public class CommandMessageDto {

	// classes of messages produced by the system
	public enum EResponseMessageType {
		MAYBE_THIS_SHOULD_BE_REFACTORED_AS_AN_INTERFACE_IF_IT_REALLY_MAKES_SENSE_TO_STILL_HAVE_IT,
		HELP, ACQUIRE_MATCH_INFORMATION, MATCH,
		PLAYING, CHAT, PROFILE,
		ERROR, INCENTIVE, INVITATION_MESSAGE,
	};
	
	private String phone;
	private String text;
	private EResponseMessageType type;
	
	public CommandMessageDto(String phone, String text, EResponseMessageType type) {
		this.phone = phone;
		this.text = text;
		this.type = type;
	}
		
	public CommandMessageDto(String text, EResponseMessageType type) {
		this(null, text, type);
	}

	public String getPhone() {
		return phone;
	}
	
	public String getText() {
		return text;
	}
	
	public EResponseMessageType getType() {
		return type;
	}
	
	@EfficientTextualSerializationMethod
	public void toString(StringBuffer buffer) {
		buffer.append("phone='").append(phone).append("', text='");
		SerializationRepository.serialize(buffer, text);
		buffer.append("', type='").append((type != null) ? type.name() : "NULL").append('\'');
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		toString(buffer);
		return buffer.toString();
	}
}
