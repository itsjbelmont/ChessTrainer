package com.jackbelmont;

import javax.swing.*;
import java.awt.image.BufferedImage;

import static java.lang.Math.abs;

public class Knight extends ChessPiece {

    Knight(PieceColor color, char colPosition, char rowPosition) {
        super(color, colPosition, rowPosition, PieceType.KNIGHT);

        //TODO: Add pawn icons - transparent image for now
        this.icon = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
    }

    @Override
    public Boolean canMoveTo(Character file, Character rank, ChessBoard board) {
        String funcStr = this.type + "::canMoveTo(): ";
        String destString = file.toString() + rank.toString();
        String thisStr = this.color + " " + this.type + " at " + this.file + this.rank;

        // Sanitize input
        if (rank < '1' || rank > '8') {
            Logger.logStr(funcStr + "FAIL: Invalid rank passed: " + rank);
            return false;
        } else if (file < 'a' || file > 'h') {
            Logger.logStr(funcStr + "FAIL: Invalid file passed: " + file);
            return false;
        } else if (board == null) {
            Logger.logStr(funcStr + "FAIL: ChessBoard passed is null!");
            return false;
        }

        ChessPiece destination = board.getPieceAtPosition(file, rank);
        if (destination != null && destination.color == this.color) {
            Logger.logStr(funcStr + "FAIL: " + thisStr + " can not move to a square occupied by friendly piece at " + destString);
            return false;
        }

        final Character curFile = this.file;
        final Character curRank = this.rank;

        if ((abs(file-curFile) == 2 && abs(rank-curRank) == 1) || (abs(file-curFile) == 1 && abs(rank-curRank) == 2)) {
            Logger.logStr(funcStr + "SUCCESS: " + thisStr + " can move to " + destString);
            return true;
        } else {
            Logger.logStr(funcStr + "FAIL: " + thisStr + " can only move in an L shape");
            return false;
        }
    }

    @Override
    public Boolean canCaptureAt(Character file, Character rank, ChessBoard board) {
        String funcStr = this.type + "::canCaptureAt(): ";
        String destString = file.toString() + rank.toString();
        String thisStr = this.color + " " + this.type + " at " + this.file + this.rank;

        // Can capture on any square that it can move to and contains an enemy piece
        ChessPiece dest = board.getPieceAtPosition(file, rank);
        if (this.canMoveTo(file, rank, board) && dest != null && dest.color != this.color) {
            Logger.logStr(funcStr + "SUCCESS: " + thisStr + " can capture at " + destString);
            return true;
        } else {
            Logger.logStr(funcStr + "FAIL: " + thisStr + " can not capture at " + destString);
            return false;
        }
    }

    @Override
    public Boolean controlsSquare(Character file, Character rank, ChessBoard board) {
        String funcStr = this.type + "::controlsSquare(): ";
        String destString = file.toString() + rank.toString();
        String thisStr = this.color + " " + this.type + " at " + this.file + this.rank;

        // Sanitize input
        if (rank < '1' || rank > '8') {
            Logger.logStr(funcStr + "FAIL: Invalid rank passed: " + rank);
            return false;
        } else if (file < 'a' || file > 'h') {
            Logger.logStr(funcStr + "FAIL: Invalid file passed: " + file);
            return false;
        } else if (board == null) {
            Logger.logStr(funcStr + "FAIL: ChessBoard passed is null!");
            return false;
        }

        // It doesnt matter if a friendly piece, enemy piece, or no piece is on the square for it to be controlled

        final Character curFile = this.file;
        final Character curRank = this.rank;

        if ((abs(file-curFile) == 2 && abs(rank-curRank) == 1) || (abs(file-curFile) == 1 && abs(rank-curRank) == 2)) {
            Logger.logStr(funcStr + "SUCCESS: " + thisStr + " controls square " + destString);
            return true;
        } else {
            Logger.logStr(funcStr + "FAIL: " + thisStr + " can only control in an L shape");
            return false;
        }
    }
}
