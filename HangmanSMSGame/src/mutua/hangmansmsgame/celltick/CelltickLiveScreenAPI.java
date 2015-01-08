package mutua.hangmansmsgame.celltick;

import java.io.IOException;

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

public class CelltickLiveScreenAPI {
	
	public static String REGISTER_SUBSCRIBER_URL   = "http://localhost:8082/celltick/wapAPI?action=subpkg&msisdn=%%MSISDN%%&pkgname=HangMan&charge=1";
	public static String UNREGISTER_SUBSCRIBER_URL = "http://localhost:8082/celltick/wapAPI?action=unsubpkg&msisdn=%%MSISDN%%&pkgname=HangMan&charge=1";


	public static boolean registerSubscriber(String phone) {
		try {
			String url = REGISTER_SUBSCRIBER_URL.replaceAll("%%MSISDN%%", phone);
			String response = HTTPClientAdapter.requestGet(url, null, "UTF-8");
			System.out.println("registerSubscriber --> " + url + "\n<-- '" + response + "'");
			if (response.indexOf("<status><id>100</id>") != -1) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean unregisterSubscriber(String phone) {
		try {
			String url = UNREGISTER_SUBSCRIBER_URL.replaceAll("%%MSISDN%%", phone);
			String response = HTTPClientAdapter.requestGet(url, null, "UTF-8");
			System.out.println("unregisterSubscriber --> " + url + "\n<-- '" + response + "'");
			if (response.indexOf("<status><id>100</id>") != -1) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
