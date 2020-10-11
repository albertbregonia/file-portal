package kanin.fileportal;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import static kanin.fileportal.Controller.*;

public class HostMode {
    
    private static Stage wait;

    public static void start(Object file, CheckBox hostMode){
        startup();
        transfer = new Transfer(wait,file,hostMode);
        transfer.start();
    }
    
    public static void startup(){
        //Creates a simple alert box to indicate that this host is waiting for a client to connect
        wait = new Stage();
        wait.initModality(Modality.APPLICATION_MODAL);
        Scene main = new Scene(new Pane());
        try {main = new Scene(FXMLLoader.load(Objects.requireNonNull(HostMode.class.getClassLoader().getResource("wait.fxml"))));}
        catch(IOException err){
            err.printStackTrace();
            System.exit(0);}
        wait.setOnCloseRequest(e->cancel());
        wait.setScene(main);
        wait.isAlwaysOnTop();
        wait.show();
    }

    //-----Cancellation-----//
    @FXML
    public void cancelConnect(){
        //Hide dialog window
        wait.close();
        cancel();
    }
    
    public static void cancel(){
        //Abort live transfer
        if(transfer.isAlive())
            transfer.interrupt();
        transfer.disconnect();
    }
}
