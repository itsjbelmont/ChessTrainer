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
