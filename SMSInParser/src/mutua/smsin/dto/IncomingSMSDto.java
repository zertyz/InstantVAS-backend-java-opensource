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

    /**
     *  The carriers where incoming messages originates and where outgoing messages should be routed to
     */
    public enum ESMSInParserCarrier {
	    TIM(159),
	    VIVO(139),
	    CLARO(160),
	    OI(150),				// verificar
	    NEXTEL(150),			// verificar
	    CTBC(150),				// verificar
	    SERCOMTEL(150),			// verificar
	    TEST_CARRIER(150),
	    UNKNOWN(150),

        ;

        private int maxMTChars;

        private ESMSInParserCarrier(int maxMTChars) {
            this.maxMTChars = maxMTChars;
        }

        public int getMaxMTChars() {
            return maxMTChars;
        }
    }

    private final String phone;
    private final String text;
    private final ESMSInParserCarrier carrierId;
    private final String largeAccount;
    private final String messageId;
    private final String[][] extraParameters;


    /**
     * Constructs a representation of an MO (incoming sms). The optional 'extra_parameters' is defined as:
     * extra_parameters := {{parameter_1_name, parameter_1_value}, ..., {parameter_n_name, parameter_n_value}}
     */
    public IncomingSMSDto(String phone, String text, ESMSInParserCarrier carrierId, String largeAccount, String messageId, String[][] extraParameters) {
        this.phone = phone;
        this.text = text;
        this.carrierId = carrierId;
        this.largeAccount = largeAccount;
        this.messageId = messageId;
        this.extraParameters = extraParameters;
    }

    public IncomingSMSDto(String phone, String text, ESMSInParserCarrier carrierId, String largeAccount, String messageId) {
        this(phone, text, carrierId, largeAccount, messageId, null);
    }


    public String getPhone() {
        return phone;
    }

    public String getText() {
        return text;
    }

    public ESMSInParserCarrier getCarrier() {
        return carrierId;
    }

    public String getLargeAccount() {
        return largeAccount;
    }

    public String getMessageId() {
        return messageId;
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
    public boolean equals(Object obj) {
        if (obj instanceof IncomingSMSDto) {
        	IncomingSMSDto other = (IncomingSMSDto)obj;
        	return this.phone.equals(other.phone) &&
        	       this.text.equals(other.text) && 
        	       this.carrierId.equals(other.carrierId) &&
        	       this.largeAccount.equals(other.largeAccount) &&
        	       this.messageId.equals(other.messageId) &&
        	       Arrays.equals(this.extraParameters, other.extraParameters);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "IncomingSMSDto [extraParameters=" + Arrays.toString(extraParameters) + ", messageId=" + 
               messageId + ", text=" + text + "]";
    }



//  /************************************
//  ** ILoggableRequest IMPLEMENTATION ** 
//  ************************************/
//
//  public void buildRequestDetails(StringBuffer request_details) {
//          String[][] extra_parameters = getExtraParameters();
//          request_details.append("phone='");
//          request_details.append(getPhone());
//          request_details.append("', text='");
//          request_details.append(getText());
//          request_details.append("', carrier='");
//          request_details.append(getCarrierId());
//          request_details.append("', la='");
//          request_details.append(getLargeAccount());
//          if (extra_parameters != null) {
//                  for (int i=0; i<extra_parameters.length; i++) {
//                          String parameter_name  = extra_parameters[i][0];
//                          String parameter_value = extra_parameters[i][1]; 
//                          request_details.append("', ");
//                          request_details.append(parameter_name);
//                          request_details.append("='");
//                          request_details.append(parameter_value);
//                          request_details.append("'");
//                  }
//          }
//          request_details.append(", id='");
//          request_details.append(getMessageId());
//          request_details.append("'");
//  }

}