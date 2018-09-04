package mutua.smsappmodule.dal.mvstore;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;

import adapters.MVStoreAdapter;
import mutua.smsappmodule.dal.IChatDB;
import mutua.smsappmodule.dal.IUserDB;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.dto.PrivateMessageDto;
import mutua.smsappmodule.dto.UserDto;

/** ChatDB.java
 * ============
 * (created by luiz, Sep 1, 2018)
 *
 * Implements the MVStore version of {@link IChatDB}.
 * 
 * @see IChat
 * @author luiz
*/

public class ChatDB extends IChatDB {
	
	// PrivateSenders  := {[phone] = {peer1Phone, peer2Phone, ...}}
	// PrivateMessages := {[senderPhone_recipientPhone] = {{msg1MoId, msg1TextIndex}, {msg2MoId, msg2TextIndex}, ...}}
	private MVMap<String, String[]>    privateSenders;
	private MVMap<String, Integer[][]> privateMessages;
	
	// temporary (until MOs and QueueBureaus are implemented in MVStore)
	public MVMap<Integer, String> moMessages;
	
	// databases
	private IUserDB userDB;

	public ChatDB(String moTableName, String moIdFieldName, String moTextFieldName) {
		MVStore store   = MVStoreAdapter.getStore();
		privateSenders  = store.openMap("smsappmodulechat.PrivateSenders",  new MVMap.Builder<String, String[]>());
		privateMessages = store.openMap("smsappmodulechat.PrivateMessages", new MVMap.Builder<String, Integer[][]>());
		//moMessages      = store.openMap("tmp.mos", new MVMap.Builder<Integer, Object[]>());
		userDB          = SMSAppModuleDALFactory.MVSTORE.getUserDB();
		
		// temporary
		moMessages      = store.openMap("tmp.mos", new MVMap.Builder<Integer, String>());
	}
	
	/** temporary, like we do with RAM */
	private static int lastMOId = 1;
	public int addMO(String moText) {
		synchronized (moMessages) {
			int moId = lastMOId++;
			moMessages.put(moId, moText);
			return moId;
		}
	}

	@Override
	public void reset() {
		privateSenders.clear();
		privateMessages.clear();
//		// temporary
//		moMessages.clear();
//		lastMOId = 1;
	}

	@Override
	public synchronized void logPrivateMessage(UserDto sender, UserDto recipient, int moId, int moTextStartIndex) {
		String conversationKey = sender.getPhoneNumber()+"_"+recipient.getPhoneNumber();
		Integer[][] messagesLog = privateMessages.get(conversationKey);
		Integer[][] newMessagesLog;
		int insertPosition;
		if (messagesLog != null) {
			newMessagesLog = new Integer[messagesLog.length+1][];
			System.arraycopy(messagesLog, 0, newMessagesLog, 0, messagesLog.length);
			insertPosition = messagesLog.length;
		} else {
			newMessagesLog = new Integer[1][];
			insertPosition = 0;
		}
		newMessagesLog[insertPosition] = new Integer[] {moId, moTextStartIndex};
		privateMessages.put(conversationKey, newMessagesLog);
	}

	@Override
	public UserDto[] getPrivatePeers(UserDto user) throws SQLException {
		String phoneNumberAsSender =    user.getPhoneNumber()+"_";
		String phoneNumberAsRecipient = "_"+user.getPhoneNumber();
		HashMap<String, UserDto> peers = new HashMap<String, UserDto>();
		for (Iterator<String> conversationKeyIterator = privateMessages.keyIterator(privateMessages.firstKey()); conversationKeyIterator.hasNext();) {
			String conversationKey = conversationKeyIterator.next();
			String peerPhoneNumber = null;
			if (conversationKey.startsWith(phoneNumberAsSender)) {
				peerPhoneNumber = conversationKey.substring(phoneNumberAsSender.length());
			} else if (conversationKey.endsWith(phoneNumberAsRecipient)) {
				peerPhoneNumber = conversationKey.substring(0, conversationKey.indexOf('_'));
			} else {
				continue;
			}
			if (!peers.containsKey(peerPhoneNumber)) {
				peers.put(peerPhoneNumber, userDB.assureUserIsRegistered(peerPhoneNumber));
			}
		}
		// return the array
		if (peers.size() == 0) {
			return null;
		}
		UserDto[] returnedPeers = new UserDto[peers.size()];
		int i=0;
		for (String key: peers.keySet()) {
			returnedPeers[i++] = peers.get(key);
		}
		return returnedPeers;
	}
	
	@Override
	public PrivateMessageDto[] getPrivateMessages(UserDto user1, UserDto user2) throws SQLException {
		
		String user1AsSenderConversationKey = user1.getPhoneNumber()+"_"+user2.getPhoneNumber();
		String user2AsSenderConversationKey = user2.getPhoneNumber()+"_"+user1.getPhoneNumber();
		Integer[][] user1AsSenderMessagesLog = privateMessages.get(user1AsSenderConversationKey);
		Integer[][] user2AsSenderMessagesLog = privateMessages.get(user2AsSenderConversationKey);

		ArrayList<PrivateMessageDto> returnedPrivateMessages = new ArrayList<PrivateMessageDto>();
		if (user1AsSenderMessagesLog != null) {
			for (int i=0; i<user1AsSenderMessagesLog.length; i++) {
				int moId             = user1AsSenderMessagesLog[i][0];
				int moTextStartIndex = user1AsSenderMessagesLog[i][1];
				String message       = moMessages.get(moId).substring(moTextStartIndex);
				returnedPrivateMessages.add(new PrivateMessageDto(user1, user2, moId, message));
			}
		}
		if (user2AsSenderMessagesLog != null) {
			for (int i=0; i<user2AsSenderMessagesLog.length; i++) {
				int moId             = user2AsSenderMessagesLog[i][0];
				int moTextStartIndex = user2AsSenderMessagesLog[i][1];
				String message       = moMessages.get(moId).substring(moTextStartIndex);
				returnedPrivateMessages.add(new PrivateMessageDto(user2, user1, moId, message));
			}
		}
		
		if (returnedPrivateMessages.size() == 0) {
			return null;
		}
		
		// sort
		Collections.sort(returnedPrivateMessages, new Comparator<PrivateMessageDto>() {
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
		
		return returnedPrivateMessages.toArray(new PrivateMessageDto[returnedPrivateMessages.size()]);
	}

}
