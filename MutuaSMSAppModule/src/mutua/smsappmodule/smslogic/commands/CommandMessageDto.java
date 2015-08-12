package mutua.smsappmodule.smslogic.commands;


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

	@Override
	public String toString() {
		return new StringBuffer().
			append("phone='").append(phone).append("', text='").append(text).
			append("', type='").append((type != null) ? type.name() : "null").append("'").
			toString();
	}
	

}