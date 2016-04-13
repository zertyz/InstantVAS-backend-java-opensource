package mutua.smsin.dto;

import java.util.Arrays;

/** <pre>
 * IncomingSMSDto.java
 * ===================
 * (created by luiz, Dec 8, 2008)
 *
 * Represents an incoming SMS request to an SMS Application, that is, an MO
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class IncomingSMSDto /*implements ILoggableRequest*/ {

    /** The carriers where incoming messages originates and where outgoing messages should be routed to */
    public enum ESMSInParserCarrier {
//	    TIM(159),
//	    VIVO(139),
//	    CLARO(160),
//	    OI(150),				// verificar
//	    NEXTEL(150),			// verificar
//	    CTBC(150),				// verificar
//	    SERCOMTEL(150),			// verificar
//	    TEST_CARRIER(150),
//	    UNKNOWN(150),
    	// celtick parameters
    	TIM(134),
	    VIVO(134),
	    CLARO(134),
	    OI(134),
	    NEXTEL(134),
	    CTBC(134),
	    SERCOMTEL(134),
	    TEST_CARRIER(134),
	    UNKNOWN(134),

        ;

        private int maxMTChars;

        private ESMSInParserCarrier(int maxMTChars) {
            this.maxMTChars = maxMTChars;
        }

        public int getMaxMTChars() {
            return maxMTChars;
        }
    }

    private int    moId;
    private String phone;
    private String text;
    private ESMSInParserCarrier carrier;
    private String largeAccount;
    private String originalMoId;
    private String[][] extraParameters;


    /** Constructs a representation of an MO (incoming sms). The optional 'extra_parameters' is defined as:
     *  extra_parameters := {{parameter_1_name, parameter_1_value}, ..., {parameter_n_name, parameter_n_value}} */
    public IncomingSMSDto(int moId, String phone, String text, ESMSInParserCarrier carrier, String largeAccount, String[][] extraParameters) {
        this.moId            = moId;
        this.phone           = phone;
        this.text            = text;
        this.carrier         = carrier;
        this.largeAccount    = largeAccount;
        this.originalMoId    = null;
        this.extraParameters = extraParameters;
    }
    
    public IncomingSMSDto(String originalMoId, String phone, String text, ESMSInParserCarrier carrier, String largeAccount) {
    	this(-1, phone, text, carrier, largeAccount);
    	this.originalMoId = originalMoId;
    }

    public IncomingSMSDto(int moId, String phone, String text, ESMSInParserCarrier carrier, String largeAccount) {
        this(moId, phone, text, carrier, largeAccount, null);
    }

    public int getMoId() {
        return moId;
    }

    public String getPhone() {
        return phone;
    }

    public String getText() {
        return text;
    }

    public ESMSInParserCarrier getCarrier() {
        return carrier;
    }

    public String getLargeAccount() {
        return largeAccount;
    }
    
    public String getOriginalMoId() {
    	return originalMoId;
    }

    public String getExtraParameter(String parameterName) {
        if (extraParameters != null) {
            for (int i=0; i<extraParameters.length; i++) {
                if (extraParameters[i][0].equals(parameterName)) {
                    return extraParameters[i][1];
                }
            }
        }
        return null;
    }

    public String[][] getExtraParameters() {
        return extraParameters;
    }

    @Override
    public String toString() {
        return new StringBuffer("phone='").
        	append(phone).append("', text='").append(text.replace("\n", "\\n")).append("', carrier=").
        	append(carrier != null ? carrier.name() : "NULL").append(", largeAccount=").
        	append(largeAccount).append(", messageId='").append(moId).
        	append("', extraParameters=").append(Arrays.toString(extraParameters)).
            toString();
    }

}