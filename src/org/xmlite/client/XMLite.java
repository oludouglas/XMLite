package org.xmlite.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlite.codecs.Base64Coder;
import org.xmlite.json.JSONObject;
import org.xmlite.json.XML;

public class XMLite implements AutoCloseable {

	private static DocumentBuilder docBuilder;
	public static final int VER_10 = 0;
	public static final int VER_11 = 1;
	public static final int VER_12 = 2;

	private final String namespace, url, method, usr, pwd;
	private final Map<String, Object> params;
	private final StringBuilder tempBuilder = new StringBuilder();

	private static Document doc;
	private Element bodyElement;

	private ByteArrayOutputStream xmlStream = new ByteArrayOutputStream();
	public Object response;

	private HttpURLConnection urlConnection;

	private XMLite(Builder xmlBuilder) {

		try {
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("soap:Envelope");
			doc.appendChild(rootElement);

			switch (xmlBuilder.SOAP_VERSION) {
			case VER_10:
				rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
				rootElement.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
				rootElement.setAttribute("xmlns:soap", "http://schemas.xmlsoap.org/soap/envelope/");
				bodyElement = doc.createElement("soap:Body");
				rootElement.appendChild(bodyElement);

				break;
			case VER_11:
				rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
				rootElement.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
				rootElement.setAttribute("xmlns:soap", "http://schemas.xmlsoap.org/soap/envelope/");
				bodyElement = doc.createElement("soap:Body");
				rootElement.appendChild(bodyElement);
				break;
			case VER_12:
				rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
				rootElement.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
				rootElement.setAttribute("xmlns:soap", "http://www.w3.org/2003/05/soap-envelope");
				bodyElement = doc.createElement("soap:Body");
				rootElement.appendChild(bodyElement);
				break;
			default:
				rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
				rootElement.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
				rootElement.setAttribute("xmlns:soap", "http://schemas.xmlsoap.org/soap/envelope/");
				bodyElement = doc.createElement("soap:Body");
				rootElement.appendChild(bodyElement);
				break;
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		this.namespace = xmlBuilder.namespace;
		this.url = xmlBuilder.url;
		this.method = xmlBuilder.method;
		this.params = xmlBuilder.params;
		this.usr = xmlBuilder.username;
		this.pwd = xmlBuilder.password;

		buildXmlTree(xmlBuilder.isDotNet);

		try {
			urlConnection = (HttpURLConnection) new URL(getEndPointUrl()).openConnection();
			if (usr != null && pwd != null)
				urlConnection.setRequestProperty("Authorization",
						"Basic " + Base64Coder.encodeString("user" + ":" + "pwd"));

			urlConnection.setUseCaches(xmlBuilder.enableCache);
			urlConnection.setReadTimeout((int) xmlBuilder.readTimeout);
			urlConnection.setConnectTimeout((int) xmlBuilder.connectTimeout);
			urlConnection.setDefaultUseCaches(xmlBuilder.enableCache);

			if (xmlBuilder.SOAP_VERSION == VER_12)
				urlConnection.setRequestProperty("Content-Type", "application/soap+xml; charset=\"utf-8\"");
			else
				urlConnection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void buildXmlTree(boolean isDotNet) {

		try {
			Element methodElement = doc.createElement(method);
			if (isDotNet)
				methodElement.setAttribute("xmlns", namespace);
			bodyElement.appendChild(methodElement);

			for (Entry<String, Object> entry : params.entrySet()) {
				if (entry.getKey() != null && entry.getKey().length() > 0) {
					Element nickname = doc.createElement(entry.getKey());
					nickname.appendChild(doc.createTextNode(String.valueOf(entry.getValue())));
					methodElement.appendChild(nickname);
				}
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);

			StreamResult result = new StreamResult(xmlStream);
			transformer.transform(source, result);

		} catch (DOMException | TransformerFactoryConfigurationError | TransformerException e) {
			e.printStackTrace();
		}

	}

	public String getNamespace() {
		return this.namespace;
	}

	public String getEndPointUrl() {
		return this.url;
	}

	public String getMethod() {
		return this.method;
	}

	public Map<String, Object> getParams() {
		return this.params;
	}

	static class Builder {

		private String namespace, url, method, username, password;
		private Map<String, Object> params = new HashMap<>();
		private int SOAP_VERSION;
		private long readTimeout, connectTimeout;
		private boolean enableCache, isDotNet;

		public XMLite build() {
			XMLite user = new XMLite(this);
			validateObject(user);
			return user;
		}

		public Builder setNamespace(String namespace) {
			this.namespace = namespace;
			return this;
		}

		public Builder setEndPointUrl(String url) {
			this.url = url;
			return this;
		}

		public Builder setMethod(String method) {
			this.method = method;
			return this;
		}

		public Builder addParameter(String paramName, Object paramValue) {
			this.params.put(paramName, paramValue);
			return this;
		}

		private void validateObject(XMLite user) {
			// TODO Auto-generated method stub

		}

		public Builder setSoapVersion(int sOAP_VERSION) {
			this.SOAP_VERSION = sOAP_VERSION;
			return this;
		}

		public Builder setCache(boolean b) {
			this.enableCache = b;
			return this;
		}

		public Builder setAuthentication(String user, String password) {
			this.username = user;
			this.password = password;
			return this;
		}

		public Builder setConnectTimeout(int i, TimeUnit seconds) {
			this.connectTimeout = seconds.toMillis(i);
			return this;
		}

		public Builder setReadTimeout(int i, TimeUnit seconds) {
			this.readTimeout = seconds.toMillis(i);
			return this;
		}

		public Builder isDotNet(boolean b) {
			this.isDotNet = b;
			return this;
		}
	}

	public void serialize() {
		// TODO Auto-generated method stub

	}

	public XMLite convert(Class<?> class1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() throws IOException {
		// close network sockets
		if (urlConnection != null)
			urlConnection.disconnect();
	}

	public XMLite call() throws IOException {
		// Perform network call
		try {
			urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
			urlConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			urlConnection.setRequestProperty("Content-Length",
					String.valueOf(new String(xmlStream.toByteArray()).length()));
			urlConnection.setRequestProperty("SOAPAction",
					(getNamespace().trim().endsWith("/") ? getNamespace() : getNamespace().trim().concat("/"))
							+ getMethod().trim());

			// Send request

			try (OutputStreamWriter reqStream = new OutputStreamWriter(urlConnection.getOutputStream())) {
				reqStream.write(new String(xmlStream.toByteArray()));
				reqStream.flush();
			}

			InputStream is = urlConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);

			int numCharsRead;
			char[] charArray = new char[1024];
			StringBuffer sb = new StringBuffer();
			while ((numCharsRead = isr.read(charArray)) > 0) {
				sb.append(charArray, 0, numCharsRead);
			}

			response = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public String getResponseNotNull() {
		return response == null ? "" : String.valueOf(response);
	}

	public String getComposedXMLBody() {
		return new String(xmlStream.toByteArray());
	}

	public XMLite getComposedBody() {
		tempBuilder.setLength(0);
		tempBuilder.append(getComposedXMLBody());
		return this;
	}

	public JSONObject asJson() {
		if (tempBuilder == null)
			throw new NullPointerException("Response is Null");
		return XML.toJSONObject(tempBuilder.toString());
	}

	public XMLite sendPost() {
		try {
			urlConnection.setRequestMethod("POST");
			call();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public XMLite sendGet() {
		try {
			urlConnection.setRequestMethod("GET");
			call();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public XMLite sendDelete() {
		try {
			urlConnection.setRequestMethod("DELETE");
			call();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public XMLite sendOther(String method) {
		try {
			urlConnection.setRequestMethod(method.toUpperCase());
			call();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public XMLite getResponse() {
		tempBuilder.setLength(0);
		tempBuilder.append(response.toString());
		return this;
	}

}
