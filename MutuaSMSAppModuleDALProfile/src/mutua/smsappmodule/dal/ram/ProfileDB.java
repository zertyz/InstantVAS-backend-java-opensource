package mutua.smsappmodule.dal.ram;

import java.sql.SQLException;
import java.util.Hashtable;

import mutua.smsappmodule.dal.IProfileDB;
import mutua.smsappmodule.dto.ProfileDto;
import mutua.smsappmodule.dto.UserDto;

/** <pre>
 * ProfileDB.java
 * ==============
 * (created by luiz, Aug 3, 2015)
 *
 * Implements the RAM version of {@link IProfileDB}
 *
 * @see IProfileDB
 * @version $Id$
 * @author luiz
 */

public class ProfileDB implements IProfileDB {
	
	// data structures
	//////////////////
	
	private static Hashtable<UserDto, ProfileDto> profilesByUser = new Hashtable<UserDto, ProfileDto>(15641,1); // 15625 * 2^7 = 2002048
	private static Hashtable<String,  ProfileDto> profilesByNick = new Hashtable<String, ProfileDto>(15641,1);
	
	
	// common methods
	/////////////////
	
	private void setNickname(UserDto user, String nickname) {
		ProfileDto profileByUser   = profilesByUser.get(user);
		String     currentNickname = profileByUser.getNickname();
		ProfileDto profileByNick   = profilesByNick.get(nickname);
	}
	
	
	// IProfileDB implementation
	////////////////////////////

	@Override
	public void reset() throws SQLException {
		profilesByUser.clear();
		profilesByNick.clear();
	}

	@Override
	public ProfileDto getProfileRecord(UserDto user) throws SQLException {
		return profilesByUser.get(user);
	}

	@Override
	public ProfileDto getProfileRecord(String nickname) {
		return profilesByNick.get(nickname.toLowerCase());
	}

	@Override
	public synchronized ProfileDto setProfileRecord(ProfileDto profile) throws SQLException {
		UserDto user = profile.getUser();
		// detect nickname collision
		String baseNickname      = profile.getNickname();
		String lowerBaseNickname = baseNickname.toLowerCase();
		if (profilesByNick.containsKey(lowerBaseNickname)) {
			// detect if the nickname collision is due to the same user attempting to update (the case of) his own nick
			UserDto nicknameOwningUser = profilesByNick.get(lowerBaseNickname).getUser();
			if (user.getUserId() != nicknameOwningUser.getUserId()) {
				for (int attempt=1; attempt<10000; attempt++) {
					String lowerNewNickname = lowerBaseNickname + Integer.toString(attempt);
					if (!profilesByNick.containsKey(lowerNewNickname)) {
						String newNickname = baseNickname + Integer.toString(attempt);
						profile = new ProfileDto(user, newNickname);
						break;
					} else {
						nicknameOwningUser = profilesByNick.get(lowerNewNickname).getUser();
						if (user.getUserId() == nicknameOwningUser.getUserId()) {
							String newNickname = baseNickname + Integer.toString(attempt);
							profile = new ProfileDto(user, newNickname);
							break;
						}
					}
				}
			}
		}
		profilesByUser.put(user, profile);
		profilesByNick.put(profile.getNickname().toLowerCase(), profile);
		return profile;
	}

}
