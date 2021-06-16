package com.jackbelmont;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Logger.enableConsoleLogging();
        //ChessBoard.runChessBoard();
        //ChessBoard cb = new ChessBoard(ChessBoard.GameMode.TWO_PLAYER);
        //cb.playOnCommandLine();

        ChessShell cs = new ChessShell();
        cs.test();

    }

}
