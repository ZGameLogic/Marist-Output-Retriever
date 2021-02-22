package merristOutputCatcher.FTP;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

/**
 * 
 * @author Ben Shabowski
 *
 */
public class ftp {

	final private String SERVER = "zos.kctr.marist.edu";
	final private int PORT = 21;

	private String username;
	private String password;

	private FTPClient ftp;

	public void open() throws IOException {

		// keyboard input
		Scanner kb = new Scanner(System.in);

		// look for username and password in userinfo.txt
		File userInfo = new File("userinfo.txt");

		if (userInfo.exists()) {
			System.out.println("Found user information");
			// Scan the file for the log on information
			Scanner input = new Scanner(userInfo);
			username = input.nextLine();
			password = input.nextLine();
			input.close();
		} else {
			System.out.println("Did not find user information.\n"
					+ "To skip this step in the future, make a file called userinfo.txt and put your KcID on the first line and password on the second line.");
			// Prompt user for log on information
			System.out.print("KcID:");
			username = kb.nextLine();
			System.out.print("Password:");
			password = kb.nextLine();
		}

		ftp = new FTPClient();

		// This next line can be un-commented for debug purposes
		// ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(new File("Debug.txt"))));

		System.out.print("Connecting to Merrist...");
		// connect to ftp server
		ftp.connect(SERVER, PORT);

		int reply = ftp.getReplyCode();

		if (!FTPReply.isPositiveCompletion(reply)) {
			// we get here if it doesnt connect properly 
			System.out.println("not connected");
			close();
			kb.close();
		}else {
			System.out.println("connected");

			System.out.print("Logging in...");

			if (ftp.login(username, password)) {
				// we get here if we login 
				System.out.println("logged in");
				System.out.println();
				// Change the site to look for jes files (the output files)
				ftp.site("filetype=jes");
				FTPFile[] files = ftp.listFiles();

				if (files.length > 0) {
					// we get here if there are output files
					for (int i = 0; i < files.length; i++) {
						System.out.println((i + 1) + " " + files[i].getName());
					}
					
					System.out.print("\nEnter the selection of the files you wish to download (space delimited):");

					String line = kb.nextLine();

					// go through each file selected by number, and attempt to retrieve the file
					for (String x : line.split(" ")) {
						try {
							// download the file
							ftp.retrieveFile(files[Integer.parseInt(x) - 1].getName(),
									new FileOutputStream(files[Integer.parseInt(x) - 1].getName() + ".txt"));
							System.out.println("Downloaded " + files[Integer.parseInt(x) - 1].getName());
							
							// TODO add output formatting
							
						} catch (NumberFormatException e) {
							System.out.println(x + " is not a valid number");
						} catch (ArrayIndexOutOfBoundsException e1) {
							System.out.println(x + " is not a valid number");
						}
					}
				} else {
					// we get here if there are no output files
					System.out.println("No job output found");
				}

				close();
			} else {
				// we get here if we dont login
				System.out.println("not logged in");
				System.out.println("Unable to login. Please check your username and password");
			}
		}

		// ending program
		System.out.println("Press enter to exit");
		kb.nextLine();
		kb.close();
	}

	void close() throws IOException {
		ftp.disconnect();
	}

}
