package config;

import static config.InstantVASLicense.*;
import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

/** <pre>
 * InstantVASLicenseTests.java
 * ===========================
 * (created by luiz, Apr 1, 2016)
 *
 * Tests the hard codes implied by the 'InstantVASLicense' variables.
 * 
 * Each test has a copy of the production code, which must be updated at the places specified -- using a CTRL-H
 * file search for each term is a good idea to find those places. 
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
*/

public class InstantVASLicenseTests {

	@Test
	public void testIFDEF_HARCODED_INSTANCE_RESTRICTION() {
		
		int AUTHENTICATION_TOKENParameterIndex  = 0;
		int REQUEST_STATUSParameterIndex        = 1;
		int PRECEDING_REQUEST_PARAMETERS_LENGTH = 2;
		
		String[][] addToMORequestValues = null;
		
		// test values for EQUALS method
		if (IFDEF_HARDCODE_CHECK_METHOD_OF_ADDITIONAL_MO_PARAMETER_VALUES == HardCodeCheckMethodOfAdditionalMOParameterValues_EQUALS) {
			addToMORequestValues = new String[][] {
				{"AiHfidSIfSmMd84ISi4", "reject", "5521998120594", "Vivo", "993", "1", "forca", "1", null},
				{"AiHfidSIfSmMd84ISi4", "reject", "5521998120594", "Vivo", "993", "1", "forca", null, null},
				{"AiHfidSIfSmMd84ISi4", "accept", "5521998120594", "Vivo", "993", "1", "forca", "1", "C1"},
				{"AiHfidSIfSmMd84ISi4", "reject", "5521998120594", "Vivo", "993", "1", "forca", "1", "C2"},
			};
		}
		
		// test values for REGEX method
		if (IFDEF_HARDCODE_CHECK_METHOD_OF_ADDITIONAL_MO_PARAMETER_VALUES == HardCodeCheckMethodOfAdditionalMOParameterValues_REGEX) {
			addToMORequestValues = new String[][] {
				{"AiHfidSIfSmMd84ISi4", "reject", "5521998120594", "Vivo", "993", "1", "forca", "1", null},
				{"AiHfidSIfSmMd84ISi4", "reject", "5521998120594", "Vivo", "993", "1", "forca", null, null},
				{"AiHfidSIfSmMd84ISi4", "accept", "5521998120594", "Vivo", "993", "1", "forca", "1", "C1"},
				{"AiHfidSIfSmMd84ISi4", "accept", "5521998120594", "Vivo", "993", "1", "forca", "1", "C1a"},
				{"AiHfidSIfSmMd84ISi4", "accept", "5521998120594", "Vivo", "993", "1", "forca", "1", "C1b"},
				{"AiHfidSIfSmMd84ISi4", "accept", "5521998120594", "Vivo", "993", "1", "forca", "1", "C1c"},
				{"AiHfidSIfSmMd84ISi4", "accept", "5521998120594", "Vivo", "993", "1", "forca", "1", "C1d"},
				{"AiHfidSIfSmMd84ISi4", "accept", "5521998120594", "Vivo", "993", "1", "forca", "1", "C1e"},
				{"AiHfidSIfSmMd84ISi4", "accept", "5521998120594", "Vivo", "993", "1", "forca", "1", "C1f"},
				{"AiHfidSIfSmMd84ISi4", "accept", "5521998120594", "Vivo", "993", "1", "forca", "1", "C1g"},
				{"AiHfidSIfSmMd84ISi4", "reject", "5521998120594", "Vivo", "993", "1", "forca", "1", "C1z"},
				{"AiHfidSIfSmMd84ISi4", "reject", "5521998120594", "Vivo", "993", "1", "forca", "1", "C1A"},
				{"AiHfidSIfSmMd84ISi4", "reject", "5521998120594", "Vivo", "993", "1", "forca", "1", "c1"},
				{"AiHfidSIfSmMd84ISi4", "reject", "5521998120594", "Vivo", "993", "1", "forca", "1", "C2"},
			};
		}

		for (String[] parameterValues : addToMORequestValues) {
			///// copied code start /////
			// should we deny the request?
			// code made in 'InstantVASLicenseTests' and shared between 'NavitaHTTPServer.ADD_TO_MO_QUEUE' and 'AddToMOQueue' servlet.
			if (// test the authentication token
			    ((INSTANTVAS_INSTANCE_CONFIGn_LENGTH > 0) && (!INSTANTVAS_INSTANCE_CONFIG0_TOKEN.equals(parameterValues[AUTHENTICATION_TOKENParameterIndex]))) ||
				// test additional MO parameter values -- EQUALS check method
			    ((IFDEF_HARDCODE_CHECK_METHOD_OF_ADDITIONAL_MO_PARAMETER_VALUES == HardCodeCheckMethodOfAdditionalMOParameterValues_EQUALS) && (
			     ((MO_ADDITIONAL_RULEn_LENGTH > 0) && (!MO_ADDITIONAL_RULE0_VALUE.equals(parameterValues[PRECEDING_REQUEST_PARAMETERS_LENGTH+MO_ADDITIONAL_RULE0_FIELD_INDEX])))
			    )) ||
				// test additional MO parameter values -- STARTS_WITH with MAX_LEN check method
			    ((IFDEF_HARDCODE_CHECK_METHOD_OF_ADDITIONAL_MO_PARAMETER_VALUES == HardCodeCheckMethodOfAdditionalMOParameterValues_STARTS_WITH) && (
			     ((MO_ADDITIONAL_RULEn_LENGTH > 0) && ( (parameterValues[PRECEDING_REQUEST_PARAMETERS_LENGTH+MO_ADDITIONAL_RULE0_FIELD_INDEX] == null) || (parameterValues[PRECEDING_REQUEST_PARAMETERS_LENGTH+MO_ADDITIONAL_RULE0_FIELD_INDEX].length() > MO_ADDITIONAL_RULE0_MAX_LEN) || (!parameterValues[PRECEDING_REQUEST_PARAMETERS_LENGTH+MO_ADDITIONAL_RULE0_FIELD_INDEX].startsWith(MO_ADDITIONAL_RULE0_VALUE))) )
			    )) ||
			    // test additional MO parameter values -- REGEX
			    ((IFDEF_HARDCODE_CHECK_METHOD_OF_ADDITIONAL_MO_PARAMETER_VALUES == HardCodeCheckMethodOfAdditionalMOParameterValues_REGEX) && (
			     ((MO_ADDITIONAL_RULEn_LENGTH > 0) && ( (parameterValues[PRECEDING_REQUEST_PARAMETERS_LENGTH+MO_ADDITIONAL_RULE0_FIELD_INDEX] == null) || (!MO_ADDITIONAL_RULE0_REGEX.matcher(parameterValues[PRECEDING_REQUEST_PARAMETERS_LENGTH+MO_ADDITIONAL_RULE0_FIELD_INDEX]).matches()) ) )
			   )) ) {
				///// copied code end /////
				assertTrue("Request '"+Arrays.deepToString(parameterValues)+"' should have been accepted", "reject".equals(parameterValues[REQUEST_STATUSParameterIndex]));
			} else {
				assertTrue("Request '"+Arrays.deepToString(parameterValues)+"' should have been rejected", "accept".equals(parameterValues[REQUEST_STATUSParameterIndex]));
			}
		}
		
	}

}
