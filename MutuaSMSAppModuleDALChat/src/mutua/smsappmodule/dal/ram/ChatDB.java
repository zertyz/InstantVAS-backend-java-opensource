package mutua.smsappmodule.dal.ram;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
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

	private static HashMap<Integer, String>                                 moTexts        = new HashMap<Integer, String>();
	private static HashMap<UserDto, HashMap<UserDto, ArrayList<Integer[]>>> privateSenders = new HashMap<UserDto, HashMap<UserDto, ArrayList<Integer[]>>>();
	
	
	// common methods
	/////////////////
	
	private HashMap<UserDto, ArrayList<Integer[]>> assureConversations(UserDto sender) {
		synchronized (privateSenders) {
			HashMap<UserDto, ArrayList<Integer[]>> conversations = privateSenders.get(sender);
			if (conversations == null) {
				conversations = new HashMap<UserDto, ArrayList<Integer[]>>();
					privateSenders.put(sender, conversations);
				}
			return conversations;
		}
	}
	
	private ArrayList<Integer[]> assureMessages(HashMap<UserDto, ArrayList<Integer[]>> conversations, UserDto recipient) {
		synchronized (conversations) {
			ArrayList<Integer[]> messages = conversations.get(recipient);
			if (messages == null) {
				messages = new ArrayList<Integer[]>();
				conversations.put(recipient, messages);
			}
			return messages;
		}
	}

	
	/** special method used to keep track of MOs for the RAM version -- DB versions should use the Queue tables */
	private static int lastMOId = 1;
	public int addMO(String moText) throws SQLException {
		synchronized (moTexts) {
			int moId = lastMOId++;
			moTexts.put(moId, moText);
			return moId;
		}
	}

	// IChatDB implementation
	////////////////////////////

	@Override
	public void reset() throws SQLException {
		//moTexts.clear();		// if we are to delete this property, we must do it when resetting the queues
		privateSenders.clear();
	}

	@Override
	public void logPrivateMessage(UserDto sender, UserDto recipient, int moId, int moTextStartIndex) throws SQLException {
		HashMap<UserDto, ArrayList<Integer[]>> conversations = assureConversations(sender);
		ArrayList<Integer[]>                   messages      = assureMessages(conversations, recipient);
		synchronized (messages) {
			messages.add(new Integer[] {moId, moTextStartIndex});
		}
	}

	@Override
	public UserDto[] getPrivatePeers(UserDto user) throws SQLException {
		// returns all peers that matches 'privateSenders[user][*]' or 'privateSenders[*][user]'
		HashSet<UserDto> peers = new HashSet<UserDto>();
		HashMap<UserDto, ArrayList<Integer[]>> conversations = assureConversations(user);
		synchronized (conversations) {
			for (UserDto peer : conversations.keySet()) {
				peers.add(peer);
			}
		}
		synchronized (privateSenders) {
			for (UserDto peerCandidate : privateSenders.keySet()) {
				conversations = assureConversations(peerCandidate);
				synchronized (conversations) {
					if (conversations.containsKey(peerCandidate)) {
						peers.add(peerCandidate);
					}
				}
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
					if (o1 == o2) {
						return 0;
					}
					throw new RuntimeException("Two (different) MOs have the same moId, which is not acceptable");
				} else {
					return 1;
				}
			}
		});
		HashMap<UserDto, ArrayList<Integer[]>> conversations = assureConversations(user1);
		synchronized (conversations) {
			for (UserDto user2Candidate : conversations.keySet()) {
				if (user2Candidate.equals(user2)) {
					ArrayList<Integer[]> messages = assureMessages(conversations, user2Candidate);
					synchronized (messages) {
						for (Integer[] message : messages) {
							int moId             = message[0];
							int moTextStartIndex = message[1];
							synchronized (moTexts) {
								privateMessages.add(new PrivateMessageDto(user1, user2, moId, moTexts.get(moId).substring(moTextStartIndex)));
							}
						}
					}
				}
			}
		}
		conversations = assureConversations(user2);
		synchronized (conversations) {
			for (UserDto user1Candidate : conversations.keySet()) {
				if (user1Candidate.equals(user1)) {
					ArrayList<Integer[]> messages = assureMessages(conversations, user1Candidate);
					synchronized (messages) {
						for (Integer[] message : messages) {
							int moId             = message[0];
							int moTextStartIndex = message[1];
							synchronized (moTexts) {
								privateMessages.add(new PrivateMessageDto(user2, user1, moId, moTexts.get(moId).substring(moTextStartIndex)));
							}
						}
					}
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