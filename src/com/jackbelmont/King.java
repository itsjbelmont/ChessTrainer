package com.jackbelmont;

import javax.swing.*;
import java.awt.image.BufferedImage;

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
        return false;
    }

    @Override
    public Boolean canCaptureAt(Character file, Character rank, ChessBoard board) {
        return false;
    }
}
