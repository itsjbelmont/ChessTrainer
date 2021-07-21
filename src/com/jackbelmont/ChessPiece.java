package com.jackbelmont;

import javax.swing.*;
import java.awt.image.BufferedImage;

/*
    ChessPiece:
        Class must be overwritten by Pawn, Rook, Knight, Bishop, Queen, King
*/
public abstract class ChessPiece {
    public enum PieceColor {
        WHITE{
            @Override
            public String toString() {
                return "WHITE";
            }
        },
        BLACK {
            @Override
            public String toString() {
                return "BLACK";
            }
        }
    };

    public enum PieceType {
        PAWN {
            @Override
            public String toString() {
                return "PAWN";
            }
        },
        ROOK {
            @Override
            public String toString() {
                return "ROOK";
            }
        },
        KNIGHT {
            @Override
            public String toString() {
                return "KNIGHT";
            }
        },
        BISHOP {
            @Override
            public String toString() {
                return "BISHOP";
            }
        },
        QUEEN {
            @Override
            public String toString() {
                return "QUEEN";
            }
        },
        KING {
            @Override
            public String toString() {
                return "KING";
            }
        }
    };

    protected PieceType type;
    protected Character characterIdentifier;
    protected ChessPiece.PieceColor color;
    protected Character rank;
    protected Character file;
    protected Character[][] possibleMoves = new Character[8][8];
    protected Logger logger = new Logger();
    public ImageIcon icon;

    ChessPiece(PieceColor color, char file, char rank, PieceType type) {
        this.color = color;
        this.rank = rank;
        this.file = file;
        this.type = type;
        switch (type){
            case PAWN:
                this.characterIdentifier = 'P';
                break;
            case ROOK:
                this.characterIdentifier = 'R';
                break;
            case KNIGHT:
                this.characterIdentifier = 'N';
                break;
            case BISHOP:
                this.characterIdentifier = 'B';
                break;
            case QUEEN:
                this.characterIdentifier = 'Q';
                break;
            case KING:
                this.characterIdentifier = 'K';
                break;
        }
        this.characterIdentifier = characterIdentifier;
        this.icon = null;

        if (this.color == ChessPiece.PieceColor.WHITE) {
            logger.iLogStr("\t\tCreated WHITE Piece: " + characterIdentifier + " at " + file + rank + "\n");
        } else {
            logger.iLogStr("\t\tCreated BLACK Piece: " + characterIdentifier + " at " + file + rank + "\n");
        }
    }

    /*
       Print the chess board to the console
   */
    public void printPossibleMoves() {
        StringBuilder outputBoard = new StringBuilder();
        Character files[] = ChessBoard.files;
        Character ranks[] = {'8', '7', '6', '5', '4', '3', '2', '1'};
        int curFile = ChessBoard.getFileIdx(this.file);
        int curRank = ChessBoard.getRankIdx(this.rank);

        outputBoard.append("  " + this.color + " " + this.type + " at [" + this.file + this.rank+"]:\n");
        outputBoard.append("  |-----|-----|-----|-----|-----|-----|-----|-----|\n");
        for (Character rank : ranks) {
            int rankIdx = ChessBoard.getRankIdx(rank);
            outputBoard.append( ConsoleColors.RED + (rankIdx + 1) + ConsoleColors.RESET + " |");
            for ( Character file : files ) {
                int fileIdx = ChessBoard.getFileIdx(file);

                if (possibleMoves[rankIdx][fileIdx] == null){
                    outputBoard.append("     ");
                } else if (possibleMoves[rankIdx][fileIdx] == 'O') {
                    if (this.color == ChessPiece.PieceColor.WHITE) {
                        outputBoard.append("  " + ConsoleColors.CYAN + 'O' + ConsoleColors.RESET + "  ");
                    } else {
                        outputBoard.append("  " + ConsoleColors.GREEN + 'O' + ConsoleColors.RESET + "  ");
                    }
                } else {
                    outputBoard.append("  " + possibleMoves[rankIdx][fileIdx] + "  ");
                }
                outputBoard.append("|");
            }
            outputBoard.append("\n");
            outputBoard.append("  |-----|-----|-----|-----|-----|-----|-----|-----|\n");
        }
        outputBoard.append(ConsoleColors.RED + "     a     b     c     d     e     f     g     h   \n" + ConsoleColors.RESET);
        System.out.println(outputBoard);
    }

