package mutua.smsappmodule.dal;

import static mutua.smsappmodule.SMSAppModuleChatTestCommons.addMO;
import static mutua.smsappmodule.SMSAppModuleChatTestCommons.moQueueLink;
import static mutua.smsappmodule.config.SMSAppModuleConfigurationChatTests.DEFAULT_CHAT_DAL;
import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;

import mutua.smsappmodule.DatabaseAlgorithmAnalysis;
import mutua.smsappmodule.SMSAppModuleTestCommons;
import mutua.smsappmodule.SplitRun;
import mutua.smsappmodule.config.SMSAppModuleConfigurationTests;
import mutua.smsappmodule.dto.PrivateMessageDto;
import mutua.smsappmodule.dto.UserDto;

import org.junit.BeforeClass;
import org.junit.Test;

/** <pre>
 * IChatDBPerformanceTests.java
 * ============================
 * (created by luiz, Sep 9, 2015)
 *
 * Measures and tests the f(n) of the O(f(n)) algorithm complexity implementations
 * of {@link IChatDB}
 *
 * @see IChatDB
 * @version $Id$
 * @author luiz
 */

public class IChatDBPerformanceTests {

	private static IChatDB chatDB = DEFAULT_CHAT_DAL.getChatDB();
	
	// algorithm settings
	private static int numberOfThreads = 4;

	// users table pre-fill
	private static int       totalNumberOfUsers = ((int)Math.ceil(Math.pow(SMSAppModuleConfigurationTests.PERFORMANCE_TESTS_LOAD_FACTOR, 1d/2d))) * ((DEFAULT_CHAT_DAL == SMSAppModuleDALFactoryChat.RAM) ? 89 : 18) * (4*numberOfThreads);	// please, pick a reasonable number, since approximately the square number of elements will be created (users + mos + privateMessages)
	private static long      phoneStart         = 991230000;
	private static UserDto[]           users    = new UserDto[totalNumberOfUsers];
	private static PrivateMessageDto[] pvts     = null;

	
	private static String firstMessage  = "Here is the message...";
	private static String secondMessage = "...and here is the answer";
	/** Inserts a private chat MO from every user to himself and every next one on the 'users' list, using 'p' concurrent threads and
	 *  computing also the replies. The private messages are placed into 'pvts' list, which will be returned and will have the length of
	 *  the double triangular number for 'users.length', that is: pvts.length := ((users.length*(users.length+1))/2)*2;
	 *  'users.length' must be divisible by '2*p' */
	private static PrivateMessageDto[] insertChatMOs(final UserDto[] users, final int p) throws SQLException, InterruptedException {
		final int _u = users.length / p;                                                        	// the work to be split, in number of 'users'
		final PrivateMessageDto[] pvts = new PrivateMessageDto[((users.length)*(users.length+1))];	// mos.length is the double triangular number for users.length
		final int _m = ((users.length+1)*_u)/2;                                                 	// the work to be split, in number of 'pvts'

		// compute conversation initiators
		for (int threadNumber=0; threadNumber<p; threadNumber++) {
			SplitRun.add(new SplitRun(threadNumber) {
				@Override
				public void splitRun(int threadNumber) throws SQLException {

					// on the following loops, 's' means the index for the sender user and
					// 'r' the index for the recipient user
					
					// to split the work of a triangular number evenly, we'll work by the borders:
                    // the first thread will process the first _n/2 and the last _n/2 elements;
                    // the second thread will do it for the intervals [(1*_n)/2, (2*_n)/2[ and [users.length-((2*n)/2),users.length-((1*n)/2)[
                    // and so on
					
					// _m is such that every thread will produce _m pvts
					
					int m = _m*threadNumber;	// the 'pvts' index
					
					// first interval: an _n/2 portion from the base of the pyramid
					for (int s=(threadNumber*_u)/2; s<((threadNumber+1)*_u)/2; s++) {
						for (int r=s; r<users.length; r++) {
							String moText = "M1i "+users[r].getPhoneNumber()+" "+firstMessage;
							int moId = addMO(chatDB, users[s], moText);
							pvts[m++] = new PrivateMessageDto(users[s], users[r], moId, firstMessage);
						}
					}
					// second interval: an _n/2 portion from the top of the pyramid
					for (int s=users.length-(((threadNumber+1)*_u)/2); s<users.length-((threadNumber*_u)/2); s++) {
						for (int r=s; r<users.length; r++) {
							String moText = "M2i "+users[r].getPhoneNumber()+" "+firstMessage;
							int moId = addMO(chatDB, users[s], moText);
							pvts[m++] = new PrivateMessageDto(users[s], users[r], moId, firstMessage);
						}
					}
				}
			});
		}
		SplitRun.runAndWaitForAll();

		// compute replies
		for (int threadNumber=0; threadNumber<p; threadNumber++) {
			SplitRun.add(new SplitRun(threadNumber) {
				@Override
				public void splitRun(int threadNumber) throws SQLException {

					int m = _m*(p+threadNumber);	// the 'pvts' index
					
					// first interval: an _n/2 portion from the base of the pyramid
					for (int r=(threadNumber*_u)/2; r<((threadNumber+1)*_u)/2; r++) {
						for (int s=r; s<users.length; s++) {
							String moText = "M1i "+users[r].getPhoneNumber()+" "+secondMessage;
							int moId = addMO(chatDB, users[s], moText);
							pvts[m++] = new PrivateMessageDto(users[s], users[r], moId, secondMessage);
						}
					}
					// second interval: an _n/2 portion from the top of the pyramid
					for (int r=users.length-(((threadNumber+1)*_u)/2); r<users.length-((threadNumber*_u)/2); r++) {
						for (int s=r; s<users.length; s++) {
							String moText = "M2i "+users[r].getPhoneNumber()+" "+secondMessage;
							int moId = addMO(chatDB, users[s], moText);
							pvts[m++] = new PrivateMessageDto(users[s], users[r], moId, secondMessage);
						}
					}
				}
			});
		}
		SplitRun.runAndWaitForAll();

		return pvts;
	}

