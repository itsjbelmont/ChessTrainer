package com.jackbelmont;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.abs;

public class Pawn extends ChessPiece{
    Pawn(PieceColor color, char colPosition, char rowPosition) {
        super(color, colPosition, rowPosition, PieceType.PAWN);

        //TODO: Add pawn icons - transparent image for now
        this.icon = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
    }

    /*
        Validate a pawn move
    */
    @Override
    public boolean validateMove (ChessPiece[][] chessPieces, String move) {
        logger.iLogStr("Validating move: " + move);

        // validate that a string was passed so we dont accidentally index out of bounds on it
        if (move.isEmpty() || move.isBlank() || move == null) {
            logger.iLogStr("\t" + move + " is empty - can not validate!");
            return false; // failure
        }

        // All pawn moves must start with their current column
        if (move.charAt(0) != this.file) {
            logger.iLogStr("\t" + move + " is invalid - current column[" + this.file + "] != commanded column[" + move.charAt(0) + "]");
            return false; //failure
        }

        // Forward moves are denoted by characters representing: column, end rank
        //   - example: e4
        if (Pattern.matches("^[a-h][1-8]", move)){
            // Valid pattern for moving forward
            logger.iLogStr("\t" + move + " is valid!");
            return true; //success
        }
        // captures are denoted by characters representing: column start, 'x', column end, end rank

        logger.iLogStr("\t" + move + " is invalid!");
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

        if (this.color == PieceColor.WHITE) {

            // Can move 1 up if the space isnt already taken
            if (curRank < 7) {
                if (chessBoard[curRank + 1][curFile] == null) {
                    possibleMoves[curRank + 1][curFile] = 'M';
                }
            }

            // Can move 2 up if on rank 2
            if (curRank == 1 /*2*/) {
                // rank 3 and rank 4 must both be un-inhabited
                if (chessBoard[curRank+1][curFile] == null && chessBoard[curRank+2][curFile] == null) {
                    possibleMoves[curRank+2][curFile] = 'M';
                }
            }

            // Can attack 1 up & 1 left unless on file 1 or rank 8
            if (curFile > 0 && curRank < 7) {
                // square under attack MUST have an enemy piece on it
                if (chessBoard[curRank+1][curFile-1] != null) {
                    if (chessBoard[curRank+1][curFile-1].color == PieceColor.BLACK) {
                        possibleMoves[curRank + 1][curFile - 1] = 'X';
                    } else {
                        possibleMoves[curRank + 1][curFile -1] = 'd';
                    }
                } else {
                    // This is because the square is still controlled by the pawn (little x)
                    possibleMoves[curRank+1][curFile-1] = 'x';
                }
            }

            // Can attack 1 up & 1 right unless on file 8 or rank 8
            if (curFile < 7 && curRank < 7) {
                // square under attack MUST have an enemy piece on it
                if (chessBoard[curRank+1][curFile+1] != null) {
                    if (chessBoard[curRank+1][curFile+1].color == PieceColor.BLACK) {
                        possibleMoves[curRank+1][curFile+1] = 'X';
                    } else {
                        possibleMoves[curRank+1][curFile+1] = 'd';
                    }
                } else {
                    // This is because the square is still controlled by the pawn (little x)
                    possibleMoves[curRank+1][curFile+1] = 'x';
                }
            }

        } else { //this.color == PieceColor.BLACK
            // Can move 1 down
            if (curRank > 0) {
                if (chessBoard[curRank - 1][curFile] == null) {
                    possibleMoves[curRank - 1][curFile] = 'M';
                }
            }

            // Can move 2 down if on rank 7
            if (curRank == 6 /*7*/) {
                // rank 6 and rank 5 must both be un-inhabited
                if (chessBoard[curRank-1][curFile] == null && chessBoard[curRank-2][curFile] == null) {
                    possibleMoves[curRank-2][curFile] = 'M';
                }
            }

            // Can attack 1 down & 1 left
            if (curFile > 0 && curRank > 0) {
                // square under attack MUST have an enemy piece on it
                if (chessBoard[curRank-1][curFile-1] != null) {
                    if (chessBoard[curRank-1][curFile-1].color == PieceColor.WHITE) {
                        possibleMoves[curRank-1][curFile-1] = 'X';
                    } else {
                        possibleMoves[curRank-1][curFile-1] = 'd';
                    }
                } else {
                    // This is because the square is still controlled by the pawn (little x)
                    possibleMoves[curRank-1][curFile-1] = 'x';
                }
            }

            // Can attack 1 down & 1 right
            if (curFile < 7 && curRank > 0) {
                // square under attack MUST have an enemy piece on it
                if (chessBoard[curRank-1][curFile+1] != null) {
                    if (chessBoard[curRank-1][curFile+1].color == PieceColor.WHITE) {
                        possibleMoves[curRank-1][curFile+1] = 'X';
                    } else {
                        possibleMoves[curRank-1][curFile+1] = 'd';
                    }
                } else {
                    // This is because the square is still controlled by the pawn (little x)
                    possibleMoves[curRank-1][curFile+1] = 'x';
                }
            }

        }
    }

}
