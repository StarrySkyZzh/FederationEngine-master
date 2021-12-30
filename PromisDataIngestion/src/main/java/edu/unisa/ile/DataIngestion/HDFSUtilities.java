package edu.unisa.ile.DataIngestion;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;

public class HDFSUtilities {

	/**
	 * create a existing file from local filesystem to hdfs
	 * 
	 * @param source
	 * @param dest
	 * @param conf
	 * @throws IOException
	 */
	
	public static void addFile(String source, String dest, Configuration conf) throws IOException {

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
	 * @param source
	 * @param conf
	 * @throws IOException
	 */
	public static void readFile(String source, String destFolderPath, Configuration conf) throws IOException {
		FileSystem fileSystem = FileSystem.get(conf);

		Path path = new Path(source);
		if (!fileSystem.exists(path)) {
			System.out.println("File " + source + " does not exists");
			return;
		}

		if (fileSystem.isDirectory(path)){
			RemoteIterator<LocatedFileStatus> files = fileSystem.listFiles(path, true);
			while (files.hasNext()){
				String filePath = files.next().getPath().toString();
				System.out.println(filePath);
				readFile(filePath, destFolderPath, conf);
			}
			return;
		}
		
		FSDataInputStream in = fileSystem.open(path);

		String filename = destFolderPath + "/" + source.substring(source.lastIndexOf('/') + 1, source.length());

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
	public static void deleteFile(String file, Configuration conf) throws IOException {
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
	public static void mkdir(String dir, Configuration conf) throws IOException {
		FileSystem fileSystem = FileSystem.get(conf);

		Path path = new Path(dir);
		if (fileSystem.exists(path)) {
			System.out.println("Dir " + dir + " already not exists");
			return;
		}

		fileSystem.mkdirs(path);

		fileSystem.close();
	}
	
	public static String readFileToString(String sourceFilePath, Configuration conf) throws IOException {
		FileSystem fileSystem = FileSystem.get(conf);

		Path path = new Path(sourceFilePath);
		if (!fileSystem.exists(path)) {
			System.out.println("File " + sourceFilePath + " does not exists");
			return null;
		}
		
		FSDataInputStream in = fileSystem.open(path);
		BufferedReader bin = new BufferedReader(new InputStreamReader(in));
		String s = null;
		String fileTxt = "";
		while((s = bin.readLine()) != null){
			fileTxt += s + System.lineSeparator();
		}
		
//		System.out.println(fileTxt);
		in.close();
		fileSystem.close();
		return fileTxt;
	}
}

