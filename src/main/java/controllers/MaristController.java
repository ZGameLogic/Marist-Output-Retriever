package controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import data.DataCacher;
import data.Job;
import data.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller class for main application
 * 
 * @author Ben Shabowski
 * @since 1.0
 *
 */
public class MaristController {

	// Server address
	final private static String SERVER = "zos.kctr.marist.edu";
	// Server port
	final private static int PORT = 21;
	// FTP file connection
	private FTPClient ftp;

	private boolean loggedIn;

	@FXML
	private ListView<Job> jobsList;

	@FXML
	private VBox logVBox;

	@FXML
	private TextField idTF;

	@FXML
	private PasswordField passwordF;

	@FXML
	private CheckBox saveCB;

	@FXML
	private Label statusLabel;

	/**
	 * Login to Marist
	 * 
	 * @param event
	 */
	@FXML
	void login(ActionEvent event) {
		new Thread(() -> {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					if (saveCB.isSelected()) {
						User user = new User(idTF.getText(), passwordF.getText());
						File userFile = new File("userinfo");
						DataCacher.saveSerialized(user, userFile);
					}

					// FTP Client
					ftp = new FTPClient();

					// Connect to the server
					try {
						ftp.connect(SERVER, PORT);
					} catch (IOException e) {
						log("Unable to connect to marist");
						statusLabel.setText("Cannot connect to Marist");
						return;
					}

					ftp.enterLocalPassiveMode();

					// Login to the server
					try {
						if (ftp.login(idTF.getText(), passwordF.getText())) {
							log("Logged into the server");
							statusLabel.setText("Logged in");
							loggedIn = true;
							refreshJobs(null);
						} else {
							log("Unable to login to marist.");
							log("Please check your username and password");
							statusLabel.setText("Invalid username or password");
						}
					} catch (IOException e) {
						log("Unable to login to marist.");
						statusLabel.setText("Cannot connect to Marist");
						return;
					}
				}
			});
		}).start();
	}

	/**
	 * Method that gets run after JavaFX starts
	 */
	@FXML
	public void initialize() {
		loggedIn = false;
		// Initial logging
		log("MARIST JOB OUTPUT RETRIEVER\tby Ben Shabowski");

		File userFile = new File("userinfo");
		// Test if a username is already made
		if (userFile.exists()) {
			saveCB.setSelected(true);
			try {
				User user = (User) DataCacher.loadSerialized(userFile);
				idTF.setText(user.getUsername());
				passwordF.setText(user.getPassword());
				login(null);
			} catch (ClassNotFoundException e) {
				log("Unable to read username and password file.");
				log("Please delete the userinfo file to re-enable auto sign in");
			} catch (IOException e1) {

			}
		}
	}

	/**
	 * Refresh the job list from Marist
	 * 
	 * @param event
	 */
	@FXML
	void refreshJobs(ActionEvent event) {
		if (loggedIn) {
			log("Refreshing jobs");
			jobsList.getItems().clear();
			// Change the site to look for jes files (the output files)
			try {
				ftp.site("filetype=jes");

				// Retrieve the list of files
				FTPFile[] files = ftp.listFiles();

				if (files.length > 0) {
					// we get here if there are output files
					for (int i = 0; i < files.length; i++) {
						jobsList.getItems().add(new Job(files[i].getName()));
					}
				} else {
					jobsList.getItems().add(new Job("NO JOBS IN THE OUTPUT QUEUE"));
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			log("Cannot refresh jobs: You are not logged in!");
		}
	}

	/**
	 * purge all jobs from Marist
	 * 
	 * @param event
	 */
	@FXML
	void deleteAllJobs(ActionEvent event) {
		if (loggedIn) {
			try {
				FTPFile[] files = ftp.listFiles();
				for (int i = 0; i < files.length; i++) {
					ftp.deleteFile(files[i].getName());
				}
				log("Deleted all jobs");
				refreshJobs(null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			log("Cannot delete jobs: You are not logged in!");
		}
	}

	/**
	 * Download and format selected job from Marist
	 * 
	 * @param event
	 */
	@FXML
	void download(ActionEvent event) {
		if (loggedIn) {
			if (jobsList.getSelectionModel().getSelectedItem() != null && !jobsList.getSelectionModel()
					.getSelectedItem().getJobName().equals("NO JOBS IN THE OUTPUT QUEUE")) {
				String fileName = jobsList.getSelectionModel().getSelectedItem().getJobName();
				// download the file
				try {
					FileOutputStream out = new FileOutputStream(fileName + ".txt");
					ftp.retrieveFile(fileName, out);
					out.close();
					// File that needs to be formatted
					File toFormatt = new File(fileName + ".txt");

					String formattedFile = "";

					Scanner fileInput = new Scanner(toFormatt);

					String inputLine;

					// format the file
					while (fileInput.hasNextLine() && (inputLine = fileInput.nextLine()) != null) {
						if (inputLine.startsWith("0")) {
							formattedFile += "\n" + inputLine.replaceFirst("0", "") + "\n";
						} else if (inputLine.startsWith("1")) {
							formattedFile += "\f\n" + inputLine.replaceFirst("1", "") + "\n";
						} else if (inputLine.startsWith("-")) {
							formattedFile += "\n\n" + inputLine.replaceFirst("-", "") + "\n";
						} else if (inputLine.startsWith(" ")) {
							formattedFile += inputLine.replaceFirst(" ", "") + "\n";
						} else {
							formattedFile += inputLine + "\n";
						}
					}

					fileInput.close();

					// save formatted file
					PrintWriter output = new PrintWriter(toFormatt);
					output.print(formattedFile);
					output.flush();
					output.close();

					log("File has been downloaded and saved to:\n" + toFormatt.getAbsolutePath());

					// Let the user know its done with alert
					Stage alertStage = new Stage();
					FXMLLoader fxmlLoader = new FXMLLoader();
					AnchorPane newPane = fxmlLoader
							.load(getClass().getClassLoader().getResource("fxml/OK.fxml").openStream());
					CompletedController controller = fxmlLoader.getController();
					controller.setStage(alertStage);
					alertStage.setTitle("Alert");
					Scene newScene = new Scene(newPane);
					alertStage.setResizable(false);
					alertStage.setScene(newScene);
					alertStage.show();

				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				log("You must click on a job before you try to download and format it");
			}
		} else {
			log("Cannot download job: You are not logged in!");
		}
	}

	private void log(String message) {
		logVBox.getChildren().add(new Label(message));
	}
}
