package org.xmlite.client;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// try (XMLite client = new XMLite.Builder().setMethod("getCreds")
		// .setNamespace("http://wazalendo.neptunesoftwareplc.com/")
		// .setEndPointUrl("http://localhost:8080/test/services/RouterPort").setSoapVersion(XMLite.VER_12)
		// .setCache(true).setConnectTimeout(10,
		// TimeUnit.SECONDS).isDotNet(false)
		// .setReadTimeout(10, TimeUnit.SECONDS).build().sendPost()) {
		//
		// System.out.println(client.getComposedXMLBody());
		// System.out.println(client.getResponse().asJson());
		//
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		queryDotNetService();
	}

	static void queryDotNetService() {
		try (XMLite client = new XMLite.Builder().setMethod("getClasses")
				.setNamespace("http://www.neptunesoftwaregroup.com/")
				.setEndPointUrl("http://196.0.34.250:81/MicropayWS/Orbitlite.asmx").setSoapVersion(XMLite.VER_12)
				.addParameter("Amount", "60000").addParameter("VCode", "neptune").addParameter("VPswd", "secret")
				.isDotNet(true).setCache(true).setConnectTimeout(10, TimeUnit.SECONDS)
				.setReadTimeout(10, TimeUnit.SECONDS).build().sendPost()) {

			// System.out.println(client.getComposedXMLBody());
			System.out.println(client.getResponse().asJson());
			client.getResponse().asJson();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
