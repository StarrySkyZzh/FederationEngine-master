package HDFSJavaAPIExample;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class IOExample {

	/**
	 * create a existing file from local filesystem to hdfs
	 *
	 * @throws IOException
	 */

	public static void main(String[] args) throws IOException {
		String namenodeIP = "103.61.226.39";
		String namenodePort = "8020";
		String localFileAddress = "/Users/Shared/OneDrive/FederatedDataPlatformProject/testData/HDFS data samples";
		String hdfsFilePath = "/user/ile/document/";

		IOExample client = new IOExample();
		String hdfsPath = "hdfs://" + namenodeIP + ":" + namenodePort;
		Configuration conf = new Configuration();
		conf.set("fs.default.name", hdfsPath);

        System.setProperty("HADOOP_USER_NAME", "hduser");

		conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
		conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
//		client.deleteFile(hdfsFilePath + "milad.txt", conf);
//		client.addFile(localFileAddress, hdfsFilePath, conf);
//        client.addBatchFiles(localFileAddress, hdfsFilePath, conf);
		client.readFile(hdfsFilePath + "input.txt", conf);
//        client.readFile( "http://103.61.226.39:50070/webhdfs/v1/user/ile/document/FEDSA.docx?op=OPEN", conf);
	}

	public void addBatchFiles(String source, String target, Configuration conf)throws IOException {
        File folder = new File(source);
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                addFile(listOfFiles[i].getAbsolutePath(),target,conf);
            }
        }
    }


	
	public void addFile(String source, String target, Configuration conf) throws IOException {

		FileSystem fileSystem = FileSystem.get(conf);

		// Get the filename out of the file path
		String filename = source.substring(source.lastIndexOf('/') + 1, source.length());

		// Create the destination path including the filename.
		if (target.charAt(target.length() - 1) != '/') {
			target = target + "/" + filename;
		} else {
			target = target + filename;
		}

		// System.out.println("Adding file to " + destination);

		// Check if the file already exists
		Path path = new Path(target);
		if (fileSystem.exists(path)) {
			System.out.println("File " + target + " already exists");
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
