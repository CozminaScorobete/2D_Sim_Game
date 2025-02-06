package main;

import javax.swing.*;
import sockets.GameClient;
import sockets.GameServer;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TitleScreen());
    }
}
