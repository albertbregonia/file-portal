package kanin.fileportal;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application 
{
    public static Stage mainWindow;
    @Override
    public void start(Stage mainWindow) throws IOException //Main window GUI initialization
    {
        Main.mainWindow = mainWindow; //Pass by reference required for the file dialogs
        mainWindow.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("main.fxml")))));
        mainWindow.setResizable(false);
        mainWindow.setOnCloseRequest(e->System.exit(0));
        mainWindow.setTitle("File Portal by Albert Bregonia");
        mainWindow.show();
    }

    public static void main(String[] args){launch(args);}
    
}