    /*
        Used to display the piece's info
    */
    @Override
    public String toString() {
        return this.color.toString() + " " + this.type.toString() + " at " + this.file + this.rank;
    }

    /*
        Get the rowPosition field of the class
    */
    public char getRank() {
        return rank;
    }

    /*

    */
    public int getRankAsInt() {
        return ((int)rank - '1') + 1;
    }

    /*
        Get the colPosition field of the class
    */
    public char getFile() {
        return file;
    }

    /*
        Get the PieceColor
    */
    public ChessPiece.PieceColor getColor() {
        return color;
    }

    /*
        pass an [8][8] array of chessPieces which represent a board
    */
    public abstract boolean validateMove (ChessPiece[][] chessPieces, String move);

    /*
        canMoveTo(file, rank)
            check if piece can move to a specified square
            Should be the same as canCaptureAt(file, rank) for all pieces except pawns
    */
    public boolean canMoveTo(Character file, Character rank) {
        int fileIdx = ChessBoard.getFileIdx(file);
        int rankIdx = ChessBoard.getRankIdx(rank);
        if (possibleMoves[rankIdx][fileIdx] != null && (possibleMoves[rankIdx][fileIdx] == 'X' || possibleMoves[rankIdx][fileIdx] == 'M')) {
            /* Can move to a square if its just a move OR an attack: 'M' or 'X' */
            return true;
        } else {
            /* if square not marked by possibleMoves then cant move there */
            return false;
        }
    }

    /*
        canCaptureAt(file, rank)
            check if the piece can capture a piece on a specified square
    */
    public boolean canCaptureAt(Character file, Character rank) {
        int fileIdx = ChessBoard.getFileIdx(file);
        int rankIdx = ChessBoard.getRankIdx(rank);
        if (possibleMoves[rankIdx][fileIdx] != null && possibleMoves[rankIdx][fileIdx] == 'X') {
            /* Can capture a piece at a square if possibleMoves marks it as an 'X' */
            return true;
        } else {
            /* if square not marked by possibleMoves as 'X' then cant capture there */
            return false;
        }
    }

    public boolean controlsSquare(Character file, Character rank) {
        int fileIdx = ChessBoard.getFileIdx(file);
        int rankIdx = ChessBoard.getRankIdx(rank);
        if (possibleMoves[rankIdx][fileIdx] != null) {
            if (this.type == PieceType.PAWN) {
                if (possibleMoves[rankIdx][fileIdx] == 'x' || possibleMoves[rankIdx][fileIdx] == 'X') {
                    return true;
                }
            } else {
                /* Non-Pawns control all squares they can move to or capture at - 'M' or 'X' */
                return true;
            }
        }

        /* if square not marked by possibleMoves as 'X' then cant capture there */
        return false;
    }

    /*
        public abstract void refreshPossibleMoves()
            - refreshes the possibleMoves[8][8] array based on the current position
            - WARNING: Does not take into account the other pieces on the board
    */
    public abstract void refreshPossibleMoves(ChessPiece[][] chessBoard);

    public abstract Boolean canMoveTo(Character file, Character rank, ChessBoard board);

    public abstract Boolean canCaptureAt(Character file, Character rank, ChessBoard board);

    // Used to determine if a square is defended by the enemy (important for checking king moves)
    public abstract Boolean controlsSquare(Character file, Character rank, ChessBoard board);

    // King will over-write this
    public Boolean canShortCastle(ChessBoard board) {
        System.out.println("ChessPiece::canShortCastle() FAIL: Can not castle on any piece other than a king");
        return false;
    }

    // King will over-write this
    public Boolean canLongCastle(ChessBoard board) {
        System.out.println("ChessPiece::canShortCastle() FAIL: Can not castle on any piece other than a king");
        return false;
    }
}
