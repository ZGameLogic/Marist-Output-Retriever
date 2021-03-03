package maristOutputCatcher.FTP;

import java.io.Console;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
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
		
		System.out.println("Starting Marist job downloader\nBy Ben Shabowski\n");

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
			
			Console console = System.console();
			
			if(console == null) {
				System.out.println("No console instance");
			}else {
				console.printf("Password:");
				char[] passwordArray = console.readPassword();
				password = new String(passwordArray);
			}
		}

		ftp = new FTPClient();

		// This next line can be un-commented for debug purposes
		// ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(new File("Debug.txt"))));

		System.out.print("Connecting to Marist...");
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
			
			ftp.enterLocalPassiveMode();

			if (ftp.login(username, password)) {
				// we get here if we login 
				System.out.println("logged in");
				// retrieving 
				System.out.print("Retrieving jobs...");
				
				// Change the site to look for jes files (the output files)
				ftp.site("filetype=jes");
				
				// Retrieve the list of files
				FTPFile[] files = ftp.listFiles();
				
				System.out.println("retrieved");
				System.out.println();

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
							
							System.out.print("Formatting...");
							// File that needs to be formatted
							File toFormatt = new File(files[Integer.parseInt(x) - 1].getName() + ".txt");
							
							String formattedFile = "";
							
							Scanner fileInput = new Scanner(toFormatt);
							
							String inputLine;
							
							while(fileInput.hasNextLine() && (inputLine = fileInput.nextLine()) != null){
								if(inputLine.startsWith("0")) {
									formattedFile += "\n " + inputLine.replaceFirst("0", "") + "\n";
								}else if(inputLine.startsWith("1")) {
									formattedFile += "\f\n " + inputLine.replaceFirst("1", "") + "\n";
								}else if(inputLine.startsWith("-")) {
									formattedFile += "\n\n " + inputLine.replaceFirst("-", "") + "\n";
								}else {
									formattedFile += inputLine + "\n";
								}
							}
							
							fileInput.close();
							
							PrintWriter output = new PrintWriter(toFormatt);
							output.print(formattedFile);
							output.flush();
							output.close();
							
							System.out.println("Formatted");
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
		System.out.println("Remember to check the output documents before submitting!");
		System.out.println("Press enter to exit");
		kb.nextLine();
		kb.close();
	}

	void close() throws IOException {
		ftp.disconnect();
	}

}
