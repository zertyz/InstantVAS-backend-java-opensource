package mutua.smsappmodule.dal.mvstore;

import java.sql.SQLException;

import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;

import adapters.MVStoreAdapter;
import mutua.smsappmodule.dal.IProfileDB;
import mutua.smsappmodule.dal.IUserDB;
import mutua.smsappmodule.dal.SMSAppModuleDALFactory;
import mutua.smsappmodule.dto.ProfileDto;
import mutua.smsappmodule.dto.UserDto;

/** ProfileDB.java
 * ===============
 * (created by luiz, Sep 4, 2018)
 *
 * Implements the MVStore version of {@link IProfileDB}.
 * 
 * @see IProfileDB
 * @author luiz
*/

public class ProfileDB implements IProfileDB {
	
	// nicknameByPhone          := {[phone] = nickname, ...}}
	// phoneByLowerCaseNickname := {[lowerCaseNickname] = phone, ...}}
	private MVMap<String, String> nicknameByPhone;
	private MVMap<String, String> phoneByLowerCaseNickname;
	
	// databases
	private IUserDB userDB;
	
	public ProfileDB() {
		MVStore store   = MVStoreAdapter.getStore();
		nicknameByPhone          = store.openMap("smsappmoduleprofile.NicknameByPhone",  new MVMap.Builder<String, String>());
		phoneByLowerCaseNickname = store.openMap("smsappmoduleprofile.PhoneByNickname",  new MVMap.Builder<String, String>());
		userDB                   = SMSAppModuleDALFactory.MVSTORE.getUserDB();
	}

	@Override
	public void reset() {
		nicknameByPhone         .clear();
		phoneByLowerCaseNickname.clear();
	}

	@Override
	public ProfileDto getProfileRecord(UserDto user) throws SQLException {
		String nickname = nicknameByPhone.get(user.getPhoneNumber());
		if (nickname != null) {
			return new ProfileDto(user, nickname);
		} else {
			return null;
		}
	}

	@Override
	public ProfileDto getProfileRecord(String nickname) throws SQLException {
		String phone = phoneByLowerCaseNickname.get(nickname.toLowerCase());
		if (phone != null) {
			String corectlyCasedNickname = nicknameByPhone.get(phone);
			UserDto user = userDB.assureUserIsRegistered(phone);
			return new ProfileDto(user, corectlyCasedNickname);
		} else {
			return null;
		}
	}

	@Override
	public synchronized ProfileDto setProfileRecord(ProfileDto profile) {
		String phone             = profile.getUser().getPhoneNumber();
		String nickname          = profile.getNickname();
		String lowerCaseNickname = nickname.toLowerCase();
		// not so simple case 1: nickname is already taken
		String ownerOfNicknamePhone = phoneByLowerCaseNickname.get(lowerCaseNickname);
		// not so simple case 2: nick is taken by the same user -- in this case we treat it as not taken
		boolean isNicknameTaken     = (ownerOfNicknamePhone != null) && (!ownerOfNicknamePhone.equals(phone));
		// if nickname is taken, we must resolve the collision -- adding a sequential number at the end of it
		if (isNicknameTaken) {
			for (int attempt=1; ; attempt++) {
				String lowerNicknameCandidate = lowerCaseNickname + attempt;
				String ownerOfCandidateNicknamePhone = phoneByLowerCaseNickname.get(lowerNicknameCandidate);
				if ((ownerOfCandidateNicknamePhone == null) || phone.equals(ownerOfCandidateNicknamePhone)) {
					String newNickname = nickname + attempt;
					nickname          = newNickname;
					lowerCaseNickname = nickname.toLowerCase();
					profile = new ProfileDto(profile.getUser(), nickname);
					break;
				}
				if (attempt == 10000) {
					throw new RuntimeException("Could not assign nickname '"+nickname+"' to user phone '"+phone+"': all collision slots are taken!");
				}
			}
		}
		// simple case: just insert
		nicknameByPhone.put(phone, nickname);
		phoneByLowerCaseNickname.put(lowerCaseNickname, phone);
		return profile;
	}

	@Override
	public ProfileDto[] getRecentProfilesByLastMOTimeNotInSessionValues(int limit, String sessionPropertyName, String... notInSessionPropertyValues) throws SQLException {
		throw new RuntimeException("Not Implemented!");
	}

}
