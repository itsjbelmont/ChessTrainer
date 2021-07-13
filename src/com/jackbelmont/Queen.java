package com.jackbelmont;

import javax.swing.*;
import java.awt.image.BufferedImage;

import static java.lang.Math.abs;

public class Queen extends ChessPiece {
    Queen(ChessPiece.PieceColor color, char colPosition, char rowPosition) {
        super(color, colPosition, rowPosition, PieceType.QUEEN);

        //TODO: Add pawn icons - transparent image for now
        this.icon = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
    }

    /*
        Validate a Queen move
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

        int r;
        int f;

        // Queen can move up
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
                    possibleMoves[curRank+r][curFile] = 'd';
                }
                break;
            }
            r++;
        }

        // Queen can move down
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
                    possibleMoves[curRank-r][curFile] = 'd';
                }
                break;
            }
            r++;
        }

        // Queen can move left
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
                    possibleMoves[curRank][curFile+f] = 'd';
                }
                break;
            }
            f++;
        }

        // Queen can move right
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
                    possibleMoves[curRank][curFile-f] = 'd';
                }
                break;
            }
            f++;
        }

        // Queen can move diagonal up-right
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

        // Queen can move diagonal up-left
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

        /* Queen can move diagonal down-right */
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

        /* Queen can move diagonal down-left */
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
        return false;
    }

    @Override
    public Boolean canCaptureAt(Character file, Character rank, ChessBoard board) {
        return false;
    }
}
