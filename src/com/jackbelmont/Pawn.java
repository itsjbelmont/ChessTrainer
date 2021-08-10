package com.jackbelmont;

import javax.swing.*;
import java.awt.geom.GeneralPath;
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
            Logger.logStr(funcStr + "FAIL: " + thisStr + " Can not move to a square occupied by friendly piece at " + destString);
            return false;
        }

        final Character curFile = this.file;
        final Character curRank = this.rank;
        final Character startRank = (this.color == PieceColor.WHITE) ? '2' : '7';

        // Get intended piece direction since pawns only move forward (white up vs black down)
        Integer direction = (this.color == PieceColor.WHITE) ? 1 : -1;

        if (rank - curRank == direction && curFile == file) {
            if (destination == null) {
                Logger.logStr(funcStr + "SUCCESS: " + thisStr + " can move to " + destString);
                return true;
            } else {
                Logger.logStr(funcStr + "FAIL: " + thisStr + " can not move to an occupied square at " + destString);
                return false;
            }
        } else if ((rank - curRank) == (direction * 2) && curRank == startRank && curFile == file) {
            ChessPiece middleSquare = board.getPieceAtPosition(file, (char)((int)curRank + direction));
            if (middleSquare == null && destination == null) {
                Logger.logStr(funcStr + "SUCCESS: " + thisStr + " can move to " + destString);
                return true;
            } else {
                Logger.logStr(funcStr + "FAIL: " + thisStr + " can not move to " + destString + " since a pawn can only move 2 squares forward if both squares are empty");
                return false;
            }
        } else if (rank - curRank == direction && abs(curFile - file) == 1) {
            // Captures on the diagonal
            if (destination == null) {
                Logger.logStr(funcStr + "FAIL: " + thisStr + " can only capture if the square contains an enemy piece");
                return false;
            } else if (destination.color != this.color) {
                Logger.logStr(funcStr + "SUCCESS: " + thisStr + " can move diagonal to capture a piece");
                return true;
            } else {
                Logger.logStr(funcStr + "FAIL: " + thisStr + " would only move diagonal to capture an enemy piece");
                return false;
            }
        } else {
            Logger.logStr(funcStr + "FAIL: pawns can only move 1 square forward [or 2 if they are on the back rank] - " + "rank: " + rank + " curRank: " + curRank + " direction: " + direction);
            return false;
        }
    }

    @Override
    public Boolean canCaptureAt(Character file, Character rank, ChessBoard board) {
        String funcStr = this.type + "::canCaptureAt(): ";
        String destString = file.toString() + rank.toString();
        String thisStr = this.color + " " + this.type + " at " + this.file + this.rank;

        final Character curFile = this.file;
        final Character curRank = this.rank;

        // Get intended piece direction since pawns only move forward (white up vs black down)
        Integer direction = (this.color == PieceColor.WHITE) ? 1 : -1;

        // Get  the rank for en passant
        Character enPassantRank = (this.color == PieceColor.WHITE) ? '5' : '4';

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

        // En Passant check
        // make sure we are on the correct rank and the destination rank/file are correct for a capture
        if (this.rank == enPassantRank && rank-curRank == direction && abs(curFile - file) == 1) {
            // Make sure the neighboring square is an enemy pawn (current rank, destination file)
            ChessPiece enPassantCapturePiece = board.getPieceAtPosition(file, this.rank);
            if (enPassantCapturePiece != null && enPassantCapturePiece.type == PieceType.PAWN && enPassantCapturePiece.color != this.color) {
                //Make sure the destination square is null
                ChessPiece destination = board.getPieceAtPosition(file, rank);
                if (destination == null) {
                    System.out.println(funcStr + "SUCCESS: " + thisStr + " can perform en passant to " + destString + " while capturing " + enPassantCapturePiece.color + " " + enPassantCapturePiece.type + " at " + enPassantCapturePiece.file + enPassantCapturePiece.rank);
                    return true;
                }
            }
        }

        ChessPiece destination = board.getPieceAtPosition(file, rank);
        if (destination == null) {
            Logger.logStr(funcStr + "FAIL: " + thisStr + " Can not capture on an empty square at " + destString);
            return false;
        } else if (destination != null && destination.color == this.color) {
            Logger.logStr(funcStr + "FAIL: " + thisStr + " Can not capture on square occupied by friendly piece at " + destString);
            return false;
        }


        if (rank - curRank == direction && abs(curFile - file) == 1) {
            // Captures on the diagonal
            if (destination.color != this.color) {
                Logger.logStr(funcStr + "SUCCESS: " + thisStr + " can capture a piece at " + destString);
                return true;
            } else {
                Logger.logStr(funcStr + "FAIL: " + thisStr + " can only capture on a square with an enemy piece: " + destString);
                return false;
            }

        } else {
            Logger.logStr(funcStr + "FAIL: pawns can only capture one-diagonal away");
            return false;
        }
        //TODO: en-pesant
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

        // Get intended piece direction since pawns only move forward (white up vs black down)
        Integer direction = (this.color == PieceColor.WHITE) ? 1 : -1;

        final Character curFile = this.file;
        final Character curRank = this.rank;
        if (rank - curRank == direction && abs(curFile - file) == 1) {
            // Any square diagonally in front of the pawn is controlled by it
            Logger.logStr(funcStr + "SUCCESS: " + thisStr + " controls square at " + destString);
            return true;
        } else {
            Logger.logStr(funcStr + "FAIL: " + thisStr + " can only control squares on one diagonal in front of it");
            return false;
        }
    }

}
