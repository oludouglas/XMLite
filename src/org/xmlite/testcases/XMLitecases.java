package org.xmlite.testcases;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmlite.client.XMLite;

public class XMLitecases {

	@Before
	public void setUp() throws Exception {
		Files.write(Paths.get(".gitignore"), "bin/*".getBytes());
	}

	@Test
	public void testBasicCall() {

		try (XMLite client = new XMLite.Builder().setMethod("getCreds")
				.setNamespace("http://wazalendo.neptunesoftwareplc.com/")
				.setEndPointUrl("http://localhost:8080/test/services/RouterPort").addParameter("yourname", "Douglas")// .addParameter("vCode",
																														// "neptune").addParameter("vPwd",
																														// "secret")
				.setSoapVersion(XMLite.VER_11).setCache(true).setConnectTimeout(10, TimeUnit.SECONDS).isDotNet(false)
				.setReadTimeout(10, TimeUnit.SECONDS).build().sendPost()) {

			Assert.assertNotNull(client.getResponseNotNull());
			Assert.assertNotNull(client.getComposedXMLBody());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAuthenticCall() {

		try (XMLite client = new XMLite.Builder().setMethod("getCreds")
				.setNamespace("http://wazalendo.neptunesoftwareplc.com/")
				.setEndPointUrl("http://localhost:8080/test/services/RouterPort").addParameter("yourname", "Douglas")
				.setAuthentication("password", "username").addParameter("vCode", "neptune")
				.addParameter("vPwd", "secret").setSoapVersion(XMLite.VER_11).setCache(true)
				.setConnectTimeout(10, TimeUnit.SECONDS).isDotNet(false).setReadTimeout(10, TimeUnit.SECONDS).build()
				.sendGet()) {

			Assert.assertNotNull(client.getResponseNotNull());
			Assert.assertNotNull(client.getComposedXMLBody());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testBasicBuild() {

		XMLite.Builder clientBuilder = new XMLite.Builder();
		clientBuilder.setMethod("getCreds");
		clientBuilder.setNamespace("http://wazalendo.neptunesoftwareplc.com/");
		clientBuilder.setEndPointUrl("http://localhost:8080/test/services/RouterPort");
		clientBuilder.addParameter("yourname", "Douglas");
		clientBuilder.setAuthentication("password", "username").addParameter("vCode", "neptune");
		clientBuilder.addParameter("vPwd", "secret");
		clientBuilder.setSoapVersion(XMLite.VER_11).setCache(true);
		clientBuilder.setConnectTimeout(10, TimeUnit.SECONDS);
		clientBuilder.isDotNet(false);
		clientBuilder.setReadTimeout(10, TimeUnit.SECONDS);

		XMLite client = clientBuilder.build();

		// Or build the object and call right away
		// XMLite client = clientBuilder.build().call();
		// you can also wrap it in try-with-resources

		Assert.assertNotNull(client.getResponseNotNull());
		Assert.assertNotNull(client.getComposedXMLBody());

		client.getResponse().asJson();

	}

}
