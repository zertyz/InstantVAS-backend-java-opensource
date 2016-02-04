package mutua.smsappmodule.smslogic.sessions;

import static org.junit.Assert.*;
import mutua.smsappmodule.dto.SessionDto;
import mutua.smsappmodule.dto.UserDto;
import mutua.smsappmodule.smslogic.navigationstates.INavigationState;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import org.junit.Test;

/** <pre>
 * SessionModelTests.java
 * ======================
 * (created by luiz, Jul 23, 2015)
 *
 * Test the {@link #SessionModel} class
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class SessionModelTests {
	
	private static ISessionProperty prop1 = new ISessionProperty() {public String getPropertyName() {return "prop1";}};
	private static ISessionProperty prop2 = new ISessionProperty() {public String getPropertyName() {return "prop2";}};
	private static ISessionProperty prop3 = new ISessionProperty() {public String getPropertyName() {return "prop3";}};


	@Test
	public void testInsertedUpdatedAndDeletedSessionProperties() {
		String expectedProp1Value = "new value1";
		String expectedProp2Value = "value2";
		String expectedProp3Value = "";
		
		SessionModel session = new SessionModel(new SessionDto(null, new String[][] {
			{prop1.getPropertyName(), "value1"},
			{prop3.getPropertyName(), "value3"},
		}), null) {
			public INavigationState getNavigationStateFromStateName(String navigationStateName) {
				throw new NotImplementedException();
			}
		};
		
		session.setProperty(prop1, expectedProp1Value);
		session.setProperty(prop3, expectedProp3Value);
		session.setProperty(prop2, expectedProp2Value);
		
		assertEquals("Wrong prop1 value", expectedProp1Value, session.getStringProperty(prop1));
		assertEquals("Wrong prop2 value", expectedProp2Value, session.getStringProperty(prop2));
		assertEquals("Wrong prop3 value", expectedProp3Value, session.getStringProperty(prop3));
		
		String[][] newProperties     = session.getNewProperties();
		String[][] updatedProperties = session.getUpdatedProperties();
		String[]   deletedProperties = session.getDeletedProperties();
		
		assertEquals("Wrong new properties names",  prop2.getPropertyName(), newProperties[0][0]);
		assertEquals("Wrong new properties values", expectedProp2Value,      newProperties[0][1]);
		
		assertEquals("Wrong updated properties names",  prop1.getPropertyName(), updatedProperties[0][0]);
		assertEquals("Wrong updated properties values", expectedProp1Value,      updatedProperties[0][1]);
		
		assertEquals("Wrong deleted properties names",  prop3.getPropertyName(), deletedProperties[0]);
		
		SessionDto sessionDto = session.getChangedSessionDto();
		assertArrayEquals("Wrong   new   properties from SessionDto", newProperties,     sessionDto.getNewProperties());
		assertArrayEquals("Wrong updated properties from SessionDto", updatedProperties, sessionDto.getUpdatedProperties());
		assertArrayEquals("Wrong deleted properties from SessionDto", deletedProperties, sessionDto.getDeletedProperties());
	}
	
	@Test
	public void testUnchangedSessionProperties() {
		String expectedProp1Value = "value1";
		String expectedProp2Value = "value2";
		String expectedProp3Value = "value3";
		
		SessionModel session = new SessionModel(new SessionDto(null, new String[][] {
			{prop1.getPropertyName(), expectedProp1Value},
			{prop2.getPropertyName(), expectedProp2Value},
			{prop3.getPropertyName(), expectedProp3Value},
		}), null) {
			public INavigationState getNavigationStateFromStateName(String navigationStateName) {
				throw new NotImplementedException();
			}
		};
		
		assertEquals("Wrong prop1 value", expectedProp1Value, session.getStringProperty(prop1));
		assertEquals("Wrong prop2 value", expectedProp2Value, session.getStringProperty(prop2));
		assertEquals("Wrong prop3 value", expectedProp3Value, session.getStringProperty(prop3));
		
		String[][] newProperties     = session.getNewProperties();
		String[][] updatedProperties = session.getUpdatedProperties();
		String[]   deletedProperties = session.getDeletedProperties();

		assertEquals("Wrong new properties",     0, newProperties.length);
		assertEquals("Wrong updated properties", 0, updatedProperties.length);
		assertEquals("Wrong deleted properties", 0, deletedProperties.length);

		SessionDto sessionDto = session.getChangedSessionDto();
		assertArrayEquals("Wrong   new   properties from SessionDto", newProperties,     sessionDto.getNewProperties());
		assertArrayEquals("Wrong updated properties from SessionDto", updatedProperties, sessionDto.getUpdatedProperties());
		assertArrayEquals("Wrong deleted properties from SessionDto", deletedProperties, sessionDto.getDeletedProperties());
	}
	
	@Test
	public void testEmptySessionProperties() {
		SessionModel session = new SessionModel((UserDto)null, null) {
			public INavigationState getNavigationStateFromStateName(String navigationStateName) {
				throw new NotImplementedException();
			}
		};
		
		assertNull("non-existent string prop1 should be null", session.getStringProperty(prop1));
		assertEquals("non-existent int prop2 should be -1", -1, session.getIntProperty(prop2));
		
		assertEquals("Wrong new properties",     0, session.getNewProperties().length);
		assertEquals("Wrong updated properties", 0, session.getUpdatedProperties().length);
		assertEquals("Wrong deleted properties", 0, session.getDeletedProperties().length);
		
		SessionDto sessionDto = session.getChangedSessionDto();
		assertArrayEquals("Wrong   new   properties from SessionDto", session.getNewProperties(),     sessionDto.getNewProperties());
		assertArrayEquals("Wrong updated properties from SessionDto", session.getUpdatedProperties(), sessionDto.getUpdatedProperties());
		assertArrayEquals("Wrong deleted properties from SessionDto", session.getDeletedProperties(), sessionDto.getDeletedProperties());
	}
	
	@Test
	public void testIntProperty() {
		SessionModel session = new SessionModel(new SessionDto(null, new String[][] {
			{prop1.getPropertyName(), "thisisnotanint"},	
		}), null) {
			public INavigationState getNavigationStateFromStateName(String navigationStateName) {
				throw new NotImplementedException();
			}
		};
		
		assertEquals("non-integer prop1 should return -1 if treated as an int", -1, session.getIntProperty(prop1));
	}

}