	/*******************
	** COMMON METHODS **
	*******************/
	
	@BeforeClass
	public static void fulfillTables() {
		
		// fulfill Users table
		try {
			SMSAppModuleTestCommons.resetTables();
			SMSAppModuleTestCommons.insertUsers(phoneStart, users, numberOfThreads);
			// prepare the 'users' list for the tests
			Arrays.sort(users, new Comparator<UserDto>() {
				@Override
				public int compare(UserDto o1, UserDto o2) {
					return o1.getUserId() - o2.getUserId();
				}
			});

		} catch (Throwable t) {
			t.printStackTrace();
			throw new RuntimeException("Could not fulfill users table", t);
		}
		
		// fulfill Queue table
		try {
			if (moQueueLink != null) {
				moQueueLink.resetQueues();
			}
			pvts = insertChatMOs(users, numberOfThreads);
			
			System.out.println("PVTS  analysis: len="+pvts.length +"; _m="+(pvts.length/numberOfThreads));
			System.out.println("USERS analysis: len="+users.length+"; _u="+(users.length/numberOfThreads)+"; _m'="+((users.length+1)*(users.length/numberOfThreads))/2);
			System.out.println("null elements: ");
			int lnfe = -1;
			int snfc = -1;
			for (int i=0; i<pvts.length; i++) {
				if (pvts[i] == null) {
					if ((i-lnfe) > 1) {
						if (snfc != -1) {
							System.out.println(" --> "+snfc+" sequential elements");
						}
						snfc = 0;
					} else {
						snfc++;
					}
					System.out.print(i+"\t");
					lnfe = i;
				}
			}
			if (snfc > 0) {
				System.out.println(" --> "+snfc+" sequential elements");
			}
		} catch (Throwable t) {
			t.printStackTrace();
			throw new RuntimeException("Could not fulfill queue table", t);
		}
	}

	
	/**********
	** TESTS **
	**********/
	
