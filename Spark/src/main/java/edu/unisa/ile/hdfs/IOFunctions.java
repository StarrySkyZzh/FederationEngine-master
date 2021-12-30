package edu.unisa.ile.hdfs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class IOFunctions {

	/**
	 * create a existing file from local filesystem to hdfs
	 * 
	 * @param source
	 * @param dest
	 * @param conf
	 * @throws IOException
	 */

	public static void main(String[] args) throws IOException {
		String namenodeIP = "130.220.210.127";
		String namenodePort = "8020";
		String localFileAddress = "/Users/Shared/OneDrive/FederatedDataPlatformProject/testData/milad.txt";
		String hdfsFilePath = "/user/ile/document/";

		IOFunctions client = new IOFunctions();
		
		System.setProperty("HADOOP_USER_NAME", "hadoop");
		String hdfsPath = "hdfs://" + namenodeIP + ":" + namenodePort;
		Configuration conf = new Configuration();
		conf.set("fs.default.name", hdfsPath);
		conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
		conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
		client.deleteFile(hdfsFilePath + "testResult", conf);
//		client.addFile(localFileAddress, hdfsFilePath, conf);
//		client.readFile(hdfsFilePath + "milad.txt", conf);
		
	}
	
	public void addFile(String source, String dest, Configuration conf) throws IOException {

		FileSystem fileSystem = FileSystem.get(conf);

		// Get the filename out of the file path
		String filename = source.substring(source.lastIndexOf('/') + 1, source.length());

		// Create the destination path including the filename.
		if (dest.charAt(dest.length() - 1) != '/') {
			dest = dest + "/" + filename;
		} else {
			dest = dest + filename;
		}

		// System.out.println("Adding file to " + destination);

		// Check if the file already exists
		Path path = new Path(dest);
		if (fileSystem.exists(path)) {
			System.out.println("File " + dest + " already exists");
			return;
		}

		// Create a new file and write data to it.
		FSDataOutputStream out = fileSystem.create(path);
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(source)));

		byte[] b = new byte[1024];
		int numBytes = 0;
		while ((numBytes = in.read(b)) > 0) {
			out.write(b, 0, numBytes);
		}

		// Close all the file descriptors
		in.close();
		out.close();
		fileSystem.close();
	}

	/**
	 * read a file from hdfs
	 * 
	 * @param file
	 * @param conf
	 * @throws IOException
	 */
	public void readFile(String file, Configuration conf) throws IOException {
		FileSystem fileSystem = FileSystem.get(conf);

		Path path = new Path(file);
		if (!fileSystem.exists(path)) {
			System.out.println("File " + file + " does not exists");
			return;
		}

		FSDataInputStream in = fileSystem.open(path);

		String filename = file.substring(file.lastIndexOf('/') + 1, file.length());

		OutputStream out = new BufferedOutputStream(new FileOutputStream(new File(filename)));

		byte[] b = new byte[1024];
		int numBytes = 0;
		while ((numBytes = in.read(b)) > 0) {
			out.write(b, 0, numBytes);
		}

		in.close();
		out.close();
		fileSystem.close();
	}

	/**
	 * delete a directory in hdfs
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void deleteFile(String file, Configuration conf) throws IOException {
		FileSystem fileSystem = FileSystem.get(conf);

		Path path = new Path(file);
		if (!fileSystem.exists(path)) {
			System.out.println("File " + file + " does not exists");
			return;
		}

		fileSystem.delete(new Path(file), true);

		fileSystem.close();
	}

	/**
	 * create directory in hdfs
	 * 
	 * @param dir
	 * @throws IOException
	 */
	public void mkdir(String dir, Configuration conf) throws IOException {
		FileSystem fileSystem = FileSystem.get(conf);

		Path path = new Path(dir);
		if (fileSystem.exists(path)) {
			System.out.println("Dir " + dir + " already not exists");
			return;
		}

		fileSystem.mkdirs(path);

		fileSystem.close();
	}
}
