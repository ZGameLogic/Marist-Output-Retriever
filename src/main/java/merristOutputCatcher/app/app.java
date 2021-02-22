package merristOutputCatcher.app;

import java.io.IOException;

import merristOutputCatcher.FTP.ftp;

public class app {
	
	public static void main(String[] args) {
		
		ftp FTPConnection = new ftp();
		
		try {
			// open the connection
			FTPConnection.open();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
