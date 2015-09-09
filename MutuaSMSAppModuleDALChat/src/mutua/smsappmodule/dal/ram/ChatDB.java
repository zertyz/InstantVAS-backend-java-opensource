package mutua.smsappmodule.dal.ram;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.TreeSet;

import mutua.smsappmodule.dal.IChatDB;
import mutua.smsappmodule.dto.PrivateMessageDto;
import mutua.smsappmodule.dto.UserDto;

/** <pre>
 * ChatDB.java
 * ===========
 * (created by luiz, Sep 8, 2015)
 *
 * Implements the RAM version of {@link IChatDB}
 *
 * @see IChatDB
 * @version $Id$
 * @author luiz
 */

public class ChatDB extends IChatDB {

	// data structures
	//////////////////

	private static Hashtable<Integer, String>                                   moTexts        = new Hashtable<Integer, String>();
	private static Hashtable<UserDto, Hashtable<UserDto, ArrayList<Integer[]>>> privateSenders = new Hashtable<UserDto, Hashtable<UserDto, ArrayList<Integer[]>>>();
	
	
	// common methods
	/////////////////
	
	private Hashtable<UserDto, ArrayList<Integer[]>> assureConversations(UserDto sender) {
		Hashtable<UserDto, ArrayList<Integer[]>> conversations = privateSenders.get(sender);
		if (conversations == null) {
			conversations = new Hashtable<UserDto, ArrayList<Integer[]>>();
			privateSenders.put(sender, conversations);
		}
		return conversations;
	}
	
	private ArrayList<Integer[]> assureMessages(Hashtable<UserDto, ArrayList<Integer[]>> conversations, UserDto recipient) {
		ArrayList<Integer[]> messages = conversations.get(recipient);
		if (messages == null) {
			messages = new ArrayList<Integer[]>();
			conversations.put(recipient, messages);
		}
		return messages;
	}

	
	/** special method used to keep track of MOs for the RAM version -- DB versions should use the Queue tables */
	public int addMO(String moText) throws SQLException {
		int moId = moTexts.size();
		moTexts.put(moId, moText);
		return moId;
	}

	// IChatDB implementation
	////////////////////////////

	@Override
	public void reset() throws SQLException {
		moTexts.clear();
		privateSenders.clear();
	}

	@Override
	public void logPrivateMessage(UserDto sender, UserDto recipient, int moId, int moTextStartIndex) throws SQLException {
		Hashtable<UserDto, ArrayList<Integer[]>> conversations = assureConversations(sender);
		ArrayList<Integer[]>                     messages      = assureMessages(conversations, recipient);
		messages.add(new Integer[] {moId, moTextStartIndex});
	}

	@Override
	public UserDto[] getPrivatePeers(UserDto user) throws SQLException {
		// returns all peers that matches 'privateSenders[user][*]' or 'privateSenders[*][user]'
		ArrayList<UserDto> peers = new ArrayList<UserDto>();
		Hashtable<UserDto, ArrayList<Integer[]>> conversations = assureConversations(user);
		for (UserDto peer : conversations.keySet()) {
			peers.add(peer);
		}
		for (UserDto peerCandidate : privateSenders.keySet()) {
			conversations = assureConversations(peerCandidate);
			if (conversations.containsKey(peerCandidate)) {
				peers.add(peerCandidate);
			}
		}
		int peersLength = peers.size();
		if (peersLength == 0) {
			return null;
		} else {
			return peers.toArray(new UserDto[peersLength]);
		}
	}

	@Override
	public PrivateMessageDto[] getPrivateMessages(UserDto user1, UserDto user2) throws SQLException {
		// returns all messages that matches 'privateSenders[user1][user2]' or 'privateSenders[user2][user1]'
		TreeSet<PrivateMessageDto> privateMessages = new TreeSet<PrivateMessageDto>(new Comparator<PrivateMessageDto>() {
			@Override
			public int compare(PrivateMessageDto o1, PrivateMessageDto o2) {
				if (o1.getMoId() < o2.getMoId()) {
					return -1;
				} else if (o1.getMoId() == o2.getMoId()) {
					return 0;
				} else {
					return 1;
				}
			}
		});
		Hashtable<UserDto, ArrayList<Integer[]>> conversations = assureConversations(user1);
		for (UserDto user2Candidate : conversations.keySet()) {
			if (user2Candidate.equals(user2)) {
				ArrayList<Integer[]> messages = assureMessages(conversations, user2Candidate);
				for (Integer[] message : messages) {
					int moId             = message[0];
					int moTextStartIndex = message[1];
					privateMessages.add(new PrivateMessageDto(user1, user2, moId, moTexts.get(moId).substring(moTextStartIndex)));
				}
			}
		}
		conversations = assureConversations(user2);
		for (UserDto user1Candidate : conversations.keySet()) {
			if (user1Candidate.equals(user1)) {
				ArrayList<Integer[]> messages = assureMessages(conversations, user1Candidate);
				for (Integer[] message : messages) {
					int moId             = message[0];
					int moTextStartIndex = message[1];
					privateMessages.add(new PrivateMessageDto(user2, user1, moId, moTexts.get(moId).substring(moTextStartIndex)));
				}
			}
		}
		int privateMessagesLength = privateMessages.size();
		if (privateMessagesLength == 0) {
			return null;
		} else {
			return privateMessages.toArray(new PrivateMessageDto[privateMessagesLength]);
		}
	}

}