package kanin.fileportal;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static kanin.fileportal.Controller.alertMsg;

public class FileTransferThread extends Thread {

    //-----Networking-----//
    private Socket client;
    private ServerSocket host;
    private final File transferFile; //will be directory if inbound
    private final int port;
    private boolean pauseFlag = false;
    
    //-----Parameters-----//
    
    private String ip; //outbound
    private File inboundFile; //inbound
    
    //-----Display Progress-----//
    private final ProgressBar bar = new ProgressBar(0);
    private final TitledPane infoCard = new TitledPane();
    private final Label status = new Label("Status: Created");

    /**
     * Constructor for a FileTransferThread where the user is hosting
     * @param file
     * The directory/file to be saved/sent
     * @param port
     * The port to be used to conduct the transfer; used by the ServerSocket
     */
    public FileTransferThread(File file, int port) {
        this.transferFile = file;
        this.port = port;

        //TitlePane gui representation of this thread
        this.infoCard.getStyleClass().add("transferThread");
        VBox container = new VBox();
        bar.prefWidthProperty().bind(container.widthProperty());
        container.getChildren().addAll(
            this.status,
            new Label(String.format("Path: '%s'", file.getAbsolutePath())),
            this.bar
        );

        //right click menu to control transfer thread
        ContextMenu menu = new ContextMenu();
        MenuItem pause = new MenuItem("Pause"),
                 cancel = new MenuItem("Cancel");
        pause.setOnAction(e -> { //toggle pause
            if(this.client != null) {
                if(pause.getText().equalsIgnoreCase("Pause")) {
                    this.pauseFlag = true;
                    pause.setText("Resume");
                    if(inboundFile == null)
                        statusUpdate(String.format("[PAUSED] Sending '%s'", transferFile.getName()));
                    else
                        statusUpdate(String.format("[PAUSED] Receiving '%s'", inboundFile.getName()));
                } else {
                    this.pauseFlag = false;
                    pause.setText("Pause");
                    if(inboundFile == null)
                        statusUpdate(String.format("Sending '%s'", transferFile.getName()));
                    else
                        statusUpdate(String.format("Receiving '%s'", inboundFile.getName()));
                }
            }
        });
        cancel.setOnAction(e -> {
            Controller.transferList.getPanes().remove(this.infoCard);
            if(this.isAlive())
                this.interrupt();
            disconnect();
        });
        menu.getItems().addAll(pause, cancel);
        this.infoCard.setContextMenu(menu);
        this.infoCard.setContent(container);
    }

    /**
     * Constructor for a FileTransferThread where the user is not hosting
     * @param file
     * The directory/file to be saved/sent
     * @param port
     * The port to be used to conduct the transfer; used by the Socket
     * @param ip
     * The IP of the peer to connect to
     */
    public FileTransferThread(File file, int port, String ip) {
        this(file, port);
        this.ip = ip;
    }

    @Override
    public void run() {
        try {
            //initialize connections
            if(this.ip == null) { //Create server and wait for client, ip won't be passed if hosting
                this.host = new ServerSocket(this.port);
                Platform.runLater(() -> {
                    this.infoCard.setText("Host Connection @localhost:" + this.port);
                    statusUpdate("Waiting...");
                    Controller.transferList.getPanes().add(this.infoCard);
                });
                this.client = this.host.accept();
            } else { //Connect to server
                this.client = new Socket(this.ip, this.port);
                Platform.runLater(() -> {
                    this.infoCard.setText(String.format("Remote Connection @%s:%d", this.ip, this.port));
                    statusUpdate("Connected");
                    Controller.transferList.getPanes().add(this.infoCard);
                });
            }
            //transfer file
            long start = System.currentTimeMillis();
            if(this.transferFile.isDirectory())
                incomingTransfer();
            else
                outgoingTransfer();
            //notify user
            Platform.runLater(() -> {
                String fileName = transferFile.getName(),
                       subtext = "Elapsed time: " + (System.currentTimeMillis() - start) / 1000 + " seconds";
                if(inboundFile != null) {
                    fileName = inboundFile.getName();
                    subtext += String.format("\nSee '%s'", inboundFile.getAbsolutePath());
                }
                Controller.transferList.getPanes().remove(this.infoCard); //If the file transfer was successful, remove from 'transferList'
                alertMsg(fileName + " successfully transferred.", subtext, Alert.AlertType.INFORMATION); //display the elapsed time
            });
        } catch(IOException e) {
            e.printStackTrace();
            if(this.inboundFile != null) //If the file transfer fails, the partially generated file is deleted
                this.inboundFile.delete();
            if(!e.getMessage().equalsIgnoreCase("Socket closed") && !e.getMessage().equalsIgnoreCase("Connection reset by peer"))
                Platform.runLater(() -> 
                    alertMsg(
                        "Error: " + e.getMessage(), 
                        "Please enter a valid IP Address or check your settings.", 
                        Alert.AlertType.ERROR
                    ));
        } finally {
            disconnect();
            Platform.runLater(() -> Controller.transferList.getPanes().remove(this.infoCard));
        }
    }

