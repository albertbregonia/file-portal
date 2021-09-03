package kanin.fileportal;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.*;
import java.util.*;

import static kanin.fileportal.Main.*;

//Event Handler Class
public class Controller implements Initializable {
    
    @FXML
    private TextField ipInput, portInput, uploadPath, downloadPath;

    @FXML
    private VBox transferContainer; //parent element for 'transferList'
    public static final Accordion transferList = new Accordion(); //gui representation of all live 'FileTransferThread's

    @Override //Required to assign GUI components to variables
    public void initialize(URL url, ResourceBundle resourceBundle) { transferContainer.getChildren().addAll(transferList); }
    
    @FXML
    public void setFileToUpload() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose a File to Send");
        File file = chooser.showOpenDialog(rootWindow);
        if(file != null)
            uploadPath.setText(file.getAbsolutePath());
    }

    @FXML
    public void setSaveLocation() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Location to Save File");
        File folder = chooser.showDialog(rootWindow);
        if(folder != null)
            downloadPath.setText(folder.getAbsolutePath());
    }

    @FXML
    private CheckBox hosting; //'hosting' denotes that this client is port forwarded (if not transferring by LAN)
                              //and will wait for a peer before sending/receiving a file
    
    @FXML
    public void upload() { transfer(false); }

    @FXML
    public void download() { transfer(true); }

    private void transfer(boolean download) {
        //get user settings
        String ip = ipInput.getText().trim();
        int port = 54000; //default is 54000, otherwise the given
        try { 
            int input = Integer.parseInt(portInput.getText().trim());
            if(input > 1024 && input < 65536) //check for invalidity
                port = input;
        } catch(Exception ignored) {}

        //set up type of transfer
        File file = new File(uploadPath.getText().trim());
        boolean valid = !file.isDirectory();
        String[] fileErrorMsgs = {"Error: Desired File Unavailable.", "Please enter a valid and accessible file path"};
        String confirmMsg = String.format("Confirm outbound file transfer of '%s' on %s:%d", 
            file.getName(), hosting.isSelected() ? "localhost" : ip, port);
        if(download) {
            file = new File(downloadPath.getText().trim());
            valid = file.isDirectory();
            fileErrorMsgs = new String[] {"Error: Invalid Save Location.", "Please enter a valid working directory"};
            confirmMsg = String.format("Confirm inbound file transfer to '%s' on %s:%d", file.getName(), ip, port);
        }
        
        //execute transfer or display error
        if(file.exists() && valid) {
            if(alertMsg(confirmMsg, "Please press OK to confirm.", Alert.AlertType.CONFIRMATION))
                if(hosting.isSelected())
                    new FileTransferThread(file, port).start();
                else if(!ip.trim().isEmpty())
                    new FileTransferThread(file, port, ip).start();
                else
                    alertMsg(
                        "Error: Failed to establish connection", 
                        "Please enter a valid IP Address or check your settings.", 
                        Alert.AlertType.ERROR
                    );
        } else
            alertMsg(fileErrorMsgs[0], fileErrorMsgs[1], Alert.AlertType.ERROR);
    }
    
    //Dialog box - in some instances its used for error messaging, otherwise for confirmation
    public static boolean alertMsg(String content, String subtext, Alert.AlertType type) {
        Alert alert = new Alert(type, subtext, ButtonType.OK);
        alert.setTitle("File Portal by Albert Bregonia");
        alert.setHeaderText(content);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK; //If the OK button is pressed return true
    }
}
