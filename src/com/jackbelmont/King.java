package com.jackbelmont;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.util.ArrayList;

import static java.lang.Math.abs;

public class King extends ChessPiece {
    King(ChessPiece.PieceColor color, char colPosition, char rowPosition) {
        super(color, colPosition, rowPosition, PieceType.KING);

        //TODO: Add pawn icons - transparent image for now
        this.icon = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
    }

    /*
        Validate a king move
    */
    @Override
    public boolean validateMove (ChessPiece[][] chessPieces, String move) {

        return false; //failure
    }

    /*
        public abstract void refreshPossibleMoves()
            - refreshes the possibleMoves[8][8] array based on the current position
            - WARNING: Does not take into account the other pieces on the board
    */
    @Override
    public void refreshPossibleMoves(ChessPiece[][] chessBoard) {
        Character files[] = ChessBoard.files;
        Character ranks[] = ChessBoard.ranks;
        int curFile = ChessBoard.getFileIdx(this.file);
        int curRank = ChessBoard.getRankIdx(this.rank);

        // When refreshing the moves they need to all be nullified first
        for ( Character file : files ) {
            for (Character rank : ranks) {
                int fileIdx = ChessBoard.getFileIdx(file);
                int rankIdx = ChessBoard.getRankIdx(rank);
                possibleMoves[rankIdx][fileIdx] = null;
            }
        }

        for ( Character file : files ) {
            for ( Character rank : ranks ) {
                int fileIdx = ChessBoard.getFileIdx(file);
                int rankIdx = ChessBoard.getRankIdx(rank);
                if (abs(fileIdx - curFile) == 0 && abs(rankIdx - curRank) == 0) {
                    /* Skip square king is currently on */
                    continue;
                }

                /* king can only capture/move to neighboring squares */
                if (abs(fileIdx - curFile) <= 1 && abs(rankIdx - curRank) <= 1) {
                    if (chessBoard[rankIdx][fileIdx] == null) {
                        possibleMoves[rankIdx][fileIdx] = 'M';
                    } else {
                        if(chessBoard[rankIdx][fileIdx].color != this.color) {
                            /* Attack enemy piece! */
                            possibleMoves[rankIdx][fileIdx] = 'X';
                        } else {
                            /* Cant move to square occupied by friendly piece - defend instead*/
                            possibleMoves[rankIdx][fileIdx] = 'd';
                        }
                    }
                    continue;
                }

            }
        }
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

        if (file == curFile && rank == curRank) {
            // Probably cant get here due to checking the color in previous if statement but leave it for sanity
            Logger.logStr(funcStr + "FAIL: " + thisStr + " can not move to its own square.");
            return false;
        }else if (abs(file - curFile) <= 1 && abs(rank - curRank) <= 1) {
            ArrayList<ChessPiece> enemyPieces = (this.color == PieceColor.WHITE) ? board.getBlackPieces() : board.getWhitePieces();
            Boolean previousLoggingStatus = Logger.disableConsoleLogging();
            for (ChessPiece piece : enemyPieces) {
                if (piece.controlsSquare(file, rank, board)) {
                    if (previousLoggingStatus) {Logger.enableConsoleLogging();}
                    Logger.logStr(funcStr + "FAIL: " + thisStr + " can not move to square that is defended by an enemy piece: " + piece.color + " " + piece.type + " on " + piece.file + piece.rank);
                    return false;
                }
            }
            if (previousLoggingStatus) {Logger.enableConsoleLogging();}
            Logger.logStr(funcStr + "SUCCESS: " + thisStr + " can move to " + destString);
            return true;
        } else {
            Logger.logStr(funcStr + "FAIL: " + thisStr + " can only move to squares that are immediately bordering.");
            return false;
        }
    }

    @Override
    public Boolean canCaptureAt(Character file, Character rank, ChessBoard board) {
        String funcStr = this.type + "::canCaptureAt(): ";
        String destString = file.toString() + rank.toString();
        String thisStr = this.color + " " + this.type + " at " + this.file + this.rank;

        Logger.logStr(funcStr + "FAIL: Not yet implemented!");
        return false;
    }

    @Override
    public Boolean controlsSquare(Character file, Character rank, ChessBoard board) {
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

        // It doesnt matter if a friendly piece, enemy piece, or no piece is on the square for it to be controlled

        final Character curFile = this.file;
        final Character curRank = this.rank;

        if (file == curFile && rank == curRank) {
            Logger.logStr(funcStr + "FAIL: " + thisStr + " can not control its own square.");
            return false;
        }else if (abs(file - curFile) <= 1 && abs(rank - curRank) <= 1) {
            //King controls all neighboring squares
            Logger.logStr(funcStr + "SUCCESS: " + thisStr + " controls" + destString);
            return true;
        } else {
            Logger.logStr(funcStr + "FAIL: " + thisStr + " can only control that are immediately bordering.");
            return false;
        }
    }
}
