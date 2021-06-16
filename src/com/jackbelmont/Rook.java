package com.jackbelmont;

import javax.swing.*;
import java.awt.image.BufferedImage;

import static java.lang.Math.abs;

public class Rook extends ChessPiece {

    Rook(PieceColor color, char colPosition, char rowPosition) {
        super(color, colPosition, rowPosition, PieceType.ROOK);

        //TODO: Add pawn icons - transparent image for now
        this.icon = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
    }

    /*
        Validate a rook move
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

        //Rook can move up
        r = 1;
        while (curRank+r < 8) {
            if (chessBoard[curRank+r][curFile] == null) {
                /* Can move up to empty square */
                possibleMoves[curRank+r][curFile] = 'M';
            } else {
                /* Square occupied */
                if (chessBoard[curRank+r][curFile].color != this.color) {
                    /* Attack enemy piece! */
                    possibleMoves[curRank+r][curFile] = 'X';
                } else {
                    /* Cant move to square occupied by friendly piece */
                }
                break;
            }
            r++;
        }

        //Rook can move down
        r = 1;
        while (curRank-r >= 0) {
            if (chessBoard[curRank-r][curFile] == null) {
                /* Can move up to empty square */
                possibleMoves[curRank-r][curFile] = 'M';
            } else {
                /* Square occupied */
                if (chessBoard[curRank-r][curFile].color != this.color) {
                    /* Attack enemy piece! */
                    possibleMoves[curRank-r][curFile] = 'X';
                } else {
                    /* Cant move to square occupied by friendly piece */
                }
                break;
            }
            r++;
        }

        //Rook can move left
        f = 1;
        while (curFile+f < 8) {
            if (chessBoard[curRank][curFile+f] == null) {
                /* Square unoccupied */
                possibleMoves[curRank][curFile+f] = 'M';
            } else {
                /* Square occupied */
                if (chessBoard[curRank][curFile+f].color != this.color) {
                    /* Attack enemy piece! */
                    possibleMoves[curRank][curFile+f] = 'X';
                } else {
                    /* Cant move to square occupied by friendly piece */
                }
                break;
            }
            f++;
        }

        //Rook can move right
        f = 1;
        while (curFile-f >= 0) {
            if (chessBoard[curRank][curFile-f] == null) {
                /* Square unoccupied */
                possibleMoves[curRank][curFile-f] = 'M';
            } else {
                /* Square occupied */
                if (chessBoard[curRank][curFile-f].color != this.color) {
                    /* Attack enemy piece! */
                    possibleMoves[curRank][curFile-f] = 'X';
                } else {
                    /* Cant move to square occupied by friendly piece */
                }
                break;
            }
            f++;
        }

    }

}