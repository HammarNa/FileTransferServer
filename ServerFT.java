package fileTransferServer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 
 * @author Nabil.H
 *
 *         This Class handles http queries, create thread for each query.
 */
public class ServerFT implements Runnable {

	private ServerSocket server;

	/**
	 * 
	 * Constructor with a serverPort
	 * 
	 * @param serverPort
	 */
	public ServerFT(int serverPort) {

		try {
			this.server = new ServerSocket(serverPort);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * handle GET query and call the suitable method to process
	 * 
	 * @throws IOException
	 */
	public void queryProcess() throws IOException {
		Socket client = server.accept();
		BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
		DataOutputStream outStream = new DataOutputStream(client.getOutputStream());
//		
		String line = reader.readLine();

		String method = "";
		if (line != null) {
			method = line.substring(0, 3); // possible nullPointerExeption
		}

		if (method.equals("GET")) {
			String urlPath = line.substring(4, line.length() - 9); // remove non significant characters in http GET
																	// method
			Path wantedPath = Paths.get(urlPath);
			// call the suitable method
			if (Files.isDirectory(wantedPath)) {
				ProcessQuery.directoryProcess(wantedPath, outStream);
			} else if (Files.isReadable(wantedPath)) {
				ProcessQuery.fileProcess(wantedPath, outStream);
			} else {
				ProcessQuery.errorProcess(outStream);
			}

		}

		
		outStream.flush();
		outStream.close();
		reader.close();
		client.close();

	}

	public void clientListner() throws IOException {

		while (true) {
			queryProcess();
		}
	}

	@Override
	public void run() {
		try {

			clientListner();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
