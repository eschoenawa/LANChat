package de.eschoenawa.lanchat.ui;

import javax.swing.*;
import java.util.List;

public class DarkUserInterface extends JFrame implements UserInterface {
    @Override
    public void setCallback(UserInterfaceCallback callback) {

    }

    @Override
    public void setUserList(List<String> users) {
    }

    @Override
    public void clearUserList() {

    }

    @Override
    public void addDiscoveredUser(String user) {

    }

    @Override
    public void clearHistory() {

    }

    @Override
    public void receiveMessage(String sender, String message, boolean shouted) {

    }

    @Override
    public boolean isOpened() {
        return false;
    }

    @Override
    public void open() {

    }

    @Override
    public void minimize() {

    }
}