    private void statusUpdate(String s) { Platform.runLater(() -> this.status.setText("Status: " + s)); }

    private void outgoingTransfer() throws IOException {
        //Sends the name and size of the file before the file contents
        PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
        writer.println(transferFile.getName() + (char)28 + transferFile.length());
        statusUpdate(String.format("Sending '%s'", transferFile.getName()));
        transfer(
            new BufferedInputStream(new FileInputStream(transferFile)),
            new BufferedOutputStream(client.getOutputStream()),
            transferFile.length()
        );
    }
  
    private void incomingTransfer() throws IOException {
        //incoming file setup
        String directory = transferFile.getAbsolutePath();
        BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String[] info = input.readLine().split("" + (char)28); //Reads in file name/size
        this.inboundFile = new File(directory + "/" + info[0]);
        int extensionIndex = info[0].lastIndexOf(".");
        if(extensionIndex < 0) //if the file has no extension
            extensionIndex = info[0].length();
        for(int i=0; !this.inboundFile.createNewFile(); i++) //Adjusts the file name if a file of the same name in the same directory exists
            this.inboundFile = 
                new File(String.format("%s/%s%d%s", directory, info[0].substring(0, extensionIndex), i, info[0].substring(extensionIndex)));
        statusUpdate(String.format("[CONFIRM] Receiving '%s'", inboundFile.getName()));
        this.pauseFlag = true; //by default incoming file transfers are paused as a pseudo confirm
        this.infoCard.getContextMenu().getItems().get(0).setText("Confirm");
        transfer(
            new BufferedInputStream(this.client.getInputStream()),
            new BufferedOutputStream(new FileOutputStream(this.inboundFile)),
            Long.parseLong(info[1]) //file size
        );
    }
    
    private void transfer(BufferedInputStream in, BufferedOutputStream out, long size) throws IOException {
        try {
            int amt; //length of read bytes
            double total = 0; //total # of read bytes
            byte[] bin = new byte[10 * 1024 * 1024]; //maximum of 10MB is read per iteration
            while(true) //if we pause just have this thread idle
                synchronized(this) {
                    if(!pauseFlag) //pause flag is used because pausing this thread would also pause the connection
                        if((amt = in.read(bin)) > 0) {
                            out.write(bin, 0, amt);
                            out.flush();
                            System.out.printf("Transferring %d bytes\n", amt);
                            final double progress = (total += amt) / size;
                            Platform.runLater(() -> bar.setProgress(progress)); //Display progress
                        } else
                            break;
                }
        } catch(IOException e) { throw e; } //pass error back up the call stack 
        finally {
            in.close();
            out.close();
        }
    }

    //Connections are severed for both parties upon successful/unsuccessful file transfer
    public void disconnect() {
        try {
            if(this.client != null)
                this.client.close();
            if(this.host != null)
                this.host.close();
            if(this.host != null || this.client != null)
                System.out.println("Connections terminated.");
        } catch(IOException e) { e.printStackTrace(); }
        finally {
            this.client = null;
            this.host = null;
        }
    }
}

