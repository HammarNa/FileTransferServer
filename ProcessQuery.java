package fileTransferServer;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProcessQuery {

	/**
	 * 
	 * process not foud path
	 * 
	 * @param outStream
	 */
	public static void errorProcess(DataOutputStream outStream) {

		String str = "HTTP/1.1 200 OK\n\n" + "<html><p>" + "no such file." + "</p></html>";

		try {
			outStream.write(str.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * process path identified as directory
	 * 
	 * @param targetPath
	 * @param outStream
	 */
	public static void directoryProcess(Path targetPath, DataOutputStream outStream) {

		String directoryContent = "";
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(targetPath)) {
			for (Path file : stream) {
				if (Files.isDirectory(file)) {
					directoryContent += "<a href=" + file.toAbsolutePath() + ">";
					directoryContent += file.toString() + "    ------------------ DIRECTORY </a><br/>";
				} else {
					directoryContent += "<a href=" + file.toAbsolutePath() + ">";
					directoryContent += file.toString() + "    ............ size: " + Files.size(file) / 1024
							+ " Ko </a><br/>";
				}
			}

			String str = "HTTP/1.1 200 OK\n\n" + "<html><p>" + directoryContent + "</p></html>";

			outStream.write(str.getBytes());

		} catch (IOException | DirectoryIteratorException x) {
			// IOException can never be thrown by the iteration.
			// In this snippet, it can only be thrown by newDirectoryStream.
			System.err.println(x);
		}
	}

	/**
	 * 
	 * process path identified as file
	 * 
	 * @param targetPath
	 * @param outStram
	 * @throws IOException
	 */
	public static void fileProcess(Path targetPath, DataOutputStream outStream) throws IOException {

// http header must be separated from body with empty line. User 2 * \n

		long start =System.currentTimeMillis();  
		
		String header = "HTTP/1.0 200 OK\n" + "Server: HTTP FTServer/0.1\n" + "Content-Length: "
				+ Files.size(targetPath) + "\n" + "Content-type: application/octet-stream;\n\n";

		outStream.write(header.getBytes()); // transform String to bytes

//			process Content

		File file = targetPath.toFile();

		FileInputStream fileInStream = null;

		fileInStream = new FileInputStream(file);

		byte[] buffer = new byte[8192];
		int length;

		while ((length = fileInStream.read(buffer)) > 0) {
			outStream.write(buffer, 0, length);
			System.out.println("Byte written : " + buffer.length);

		}

		fileInStream.close();
		
		long end =System.currentTimeMillis();  
		
		System.out.println(end - start);

	}
}
