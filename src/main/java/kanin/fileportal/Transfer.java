package kanin.fileportal;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Transfer extends Thread{

    //-----Networking-----//
    private static Socket client;
    private static ServerSocket host;
    
    //-----Parameters-----//
    
    //Outgoing
    private String validIP; 
    private File sendFile;

    //Incoming
    private String dir; 
    private Stage wait;

    private final CheckBox hostMode;
    
    //-----Display Progress-----//
    private final ProgressBar bar = new ProgressBar(0);
    private final Label progress = new Label();
    private final Stage fileStatus = new Stage();
    
    //OS Directory delimiter format
    private char dlm;
    
    //I understand that I can simply overload the constructor, but that's very verbose
    public Transfer(Object type, Object file, CheckBox hostMode){
        if(type instanceof String) //If host mode is disabled, 'type' is an IP address
            this.validIP=(String)type;
        else if(type instanceof Stage) //If host mode is enabled, 'type' is a wait dialog
            this.wait=(Stage)type;
        if(file instanceof File) //If sending, 'file' will be an actual file
            this.sendFile=(File)file;
        else if(file instanceof String)//If receiving, 'file' will be a directory
            this.dir=(String)file;
        this.hostMode=hostMode;
        //OS Detection to format to file directory structure
        dlm = System.getProperty("os.name").toLowerCase().contains("windows") ? '\\' : '/';
    }

    @Override
    public void run(){
        try{ //Create server and wait for client
            if(hostMode.isSelected()){ 
                host = new ServerSocket(54000);
                client = host.accept();
                if(sendFile!=null)
                    Platform.runLater(()->{wait.close(); 
                    showProgress("Sending ["+sendFile.getName()+"]");
                });}
            else{ //Connect to server
                client = new Socket(validIP,54000);
                if(sendFile!=null)
                    Platform.runLater(()->showProgress("Sending ["+sendFile.getName()+"]"));}
            if(client!=null) //Initiate Transfer
                if(sendFile!=null)
                    outgoing();
                else if(dir!=null)
                    incoming();
        }catch(IOException e){e.printStackTrace(); Platform.runLater(fileStatus::close); disconnect();}
    }

    public void outgoing() throws IOException{ //Sends the name and size of the file before the file contents
        PrintWriter send = new PrintWriter(client.getOutputStream(),true);
        send.println(sendFile.getName()+(char)127+sendFile.length());
        transfer(new BufferedInputStream(new FileInputStream(sendFile)),new BufferedOutputStream(client.getOutputStream()), sendFile.length());
    }
  
    public void incoming(){
        File data = null;
        try{
            BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String[] info = input.readLine().split(""+(char)127); //Reads in file name
             data = new File(dir+dlm+info[0]);
            for(int i=0; data.exists(); i++) //Adjusts the file name if a file of the same name in the same directory exists
                data = new File(dir+dlm+info[0].substring(0,info[0].lastIndexOf("."))+i+info[0].substring(info[0].indexOf(".")));
            Platform.runLater(()->{
                if(wait!=null) wait.close();
                showProgress("Receiving ["+info[0]+"]");});
            transfer(new BufferedInputStream(client.getInputStream()),new BufferedOutputStream(new FileOutputStream(data)),Long.parseLong(info[1])); //Last parameter is file size
        }catch(IOException e){if(data!=null) data.delete();} //If the file transfer fails, the partially generated file is deleted
    }
    
    public void transfer(BufferedInputStream in, BufferedOutputStream out, long size) throws IOException{
        try{ //File Transfer - Either incoming or outgoing
            long start = System.currentTimeMillis();
            int amt; //length of read bytes
            double total=0; //total # of read bytes
            byte[] file = new byte[10*1024*1024]; //maximum of 10MB is read per iteration
            while((amt=in.read(file))>0){
                out.write(file,0,amt);
                out.flush();
                System.out.println("Transferring ["+amt+"] bytes...");
                final double progress = (total+=amt)/size;
                Platform.runLater(()->bar.setProgress(progress)); //Display progress
            }
            Platform.runLater(()->{
                fileStatus.close(); //If the file transfer was successful, display the elapsed time
                Controller.alertMsg("File successfully transferred.","Elapsed time: "+(System.currentTimeMillis()-start)/1000+" seconds", Alert.AlertType.INFORMATION);
            });}
        finally{ //Clean disconnect regardless of success
            in.close();
            out.close();
            disconnect();
        }
    }

    //Connections are severed for both parties upon successful/unsuccessful file transfer
    public void disconnect(){
        try{
            if(host!=null)
                host.close();
            if(client!=null)
                client.close();
        }catch(IOException e){e.printStackTrace();}
        finally{
            host=null;
            client=null;
        }
    }
    
    //Displays the progress of the file transfer based on read bytes, allows cancellation
    public void showProgress(String s){
        fileStatus.initModality(Modality.APPLICATION_MODAL);
        VBox layout = new VBox();
        layout.setPrefSize(225,105);
        layout.setMaxSize(225,105);
        progress.setText(s+"...");
        progress.setLayoutX(165);
        progress.setLayoutY(55);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(progress,bar);
        layout.getStylesheets().add("main.css");
        fileStatus.setScene(new Scene(layout));
        fileStatus.setOnCloseRequest(e->{
            fileStatus.close();
            if(this.isAlive())
                this.interrupt();
            disconnect();
        });
        fileStatus.setResizable(false);
        fileStatus.isAlwaysOnTop();
        fileStatus.show();
    }
    
}

