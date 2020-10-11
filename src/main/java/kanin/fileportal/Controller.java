package kanin.fileportal;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.*;
import java.util.*;

import static kanin.fileportal.Main.*;

//Event Handler Class
public class Controller implements Initializable {
    
    @Override //Required to assign GUI components to variables
    public void initialize(URL url, ResourceBundle resourceBundle){}
    
    @FXML
    private TextField ip,fileDir,saveLocation;
    
    @FXML
    public void setFile(){
        FileChooser choose = new FileChooser();
        choose.setTitle("Choose a File to Send");
        File sendFile = choose.showOpenDialog(mainWindow);
        if(sendFile!=null)
            fileDir.setText(sendFile.getAbsolutePath());
    }

    @FXML
    public void saveFile(){
        DirectoryChooser choose = new DirectoryChooser();
        choose.setTitle("Location to Save File");
        File receiveFile = choose.showDialog(mainWindow);
        if(receiveFile!=null)
            saveLocation.setText(receiveFile.getAbsolutePath());
    }

    @FXML
    //'Host Mode' denotes that this client is port forwarded 
    // and will wait for a peer before sending/receiving a file
    private CheckBox hostMode;
    public static Transfer transfer;
    
    @FXML
    public void send() throws IOException{
        //If the selected file is a valid file, *not a directory*,
        //and the corresponding peer is accessible, send the file
        File sendFile = new File(fileDir.getText().trim());
        if(sendFile.exists() && !sendFile.isDirectory()){
            if(alertMsg("Confirm outbound transfer of: \""+sendFile.getName()+"\"?","Please press OK to confirm.", Alert.AlertType.CONFIRMATION))
                if(hostMode.isSelected())
                    HostMode.start(sendFile,hostMode);
                else if(ip.getText().trim().length()>1 && InetAddress.getByName(ip.getText().trim()).isReachable(10000)){
                    //File transfer thread is saved to a variable in order to allow for cancellation
                    transfer = new Transfer(ip.getText().trim(),sendFile,hostMode);
                    transfer.start();}
                else
                    alertMsg("Error: Failed to establish connection","Please enter a valid IP Address or check your settings.", Alert.AlertType.ERROR);}
        else
            alertMsg("Error: Desired file could not be accessed","Please enter a valid file/file location.", Alert.AlertType.ERROR);
    }
    
    @FXML
    public void recv() throws IOException{
        //Ensure that the save directory is a valid location
        File saveDir = new File(saveLocation.getText().trim());
        if(saveDir.exists() && saveDir.isDirectory()){
            if(alertMsg("Confirm inbound file transfer?","Please press OK to confirm.", Alert.AlertType.CONFIRMATION))
                if(hostMode.isSelected())
                    HostMode.start(saveLocation.getText().trim(),hostMode);
                else if(ip.getText().trim().length()>1 && InetAddress.getByName(ip.getText().trim()).isReachable(10000)){
                    transfer = new Transfer(ip.getText().trim(),saveLocation.getText().trim(),hostMode);
                    transfer.start();}
                else
                    alertMsg("Error: Failed to establish connection","Please enter a valid IP Address or check your settings.", Alert.AlertType.ERROR);}
        else
            alertMsg("Error: Invalid Save Location.","Please enter a valid working directory", Alert.AlertType.ERROR);
    }
    
    //Dialog box - in some instances its used for error messaging, otherwise for confirmation
    public static boolean alertMsg(String content, String subtext, Alert.AlertType type){
        Alert alert = new Alert(type,subtext,ButtonType.OK);
        alert.setTitle("File Portal by Albert Bregonia");
        alert.setHeaderText(content);
        Optional<ButtonType> result = alert.showAndWait();
        //If the OK button is pressed return true
        return result.filter(bt->bt==ButtonType.OK).isPresent();
    }
    
}
