package mutua.subscriptionengine;

import java.io.IOException;

import static mutua.subscriptionengine.SubscriptionEngineInstrumentationMethods.*;

import adapters.HTTPClientAdapter;


/** <pre>
 * CelltickLiveScreenAPI.java
 * ==========================
 * (created by luiz, Jan 8, 2015)
 *
 * Manages Celltick LiveScreen API requests
 *
 * @see RelatedClass(es)
 * @version $Id$
 * @author luiz
 */

public class CelltickLiveScreenSubscriptionAPI extends SubscriptionEngine {
	
	
	private final String liveScreenServiceBaseUrl; // = "http://localhost:8082/celltick/wapAPI // ?action=(un)subpkg&msisdn=%%MSISDN%%&pkgname=%%pkgname%%&charge=1"
	private final String channelName;
	
	private final HTTPClientAdapter lifecycleClient;


	public CelltickLiveScreenSubscriptionAPI(String liveScreenServiceBaseUrl, String channelName) {
		this.liveScreenServiceBaseUrl   = liveScreenServiceBaseUrl;
		this.channelName                = channelName;
		HTTPClientAdapter.configureDefaultValuesForNewInstances(-1, -1, false, "User-Agent", "InstantVAS.com lifecycle client");
		lifecycleClient = new HTTPClientAdapter(liveScreenServiceBaseUrl);
	}

	@Override
	public ESubscriptionOperationStatus subscribeUser(String userPhone) {
		
		String[] request = {
			"action",  "subpkg",
			"msisdn",  userPhone,
			"pkgname", channelName,
			"charge",  "1"};
		
		try {
			String response = lifecycleClient.requestGet(request);
			if (response.indexOf("<id>100</id>") != -1) {
				reportSubscriptionOK(channelName, liveScreenServiceBaseUrl, request, response);
				return ESubscriptionOperationStatus.OK;
			} else if (response.indexOf("<id>120</id>") != -1) {
				reportSubscriptionAlreadySubscribed(channelName, liveScreenServiceBaseUrl, request, response);
				return ESubscriptionOperationStatus.ALREADY_SUBSCRIBED;
			} else {
				reportSubscriptionAuthenticationError(channelName, liveScreenServiceBaseUrl, request, response);
				return ESubscriptionOperationStatus.AUTHENTICATION_ERROR;
			}
		} catch (IOException e) {
			reportSubscriptionCommunicationError(channelName, liveScreenServiceBaseUrl, request, e);
			return ESubscriptionOperationStatus.COMMUNICATION_ERROR;
		}
	}

	@Override
	public EUnsubscriptionOperationStatus unsubscribeUser(String userPhone) {

		String[] request = {
			"action",  "unsubpkg",
			"msisdn",  userPhone,
			"pkgname", channelName,
			"charge",  "1"};
		
		try {
			String response = lifecycleClient.requestGet(request);
			if (response.indexOf("<id>100</id>") != -1) {
				reportUnsubscriptionOK(channelName, liveScreenServiceBaseUrl, request, response);
				return EUnsubscriptionOperationStatus.OK;
			} else if (response.indexOf("<id>113</id>") != -1) {	// Packages not found for Subscriber
				reportUnsubscriptionNotSubscribed(channelName, liveScreenServiceBaseUrl, request, response);
				return EUnsubscriptionOperationStatus.NOT_SUBSCRIBED;
			} else {
				reportUnsubscriptionAuthenticationError(channelName, liveScreenServiceBaseUrl, request, response);
				return EUnsubscriptionOperationStatus.AUTHENTICATION_ERROR;
			}
		} catch (IOException e) {
			reportUnsubscriptionCommunicationError(channelName, liveScreenServiceBaseUrl, request, e);
			return EUnsubscriptionOperationStatus.COMMUNICATION_ERROR;
		}
	}
}
