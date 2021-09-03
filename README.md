# File Portal
## A Simple, Efficient, Peer-to-Peer File Transfer Tool

<p align="center">
  <img title="Tool" src="/src/main/resources/fp2.png" height="500px"></img>
</p>

**File Portal** is a simple, peer-to-peer, file transfer tool written in Java. It utilizes [JavaFX](https://openjfx.io/) for the GUI and the standard `java.io` and `java.net` APIs. It was designed to transfer files directly to a peer's computer as opposed to uploading to a third party service and then having a peer download it.

# Main Features
1. Simple to use UI
2. Supports multiple concurrent file transfers to differing connections
3. File transfer manager; users are able to pause/cancel transfers *(right click a drop down tab)*

# Installation
*Before downloading, you must agree that I am **NOT** liable for any damage to anyone's system or any illegal activity. **<ins>ONLY use this tool with those you personally know and TRUST!</ins>***

Please check the [`target`](https://github.com/albertbregonia/File-Portal/tree/master/target) folder then download and double-click [`FilePortal-1.0.jar`](https://github.com/albertbregonia/File-Portal/blob/master/target/FilePortal-1.0.jar) to run.

# Requirements
- Java 8 Runtime or Higher
- ~14 MB of Disk Space
- *Optional:* Knowledge of Port Forwarding

# How to Use
Decide between you and your peer who will be the `Host`. This person must port forward their network on a port of their choice *(the default is `54000`)* unless both parties are on the same network.
- ### If you are sending the file:
  1. Click `Select` and locate the file to be sent.
      ###### If you are hosting:
        2. Click the `Host` checkbox next to the `Port` input field.
        3. Input your desired port in the `Port` input field.
        4. Click `Send` and wait for your peer to connect.
      ###### Otherwise:
        2. Type in your peer's [IP Address](https://www.whatsmyip.org/) excluding the port into the `IP Address` input field.
        3. Type in your peer's chosen port in the `Port` input field.
        4. Click `Send` once your peer has completed their setup.
- ### If you are receiving the file:
  1. Click `Save` and select the desintation where the incoming file will be saved.
      ###### If you are hosting:
        2. Click the `Host` checkbox next to the `Port` input field.
        3. Input your desired port in the `Port` input field.
        4. Click `Receive` and wait for your peer to connect.
      ###### Otherwise:
        2. Type in your peer's [IP Address](https://www.whatsmyip.org/) excluding the port into the `IP Address` input field.
        3. Type in your peer's chosen port in the `Port` input field.
        4. Click `Receive` once your peer has completed their setup.

# Personal Note
I mainly use this to quickly transfer files between my laptop and my desktop as it is much faster than using a Google Drive or a USB Drive. In my testing, **I am able to successfully transfer roughly 1GB in ~25 seconds on average.** I simply punch in my laptop's IPv4 and the file sends almost instantly.

Let me know if you find any bugs or have any suggestions to futher improve performance! Enjoy.

# Known Issues
- When one end cancels a transfer while the other user is paused, once they resume the transfer, the system will assume that the transfer was successful as the connection had been terminated. This is due to reading the end of the input stream being the same value as a disconnect. *Please merely delete the partial file* :)