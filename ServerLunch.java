package fileTransferServer;

public class ServerLunch {
	
	public static void main(String[] args) {
		Thread server = new Thread(new ServerFT(7170));
		server.start();
	}

}
