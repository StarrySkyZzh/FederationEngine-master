package edu.unisa.ile.DataIngestion;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;

public class FileExtraction {


	public static ArrayList<Map<String, Object>> getEntityFromFile(String source, Configuration conf)
			throws IOException {
		ArrayList<Map<String, Object>> fileList = new ArrayList<Map<String, Object>>();

		FileSystem fileSystem = FileSystem.get(conf);

		Path path = new Path(source);
		if (!fileSystem.exists(path)) {
			System.out.println("File " + source + " does not exists");
			return null;
		}

		RemoteIterator<LocatedFileStatus> files = fileSystem.listFiles(path, true);
		while (files.hasNext()) {
			String filePath = files.next().getPath().toString();
			System.out.println(filePath);
			String fileContent = HDFSUtilities.readFileToString(filePath, conf);
			Map<String, Object> fileMap = new LinkedHashMap<String, Object>();
			fileMap.put("documentId", filePath);
			fileMap.put(DBUtilities.getFileName(filePath), fileContent);
			fileList.add(fileMap);
		}
		return fileList;
	}
}
