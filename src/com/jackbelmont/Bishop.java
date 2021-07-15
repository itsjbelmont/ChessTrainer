package com.jackbelmont;

import javax.swing.*;
import java.awt.image.BufferedImage;

import static java.lang.Math.abs;

public class Bishop extends ChessPiece {

    Bishop(ChessPiece.PieceColor color, char colPosition, char rowPosition) {
        super(color, colPosition, rowPosition, PieceType.BISHOP);

        //TODO: Add pawn icons - transparent image for now
        this.icon = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
    }

    /*
        Validate a Bishop move
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

        int f;
        int r;

        /* Bishop can move diagonal up-right */
        f = 1;
        r = 1;
        while (curFile+f < 8 && curRank+r < 8) {
            if (chessBoard[curRank+r][curFile+f] == null) {
                /* Square unoccupied */
                possibleMoves[curRank+r][curFile+f] = 'M';
            } else {
                /* Square occupied */
                if (chessBoard[curRank+r][curFile+f].color != this.color) {
                    /* Attack enemy piece! */
                    possibleMoves[curRank+r][curFile+f] = 'X';
                } else {
                    /* Cant move to square occupied by friendly piece */
                    possibleMoves[curRank+r][curFile+f] = 'd';
                }
                break;
            }
            r++;
            f++;
        }

        /* Bishop can move diagonal up-left */
        f = 1;
        r = 1;
        while (curFile-f >= 0 && curRank+r < 8) {
            if (chessBoard[curRank+r][curFile-f] == null) {
                /* Square unoccupied */
                possibleMoves[curRank+r][curFile-f] = 'M';
            } else {
                /* Square occupied */
                if (chessBoard[curRank+r][curFile-f].color != this.color) {
                    /* Attack enemy piece! */
                    possibleMoves[curRank+r][curFile-f] = 'X';
                } else {
                    /* Cant move to square occupied by friendly piece */
                    possibleMoves[curRank+r][curFile-f] = 'd';
                }
                break;
            }
            r++;
            f++;
        }

        /* Bishop can move diagonal down-right */
        f = 1;
        r = 1;
        while (curFile+f < 8 && curRank-r >= 0) {
            if (chessBoard[curRank-r][curFile+f] == null) {
                /* Square unoccupied */
                possibleMoves[curRank-r][curFile+f] = 'M';
            } else {
                /* Square occupied */
                if (chessBoard[curRank-r][curFile+f].color != this.color) {
                    /* Attack enemy piece! */
                    possibleMoves[curRank-r][curFile+f] = 'X';
                } else {
                    /* Cant move to square occupied by friendly piece */
                    possibleMoves[curRank-r][curFile+f] = 'd';
                }
                break;
            }
            r++;
            f++;
        }

        /* Bishop can move diagonal down-left */
        f = 1;
        r = 1;
        while (curFile-f >=0 && curRank-r >= 0) {
            if (chessBoard[curRank-r][curFile-f] == null) {
                /* Square unoccupied */
                possibleMoves[curRank-r][curFile-f] = 'M';
            } else {
                /* Square occupied */
                if (chessBoard[curRank-r][curFile-f].color != this.color) {
                    /* Attack enemy piece! */
                    possibleMoves[curRank-r][curFile-f] = 'X';
                } else {
                    /* Cant move to square occupied by friendly piece */
                    possibleMoves[curRank-r][curFile-f] = 'd';
                }
                break;
            }
            r++;
            f++;
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

        if (abs(file - curFile) != abs(rank - curRank)) {
            Logger.logStr(funcStr + "FAIL: " + thisStr + " can not move to " + destString + " which is not on the diagonal of the current piece");
            return false;
        }

        Integer rankDirection = ((int)rank > (int)curRank) ? 1 : -1;
        Integer fileDirection = ((int)file > (int)curFile) ? 1 : -1;
        Character aRank = (char)((int)this.rank + rankDirection);
        Character aFile = (char)((int)this.file + fileDirection);
        while (aRank != rank && aFile != file) {
            ChessPiece square = board.getPieceAtPosition(aFile, aRank);
            if (square != null) {
                Logger.logStr(funcStr + "FAIL: " + thisStr + " can not move through " + square.color + " " + square.type + " at " + square.file + square.rank + " to get to " + destString);
                return false;
            }

            aFile = (char)((int)aFile + fileDirection);
            aRank = (char)((int)aRank + rankDirection);
        }

        Logger.logStr(funcStr + "SUCCESS: " + thisStr + " can move to " + destString);
        return true;
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

        if (abs(file - curFile) != abs(rank - curRank)) {
            Logger.logStr(funcStr + "FAIL: " + thisStr + " can not move to " + destString + " which is not on the diagonal of the current piece");
            return false;
        }

        Integer rankDirection = ((int)rank > (int)curRank) ? 1 : -1;
        Integer fileDirection = ((int)file > (int)curFile) ? 1 : -1;
        Character aRank = (char)((int)this.rank + rankDirection);
        Character aFile = (char)((int)this.file + fileDirection);
        while (aRank != rank && aFile != file) {
            ChessPiece square = board.getPieceAtPosition(aFile, aRank);
            if (square != null) {
                Logger.logStr(funcStr + "FAIL: " + thisStr + " can not move through " + square.color + " " + square.type + " at " + square.file + square.rank + " to get to " + destString);
                return false;
            }

            aFile = (char)((int)aFile + fileDirection);
            aRank = (char)((int)aRank + rankDirection);
        }

        Logger.logStr(funcStr + "SUCCESS: " + thisStr + " can move to " + destString);
        return true;
    }
}
