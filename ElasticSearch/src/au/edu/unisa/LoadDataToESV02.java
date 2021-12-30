package au.edu.unisa;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import java.io.*;

/*Load the output files of Apache Spark  into ES*/
public class LoadDataToESV02 {

	public static void LoadData(String index, String type, int documentId) {

		String indexName = index;
		String typeName = type;
		String serverURL = new String("localhost");
		IndexResponse response = null;
		int documentID = documentId;

		List<FilesBean> sparkOutput = new ArrayList<FilesBean>();

		try {

			System.out.println("Begin to injest spark output!");

			/*
			 * Getting all the filenames and all the corresponding files in a
			 * folder
			 */
			File folder = new File("/Users/fengz/Documents/workspace4Eclipse/LoadDataToES/resources/output_0");

			File[] listOfFiles = folder.listFiles();

			for (File file : listOfFiles) {

				FilesBean filesBean = new FilesBean();

				if (file.isFile()) {

					filesBean.setFileName(file.getName());
					filesBean.setFileContent(new BufferedReader(new FileReader(file)));
				}

				sparkOutput.add(filesBean);

			}

			/* Create a new List to contain the json string for the whole */

			ObjectMapper objectMapper = new ObjectMapper();

			/* Obtain the transport client of ElasticSearch */
			Settings settings = Settings.settingsBuilder().put("elasticsearch", "Annie Ghazikhanian")
					.put("client.transport.sniff", true).build();

			TransportClient client = new TransportClient.Builder().settings(settings).build();

			client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(serverURL), 9300));

			/*
			 * Iterate 'sparkOutput', and convert 'sparkOutput' to Json Object,
			 * and then load Json Object to ES
			 */

			Map<String, Object> rawData = new HashMap<String, Object>();

			FilesBean fileBean_1 = sparkOutput.get(1);

			FilesBean fileBean_2 = sparkOutput.get(2);

			/* 1st Element */
			rawData.put(fileBean_2.getFileName(), fileBean_1.getFileName());

			/* the rest Elements, which are the concrete elements */
			BufferedReader second_file_content = fileBean_2.getFileContent();

			/* convert bufferedReader object to a string */
			String eachRow = null;
			while ((eachRow = second_file_content.readLine()) != null) {

				/*
				 * convert a string formed as '(a,b)' to 'string[]', which is an
				 * element of hash map
				 */
				String[] str = Utilities.extractKeyElement(eachRow);

				rawData.put(str[0], str[1]);

			}

			/* convert Map Object to Json String */
			String rawJsonData = objectMapper.writeValueAsString(rawData);

			response = client.prepareIndex(indexName, typeName, Integer.toString(documentID)).setSource(rawJsonData)
					.get();

			System.out.println("load is finished...");

		} catch (JsonGenerationException e) {

			e.printStackTrace();
		} catch (JsonMappingException e) {

			e.printStackTrace();
		} catch (ElasticsearchException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} catch (Exception e) {

			System.err.println("Exception" + e.getMessage());
		}

	}

}
