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
                }
                break;
            }
            r++;
            f++;
        }
    }

}
