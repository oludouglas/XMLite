# XMLite
This project aims to build a simple light weight XML library to handle XML generation, Serialization, De-serialization and maybe even XML to File, XML over HTTP, XML to JSON, XML to POJOs and back. Currently supports SOAP and XML OVER HTTP based requests and does not have any dependancies. just a single jar. 

Here's a sample request to query a Dot Net webservice. 

    try (XMLite client = new XMLite.Builder().setMethod("getClasses")
				.setNamespace("http://www.neptunesoftwaregroup.com/")
				.setEndPointUrl("http://0.0.0.0:8080/OpenSoftWS/OpenSoft.asmx").setSoapVersion(XMLite.VER_12)
				.addParameter("Username", "Olu").addParameter("VendorCode", "opensoft").addParameter("VendorPswd", "secret")
				.isDotNet(true).setCache(true).setConnectTimeout(10, TimeUnit.SECONDS)
				.setReadTimeout(10, TimeUnit.SECONDS).build().sendPost()) {
				
		   System.out.println(client.getComposedXMLBody());
	       System.out.println(client.getResponse().asJson());
    } catch (IOException e) {
			e.printStackTrace();
	 }

	
#Other few method calls include 

     XMLite client = new XMLite.Builder().sendGet();
     XMLite client = new XMLite.Builder().sendOther("GET");
     client.getResponse().asJson();
     

     
