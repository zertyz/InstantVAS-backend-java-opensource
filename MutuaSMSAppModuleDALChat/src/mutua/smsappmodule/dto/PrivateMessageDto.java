package mutua.smsappmodule.dto;

/** <pre>
 * PrivateMessageDto.java
 * ======================
 * (created by luiz, Sep 8, 2015)
 *
 * Represents a retrieved private message
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class PrivateMessageDto {
	
	
	private UserDto sender;
	private UserDto recipient;
	private int moId;
	private String message;
	
	
	/** Creates a DTO to represent a private message */
	public PrivateMessageDto(UserDto sender, UserDto recipient, int moId, String message) {
		this.sender    = sender;
		this.recipient = recipient;
		this.moId      = moId;
		this.message   = message;
	}
	
	public UserDto getSender() {
		return sender;
	}
	
	public UserDto getRecipient() {
		return recipient;
	}
	
	public int getMoId() {
		return moId;
	}
	
	public String getMessage() {
		return message;
	}
	
	@Override
	public boolean equals(Object obj) {
		PrivateMessageDto other = (PrivateMessageDto)obj;
		return this.sender.equals(other.getSender()) && this.recipient.equals(other.getRecipient()) && this.message.equals(other.message) && (this.moId == other.moId);
	}
	
	@Override
	public String toString() {
		return new StringBuffer().append("{moId=").append(moId).
		       append(", sender={").append(sender.toString()).
		       append("}, recipient={").append(recipient.toString()).
		       append("}, message='").append(message).append("'}").toString();
	}
}
