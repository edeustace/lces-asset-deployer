import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;

import org.yaml.snakeyaml.Yaml;

import com.adobe.idp.Document;
import com.adobe.idp.dsc.clientsdk.ServiceClientFactory;
import com.adobe.idp.dsc.clientsdk.ServiceClientFactoryProperties;
import com.adobe.repository.bindings.dsc.client.ResourceRepositoryClient;
import com.adobe.repository.infomodel.Id;
import com.adobe.repository.infomodel.Lid;
import com.adobe.repository.infomodel.bean.RepositoryInfomodelFactoryBean;
import com.adobe.repository.infomodel.bean.Resource;
import com.adobe.repository.infomodel.bean.ResourceContent;
import com.adobe.repository.infomodel.bean.ResourceProperty;

public class Main {

	/**
	 */

	public static void main(String[] args) throws FileNotFoundException {

		System.out.println("lces-asset-deployer running");
		if (args.length != 1) {
			System.out.println("Usage: asset-deployer.jar myconfig.yml");
		}
		String configurationFilePath = args[0];
		String contents = getFileAsString(configurationFilePath);
		Yaml yaml = new Yaml();
		Object list = yaml.load(contents);

		if (!(list instanceof HashMap)) {

			System.out.println("Bad Yaml: not a hash map");
			return;
		}
		@SuppressWarnings("unchecked")
		HashMap<String, String> map = (HashMap<String, String>) list;
		String file = map.get("file");
		String assetName = map.get("asset_name");
		String lcesPath = map.get("lces_path");

		if (file == null || assetName == null || lcesPath == null) {
			System.out
					.println("You need to specify: file, asset_name and lces_path in your yml config");
			return;
		}
		// Optional
		String host = getValue(map.get("ejb_host"), "localhost");
		String port = getValue(map.get("ejb_port"), "1099");
		String username = getValue(map.get("username"), "administrator");
		String password = getValue(map.get("password"), "password");
		String mimeType = getValue(map.get("mime_type"),
				"application/x-shockwave-flash");
		String description = getValue(map.get("description"), assetName + " deployed on: "+ Calendar.getInstance().getTime().toString() );

		try {
			// Set connection properties required to invoke LiveCycle ES2
			Properties connectionProps = new Properties();
			connectionProps.setProperty(
					ServiceClientFactoryProperties.DSC_DEFAULT_EJB_ENDPOINT,
					"jnp://" + host + ":" + port);
			connectionProps.setProperty(
					ServiceClientFactoryProperties.DSC_TRANSPORT_PROTOCOL,
					ServiceClientFactoryProperties.DSC_EJB_PROTOCOL);
			connectionProps.setProperty(
					ServiceClientFactoryProperties.DSC_SERVER_TYPE, "JBoss");
			connectionProps.setProperty(
					ServiceClientFactoryProperties.DSC_CREDENTIAL_USERNAME,
					username);
			connectionProps.setProperty(
					ServiceClientFactoryProperties.DSC_CREDENTIAL_PASSWORD,
					password);

			ServiceClientFactory myFactory = ServiceClientFactory
					.createInstance(connectionProps);

			// Create a ResourceRepositoryClient object
			ResourceRepositoryClient repositoryClient = new ResourceRepositoryClient(
					myFactory);

			// Specify the parent path
			String parentResourcePath = lcesPath;

			// Create a RepositoryInfomodelFactoryBean object
			RepositoryInfomodelFactoryBean infomodelFactory = new RepositoryInfomodelFactoryBean(
					null);

			// Create a Resource object to add to the Repository

			Resource newResource = (Resource) infomodelFactory.newResource(
					new Id(), new Lid(), assetName);

			ResourceProperty rProperty = new ResourceProperty();
			rProperty.setNamespace("System");
			rProperty.setName("PRIMARY");
			rProperty.setValue("true");
			@SuppressWarnings("unchecked")
			Collection<Object> rProperties = newResource
					.getResourceProperties();
			rProperties.add(rProperty);

			// Create a ResourceContent object that contains the content (file
			// bytes)
			ResourceContent content = (ResourceContent) infomodelFactory
					.newResourceContent();

			// Create a Document that references an XDP file
			// to add to the Repository
			FileInputStream myForm = new FileInputStream(file);
			Document form = new Document(myForm);

			// Set the description and the MIME type
			content.setDataDocument(form);
			content.setMimeType(mimeType);

			// Assign content to the Resource object
			newResource.setContent(content);

			// Set a description of the resource
			newResource.setDescription(description);

			// Commit to repository, and update resource
			// in memory (by assignment)
			if (repositoryClient.resourceExists(lcesPath + "/" + assetName)) {
				repositoryClient.deleteResource(lcesPath + "/" + assetName);
			}

			Resource addResource = repositoryClient.writeResource(
					parentResourcePath, newResource);

			// Get the description of the returned Resource object
			System.out.println("New resource is: "
					+ addResource.getDescription());
			System.out.println("Complete");

			// Close the FileStream object
			myForm.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private static String getFileAsString(String path) {
		String out = "";
		File file = new File(path);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;

		try {
			fis = new FileInputStream(file);

			// Here BufferedInputStream is added for fast reading.
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);

			// dis.available() returns 0 if the file does not have more lines.
			while (dis.available() != 0) {

				// this statement reads the line from the file and print it to
				// the console.
				out += dis.readLine() + "\n";
			}

			// dispose all the resources after using them.
			fis.close();
			bis.close();
			dis.close();

			return out;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "";
	}

	private static String getValue(String val, String defaultVal) {
		return val == null ? defaultVal : val;

	}

}
