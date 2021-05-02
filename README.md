
# LANChat

![LANChat Screenshot](/lanchat_screenshot.png)

This is a very small application I threw together for communicating inside LANs using UDP Broadcast messages (I use it as a local chat for communicating with my roommates). Please note that even though LANChat is written in Java it will *only* work on Windows 10 (for now). Since this is a freetime project of mine, I won't ensure full functionality. Use at your own risk!

**IMPORTANT INFO:** If you have a network or system that has to be secure in any form I do not recommend using this program as it can be modified remotely and uses no encryption!

*This program broadcasts messages through the entire network. Every participant of any network connected to your PC will be able to read the messages you send with LANChat. This is not a program to hold private conversations with, use any chat client with encryption instead (there are hundreds to choose from).*

## Usage
LANChat resides in your task bar where other background programs / services are displayed. By clicking on the LANChat icon you open the interface for communication.

You can type into the bottom textbox and send something by pressing `Enter`.

On the top you can select the `Online`-tab to see who is currently in your network. Only these clients will receive anything you send. Any client joining after you sent something will not be able to see your message. This means clients can have different chat histories depending on when they were online.

By holding down `Ctrl`+`Shift`+`Alt` while having the textbox in the `Chat`-tab selected you can access the shout dialog to send a message that will be highlighted in red to other users and will receive a more prominent notification.

LANChat supports sending and receiving links and will try to highlight them and make them clickable, if possible.

Right-clicking on the LANChat icon gives you access to a context menu mainly for accessing the settings. There you can set your username and configure LANChat to your liking.

If you want LANChat to launch with Windows boot you can change the corresponding setting in the settings window.

## Updating LANChat
LANChat comes with a neat updating functionality to allow one (or multiple) clients in a network to provide update links to the other clients. This means at least one person in the network has to update manually and provide a download link for the new .jar file, and all the other clients can use that link to automatically update their client.

To provide an update link to clients in your network upload your current LANChat.jar to a file hosting provider of your choice (or host it yourself somewhere the other clients can connect to). You can then add a file called `UPDATE` into the same directory as LANChat. Into that file you can then add the link to the hosted .jar file. Make sure that the file only contains the link and no newlines.

When you update to the next version make sure to remove or update the `UPDATE` file **before** updating or you will cause other clients to land in an infinite update loop. I plan on fixing this in the future but for now this is sadly something you have to watch out for yourself.

Please note that the auto-update can be a huge security risk because it allows any participant in your network to run any Java code on any machine running LANChat with the auto-update feature enabled! Use only with extreme caution in networks you trust. To be safe you should disable the auto-update feature in the settings if you don't use it. *Use at your own risk! You have been warned!*

## Plugin development for LANChat

LANChat will soon support Plugins again. After a rework for version 2.0.0 the whole concept had to be scrapped and will be overhauled to allow for even more awesome possibilities with plugins. Until then please be patient ;).