	@Test
	public void testTriangularNumberedPrivateMessagesChatAlgorithmAnalysis() throws Throwable {

		final int inserts = pvts.length / 2;	// this requires that users.length is a multiple of 4*numbersOfThread, so that pvts.length can be a multiple of 2*numbersOfThread
		final int updates = users.length;
		final int selects = users.length;
		
		// prepare the tables & variables
		// this test depends on the 'users' list being sorted

		// TODO: refactor DatabaseAlgorithmAnalysis to allow custom test names. Here, the update test should be called "getPrivatePeers test" and the select test, "GetPrivateMessages test"
		new DatabaseAlgorithmAnalysis("IChatDB User's Triangular Numbers Bi-Directional Private Messages", numberOfThreads, inserts, updates, selects) {
			public void resetTables() throws SQLException {
				chatDB.reset();
			}
			public void insertLoopCode(int i) throws SQLException {
				// after the first insertion pass, all users will have either sent or received at least a message to/from every other
				UserDto sender        = pvts[i].getSender();
				UserDto recipient     = pvts[i].getRecipient();
				int moId              = pvts[i].getMoId();
				chatDB.logPrivateMessage(sender, recipient, moId, 14);	// 14 is the length of "M2i <phone> "
				
//				PrivateMessageDto[] privateMessages1 = chatDB.getPrivateMessages(sender, recipient);
//				PrivateMessageDto[] privateMessages2 = chatDB.getPrivateMessages(recipient, sender);
//				
//				assertArrayEquals("getPrivateMessages results are dependent on the factors order", privateMessages1, privateMessages2);
//				boolean found = false;
//				for (PrivateMessageDto pvt : privateMessages1) {
//					if (pvt.getMoId() == moId) {
//						found = true;
//					}
//				}
//				if (!found) {
//					System.out.println("---> " + new PrivateMessageDto(sender, recipient, moId, "#14").toString());
//					for (PrivateMessageDto pvt : privateMessages1) {
//						System.out.println(pvt.toString());
//					}
//				}
			}
			public void updateLoopCode(int i) throws SQLException {
				if ((i != 0) && (i != inserts)) {
					UserDto user1 = users[(i-1) % inserts];
					UserDto user2 = users[i % inserts];
					PrivateMessageDto[] privateMessages = chatDB.getPrivateMessages(user1, user2);
					if ((i/inserts) == 0) {	//	first pass
						assertEquals("Wrong number of private messages", 1, privateMessages.length);
						assertEquals("Wrong sender    for first message", user1,        privateMessages[0].getSender());
						assertEquals("Wrong recipient for first message", user2,        privateMessages[0].getRecipient());
						assertEquals("Wrong text      for first message", firstMessage, privateMessages[0].getMessage());
					} else {	// second pass
						assertEquals("Wrong number of private messages", 2, privateMessages.length);
						assertEquals("Wrong sender    for second message", user2,         privateMessages[1].getSender());
						assertEquals("Wrong recipient for second message", user1,         privateMessages[1].getRecipient());
						assertEquals("Wrong text      for second message", secondMessage, privateMessages[1].getMessage());
					}
				}
			}
			public void selectLoopCode(int i) throws SQLException {
				UserDto[] retrievedPeers = chatDB.getPrivatePeers(users[i % inserts]);
				assertEquals("Wrong number of peers retrieved", users.length, retrievedPeers.length);
				Arrays.sort(retrievedPeers, new Comparator<UserDto>() {
					@Override
					public int compare(UserDto o1, UserDto o2) {
						return o1.getUserId() - o2.getUserId();
					}
				});
				assertArrayEquals("Peers list differs", users, retrievedPeers);
			}
		};

	}
	
}
