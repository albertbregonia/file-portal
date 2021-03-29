# File Portal
## A Simple, Efficient, Peer-to-Peer File Transfer Tool

As cloud storage services have been popularized and the lack of internet privacy becoming an increasingly apparent threat, I designed this lightweight file transfer tool to aid against that.

<p align="center">
  <img title="Tool" src=https://github.com/albertbregonia/FilePortal/blob/master/src/main/resources/fp.png?raw=true></img>
</p>

**File Portal** is a simple, peer-to-peer, file transfer tool written in Java. It utilizes [JavaFX](https://openjfx.io/) for the GUI and the standard JavaIO and Java.Net APIs. It was designed to transfer files directly to a peer's computer as opposed to uploading to a cloud storage service and then having a peer download it.

# Requirements
- Understand the basic concepts of networking such as IP addresses and ports.
- Know how to port forward or have a peer that knows how to port forward.
- Understand that only 1 file can be sent at a time.
- You must agree that I am ***NOT*** liable for any damage to anyone's system or any illegal activity. <ins>**ONLY use this tool with those you personally know and TRUST!**</ins>

# Main Features
1. Configurable to be a server or client.
2. Easy to use GUI
3. Cross Platform - Able to run on Windows, Mac OS X and Linux as long as a [JRE 1.8+](https://www.java.com/en/) is available.
4. Lightweight - Roughly `14MB`
5. Solely dependent on you and your peer's network speeds

# Installation
Please check the [`target`](https://github.com/albertbregonia/File-Portal/tree/master/target) folder then download and double-click [`FilePortal-1.0.jar`](https://github.com/albertbregonia/File-Portal/blob/master/target/FilePortal-1.0.jar) to run.

***Note: If the jar does not run properly, also try running the jar file through command prompt/terminal with the following command:*** `java -jar <location of jar file>`

# How to Use
Decide between you and your peer who will be the `Host`. This person has to port forward their network on port `54000` unless both parties are on the same network.
- ### If you are sending the file:
  1. Click `Select` and locate the file to be sent.
      ###### If you are hosting:
        2. Click the `Host` checkbox next to the `IP Address` window.
        3. Click `Send` and wait for your peer to connect.
      ###### Otherwise:
        2. Type in your peer's [IP Address](https://www.whatsmyip.org/) excluding the port into the `IP Address` window.
        3. Click `Send` once your peer has completed their setup.
- ### If you are receiving the file:
  1. Click `Save` and select the desintation where the incoming file will be saved.
      ###### If you are hosting:
        2. Click the `Host` checkbox next to the `IP Address` window.
        3. Click `Receive` and wait for your peer to connect.
      ###### Otherwise:
        2. Type in your peer's [IP Address](https://www.whatsmyip.org/) excluding the port into the `IP Address` window.
        3. Click `Receive` once your peer has completed their setup.

# Personal Note
I mainly use this to quickly transfer files between my laptop and my desktop as it is much faster than using a Google Drive or a USB Drive. In my testing, **I am able to successfully transfer roughly 1GB in ~25 seconds on average.** I simply punch in my laptop's IPv4 and the file sends almost instantly.

Let me know if you find any bugs or have any suggestions to futher improve performance! Enjoy.
