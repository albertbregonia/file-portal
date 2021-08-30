package kanin.fileportal;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    
    public static Stage rootWindow; //static as opposed to using an accessor to prevent needing instances of 'Main'
    
    @Override //Main window GUI initialization
    public void start(Stage rootWindow) throws IOException { 
        Main.rootWindow = rootWindow; //Pass by reference, required for the file dialogs
        rootWindow.setScene(new Scene(FXMLLoader.load(
            Main.class.getClassLoader().getResource("main.fxml"))));
        rootWindow.setMinHeight(372);
        rootWindow.setMinWidth(706);
        rootWindow.setOnCloseRequest(e -> System.exit(0));
        rootWindow.setTitle("File Portal by Albert Bregonia");
        rootWindow.show();
    }

    public static void main(String[] args) { launch(args); }
    
}
