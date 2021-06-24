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

    /*
        Validate a knight move
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

                if ((abs(fileIdx - curFile) == 2 && abs(rankIdx - curRank) == 1) || (abs(fileIdx - curFile) == 1 && abs(rankIdx - curRank) == 2)) {
                    if (chessBoard[rankIdx][fileIdx] == null) {
                        possibleMoves[rankIdx][fileIdx] = 'M';
                    } else {
                        if (chessBoard[rankIdx][fileIdx].color != this.color) {
                            /* Attack enemy piece! */
                            possibleMoves[rankIdx][fileIdx] = 'X';
                        } else {
                            /* Cant move to square occupied by friendly piece - Defend instead */
                            possibleMoves[rankIdx][fileIdx] = 'd';
                        }
                    }
                }

            }
        }
    }

}
