package com.jackbelmont;
// Code from: https://stackoverflow.com/questions/21077322/create-a-chess-board-with-jpanel
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

import static java.lang.Math.abs;

public class ChessBoard {

    // GUI Vars
    private final JPanel gui = new JPanel(new BorderLayout(3, 3));
    private JButton[][] chessBoardSquares = new JButton[8][8];
    private JPanel chessBoard;
    private JToolBar tools;
    private final JLabel message = new JLabel("Chess Openings Trainer is ready!");

    // Chess Data
    private ChessPiece[][] chessPieces = new ChessPiece[8][8];
    public enum GameMode{TWO_PLAYER, ONE_PLAYER}
    private GameMode mode = GameMode.TWO_PLAYER;
    private ChessPiece.PieceColor whosTurn = ChessPiece.PieceColor.WHITE;
    private ArrayList<ChessPiece> whitePieces = new ArrayList<>();
    private ArrayList<ChessPiece> blackPieces = new ArrayList<>();
    private Integer[][] blackControlledSquares = new Integer[8][8];
    private Integer[][] whiteControlledSquares = new Integer[8][8];
    private ChessPiece.PieceColor myPieceColor = ChessPiece.PieceColor.WHITE;
    public static final Character files[] = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
    public static final Character ranks[] = {'1', '2', '3', '4', '5', '6', '7', '8'};


    ChessBoard() {
        //Creates an empty board
    }

    ChessBoard(GameMode mode) {
        this.mode = mode;
         initializeChessPieces();
    }

    // Load chessboard from file
    public static ChessBoard loadChessBoardFromFile(String fileName) {
        ChessBoard board = new ChessBoard();
        // if file doesnt exist then just initialize the new board
        if (!Files.exists(Paths.get(fileName))) {
            System.out.println("ChessBoard(): file does not exist: " + fileName);
            return null;
        }

        try {
            File chessFile = new File(fileName);
            Scanner chessReader = new Scanner(chessFile);
            String pieceStrRegEx = "(WHITE|BLACK) (PAWN|ROOK|KNIGHT|BISHOP|QUEEN|KING) at ([a-h][1-8])";
            Pattern piecePattern = Pattern.compile(pieceStrRegEx);
            while (chessReader.hasNextLine()) {
                String line = chessReader.nextLine();

                System.out.println(line);
                Matcher chessMatcher = piecePattern.matcher(line);
                if (chessMatcher.matches()) {
                    ArrayList<ChessPiece> pieces = (chessMatcher.group(1).equals("WHITE")) ? board.whitePieces : board.blackPieces;
                    ChessPiece.PieceColor color = (chessMatcher.group(1).equals("WHITE")) ? ChessPiece.PieceColor.WHITE : ChessPiece.PieceColor.BLACK;
                    Character file = chessMatcher.group(3).charAt(0);
                    Character rank = chessMatcher.group(3).charAt(1);
                    ChessPiece piece = null;
                    switch(chessMatcher.group(2)) {
                        case "PAWN":
                            pieces.add(new Pawn(color, file, rank));
                            break;
                        case "ROOK":
                            pieces.add(new Rook(color, file, rank));
                            break;
                        case "KNIGHT":
                            pieces.add(new Knight(color, file, rank));
                            break;
                        case "BISHOP":
                            pieces.add(new Bishop(color, file, rank));
                            break;
                        case "QUEEN":
                            pieces.add(new Queen(color, file, rank));
                            break;
                        case "KING":
                            pieces.add(new King(color, file, rank));
                            break;
                        default:
                            break;
                    }
                    continue;
                }

                if (Pattern.matches("myPieceColor: (WHITE|BLACK)", line)) {
                    Pattern p = Pattern.compile("myPieceColor: (WHITE|BLACK)");
                    Matcher m = p.matcher(line);
                    if (m.matches()) {
                        board.myPieceColor = (m.group(1).equals("WHITE")) ? ChessPiece.PieceColor.WHITE : ChessPiece.PieceColor.BLACK;
                    }
                    continue;
                }

                if (Pattern.matches("whosTurn: (WHITE|BLACK)", line)) {
                    Pattern p = Pattern.compile("whosTurn: (WHITE|BLACK)");
                    Matcher m = p.matcher(line);
                    if (m.matches()) {
                        board.whosTurn = (m.group(1).equals("WHITE")) ? ChessPiece.PieceColor.WHITE : ChessPiece.PieceColor.BLACK;
                    }
                    continue;
                }

                if (Pattern.matches("mode: (ONE_PLAYER|TWO_PLAYER)", line)) {
                    Pattern p = Pattern.compile("mode: (ONE_PLAYER|TWO_PLAYER)");
                    Matcher m = p.matcher(line);
                    if (m.matches()) {
                        board.mode = (m.group(1).equals("ONE_PLAYER")) ? GameMode.ONE_PLAYER : GameMode.TWO_PLAYER;
                    }
                    continue;
                }
            }
            chessReader.close();
            board.refreshChessBoard();
        } catch (FileNotFoundException e) {
            System.out.println("Exception thrown when loading new board!");
            return null;
        }

        return board;
    }

    public int initializeChessPieces() {
        try {
            Logger.logStr("Initializing the chess pieces:");

            // Make sure the board is full of nulls to start
            for (int ii = 0; ii < 8; ii++) {
                for (int jj = 0; jj < 8; jj++) {
                    chessPieces[ii][jj] = null;
                }
            }

            // Initialize the pawns
            Logger.logStr("\tInitializing the pawns");
            for (int i = 0; i < 8; i++) {
                Character column = (char) ((int) 'a' + i);
                // White pawns on 2nd rank
                setPieceAtPosition(new Pawn(ChessPiece.PieceColor.WHITE, column, '2'));
                // Black pawns on 7th rank
                setPieceAtPosition(new Pawn(ChessPiece.PieceColor.BLACK, column, '7'));
            }

            // Initialize the rooks
            Logger.logStr("\tInitializing the rooks");
            setPieceAtPosition(new Rook(ChessPiece.PieceColor.WHITE, 'a', '1'));
            setPieceAtPosition(new Rook(ChessPiece.PieceColor.WHITE, 'h', '1'));
            setPieceAtPosition(new Rook(ChessPiece.PieceColor.BLACK, 'a', '8'));
            setPieceAtPosition(new Rook(ChessPiece.PieceColor.BLACK, 'h', '8'));

            // Initialize the knights
            Logger.logStr("\tInitializing the knights");
            setPieceAtPosition(new Knight(ChessPiece.PieceColor.WHITE, 'b', '1'));
            setPieceAtPosition(new Knight(ChessPiece.PieceColor.WHITE, 'g', '1'));
            setPieceAtPosition(new Knight(ChessPiece.PieceColor.BLACK, 'b', '8'));
            setPieceAtPosition(new Knight(ChessPiece.PieceColor.BLACK, 'g', '8'));

            // Initialize the bishops
            Logger.logStr("\tInitializing the bishops");
            setPieceAtPosition(new Bishop(ChessPiece.PieceColor.WHITE, 'c', '1'));
            setPieceAtPosition(new Bishop(ChessPiece.PieceColor.WHITE, 'f', '1'));
            setPieceAtPosition(new Bishop(ChessPiece.PieceColor.BLACK, 'c', '8'));
            setPieceAtPosition(new Bishop(ChessPiece.PieceColor.BLACK, 'f', '8'));

            // Initialize the queens
            Logger.logStr("\tInitializing the queens");
            setPieceAtPosition(new Queen(ChessPiece.PieceColor.WHITE, 'd', '1'));
            setPieceAtPosition(new Queen(ChessPiece.PieceColor.BLACK, 'd', '8'));

            // Initialize the kings
            Logger.logStr("\tInitializing the kings");
            setPieceAtPosition(new King(ChessPiece.PieceColor.WHITE, 'e', '1'));
            setPieceAtPosition(new King(ChessPiece.PieceColor.BLACK, 'e', '8'));

            Logger.logStr("Finished initializing the chess pieces!");

        } catch (Exception e) {
            System.out.println("ERROR: Exception thrown while initializing the chess board!");
            e.printStackTrace();
            System.exit(1);
        }

        return 0;
    }

    public final int initializeGui() {
        gui.setBorder(new EmptyBorder(5,5,5,5));
        tools = new JToolBar();
        tools.setFloatable(false);
        gui.add(tools, BorderLayout.PAGE_START);
        tools.add(new JButton("Configure Trainer"));
        tools.add(new JButton("Start"));
        tools.addSeparator();
        tools.add(new JButton("Reset"));
        tools.addSeparator();
        tools.add(message);

        chessBoard = new JPanel(new GridLayout(10, 10));
        gui.add(chessBoard);
        chessBoard.setBorder(new LineBorder(Color.RED));

        Insets buttonMargin = new Insets(0,0,0,0);
        for (int ii = 0; ii < chessBoardSquares.length; ii++) {
            for (int jj = 0; jj < chessBoardSquares.length; jj++) {
                JButton square= new JButton();
                square.setMargin(buttonMargin);
                ImageIcon icon = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
                square.setIcon(icon);
                if ((jj % 2 == 1 && ii % 2 == 0) || (jj % 2 == 0 && ii % 2 == 1)) {
                    square.setBackground(Color.BLACK);
                } else {
                    square.setBackground(Color.WHITE);
                }
                chessBoardSquares[ii][jj] = square;
            }
        }

        for (int ii = 0; ii < 10; ii++) {
            for (int jj = 0; jj < 10; jj++) {
                if (jj == 0 || ii == 0 || jj == 9 || ii == 9) {
                    // Add a row/column label
                    if (ii == jj || (ii == 0 && jj == 9) || (ii == 9 && jj == 0)) {
                        chessBoard.add(new JLabel(""));
                    } else {
                        if (ii == 0 || ii == 9) {
                            if (myPieceColor == ChessPiece.PieceColor.WHITE) {
                                JLabel label = new JLabel("" + (char) ((int) 'A' + jj - 1));
                                label.setHorizontalAlignment(JLabel.CENTER);
                                chessBoard.add(label);
                            } else if (myPieceColor == ChessPiece.PieceColor.BLACK) {
                                JLabel label = new JLabel("" + (char) ((int) 'H' - jj + 1));
                                label.setHorizontalAlignment(JLabel.CENTER);
                                chessBoard.add(label);
                            }
                        } else {
                            if (myPieceColor == ChessPiece.PieceColor.WHITE) {
                                JLabel label = new JLabel("" + (9 - ii));
                                label.setHorizontalAlignment(JLabel.CENTER);
                                chessBoard.add(label);
                            } else if (myPieceColor == ChessPiece.PieceColor.BLACK) {
                                JLabel label = new JLabel("" + ii);
                                label.setHorizontalAlignment(JLabel.CENTER);
                                chessBoard.add(label);
                            }
                        }
                    }
                } else {
                    //Add a board square
                    chessBoard.add(chessBoardSquares[ii-1][jj-1]);
                }
            }
        }
        return 0; //success
    }

    public void refreshChessBoard() {
        /*
            All we need to do in order to move a piece now is to change the pieces file and rank index and then
            call this function to re-draw the board
        */

        // First we need to nullify board
        for (Character rank : ranks) {
            for (Character file : files) {
                Integer rankIdx = getRankIdx(rank);
                Integer fileIdx = getFileIdx(file);
                chessPieces[rankIdx][fileIdx] = null;
            }
        }

        for (ChessPiece whitePiece : whitePieces) {
            Integer rankIdx = getRankIdx(whitePiece.rank);
            Integer fileIdx = getFileIdx(whitePiece.file);
            chessPieces[rankIdx][fileIdx] = whitePiece;
        }
        for (ChessPiece blackPiece : blackPieces) {
            Integer rankIdx = getRankIdx(blackPiece.rank);
            Integer fileIdx = getFileIdx(blackPiece.file);
            chessPieces[rankIdx][fileIdx] = blackPiece;
        }
    }

    public boolean move(String command) {
        ChessPiece pieceToMove = null;
        ChessPiece.PieceType type = null;
        Boolean capture = false;
        Boolean promotion = false;
        Boolean castle = false;
        Boolean enPassant = false;
        Character originRank = null;
        Character originFile = null;
        Character destinationFile = null;
        Character destinationRank = null;
        ChessPiece.PieceType promotionType = null;
        String logStr = "ChessBoard::move() ";

        ChessPiece enPassantCapturedPiece = null;
        ChessPiece castleRook = null;
        Character rookDestFile = null;
        Character rookDestRank = null;

        /* Regular Expressions for chess notation move strings */
        String pawnMoveStr = "^([a-h])(x([a-h]))?([1-8])(=?([kqrbn]))?";
        String majorPieceMoveStr = "^([NRBQ])([a-h])?([1-8])?(x)?([a-h][1-8])";
        String kingPieceMoveStr = "^K(x)?([a-h][1-8])";

        if (Pattern.matches(pawnMoveStr,command)) {
            Pattern p = Pattern.compile(pawnMoveStr);
            Matcher m = p.matcher(command);

            if (m.matches()) {
                type = ChessPiece.PieceType.PAWN;

                // Set  the rank for en passant
                Character enPassantRank = (whosTurn == ChessPiece.PieceColor.WHITE) ? '5' : '4';

                // Get intended piece direction since pawns only move forward (white up vs black down)
                Integer direction = (whosTurn == ChessPiece.PieceColor.WHITE) ? 1 : -1;

                if (m.group(1) != null) {
                    originFile = m.group(1).charAt(0);
                    /* Default pawns only move forwared - may be overwriten by capture */
                    destinationFile = originFile;
                    logStr = logStr + "Trying to move Pawn on file " + originFile + " to ";
                } else {
                    /* Pawn move always starts with the origin file */
                    return false;
                }

                if (m.group(2) != null) {
                    capture = true;
                    if(m.group(3) != null) {
                        destinationFile = m.group(3).charAt(0);
                        logStr = logStr + "capture at " + destinationFile;
                    } else {
                        return false;
                    }
                } else {
                    logStr = logStr + originFile;
                }

                if(m.group(4) != null) {
                    destinationRank = m.group(4).charAt(0);
                    logStr = logStr + destinationRank + " ";
                } else {
                    /* Every pawn move must specify a destination rank */
                    return false;
                }

                /* Can only move pieces associated with the current turn */
                ArrayList<ChessPiece> piecesToMove = (whosTurn == ChessPiece.PieceColor.WHITE) ? whitePieces : blackPieces;

                for (ChessPiece piece : piecesToMove) {
                    if (piece.type == type && piece.getFile() == originFile) {
                        Boolean pieceCanMakeMove = false;
                        if ( capture ) {
                            pieceCanMakeMove = piece.canCaptureAt(destinationFile, destinationRank, this);
                        } else {
                            pieceCanMakeMove = piece.canMoveTo(destinationFile, destinationRank, this);
                        }

                        if (pieceCanMakeMove) {
                            /* Dont need to worry about multiple pawns on a given file moving to the same square */
                            pieceToMove = piece;
                            break;
                        }
                    }
                }

                if (pieceToMove == null) {
                    System.out.println("ChessBoard::move() there are no moves that can make the specified move right now!");
                    return false;
                }

                // Check if the move is en passant
                if (pieceToMove.canCaptureAt(destinationFile, destinationRank, this) && capture && pieceToMove.rank == enPassantRank) {
                    ChessPiece destinationSquare = getPieceAtPosition(destinationFile, destinationRank);
                    if (destinationSquare != null) {
                        System.out.println("ChessBoard::move(): FAIL: Can not en-passant to an occupied square");
                        return false;
                    } else {
                        enPassantCapturedPiece = getPieceAtPosition(destinationFile, pieceToMove.rank);
                        if (enPassantCapturedPiece == null) {
                            System.out.println("ChessBoard::move(): FAIL: Can not capture a null square with en passant");
                            return false;
                        }
                        enPassant = true;
                    }
                }

                if (m.group(5) != null) {
                    if ((pieceToMove.color == ChessPiece.PieceColor.WHITE && destinationRank == '8') ||
                         pieceToMove.color == ChessPiece.PieceColor.BLACK && destinationRank == '1') {
                        if (m.group(6) != null) {
                            promotion = true;
                            switch (m.group(6).charAt(0)) {
                                case 'r':
                                    logStr = logStr + "with a promotion to a ROOK";
                                    promotionType = ChessPiece.PieceType.ROOK;
                                    break;
                                case 'n':
                                    logStr = logStr + "with a promotion to a KNIGHT";
                                    promotionType = ChessPiece.PieceType.KNIGHT;
                                    break;
                                case 'b':
                                    logStr = logStr + "with a promotion to a BISHOP";
                                    promotionType = ChessPiece.PieceType.BISHOP;
                                    break;
                                case 'q':
                                    logStr = logStr + "with a promotion to a QUEEN";
                                    promotionType = ChessPiece.PieceType.QUEEN;
                                    break;
                                default:
                                    System.out.println("ChessBoard::move() something went wrong getting the promotion piece!");
                                    return false;
                            }
                        }
                    } else {
                        System.out.println("ChessBoard::move() only can promote pawn when white piece moves to rank 8 or black piece moves to rank 1");
                        return false;
                    }
                } else if ((pieceToMove.color == ChessPiece.PieceColor.WHITE && destinationRank == '8') ||
                            pieceToMove.color == ChessPiece.PieceColor.BLACK && destinationRank == '1') {
                    System.out.println("ChessBoard::move() any pawn move to the back rank MUST be accompanied by a promotion");
                    return false;
                } else {
                    promotion = false;
                }

            } else {
                System.out.println("ChessBoard::move() something went wrong matching pawn movement string!");
                return false;
            }
        } else if (Pattern.matches(majorPieceMoveStr,command)) {
            Pattern p = Pattern.compile(majorPieceMoveStr);
            Matcher m = p.matcher(command);

            if (m.matches()) {
                // Get the piece type that we are trying to move
                switch(m.group(1).charAt(0)) {
                    case 'R': type = ChessPiece.PieceType.ROOK; break;
                    case 'N': type = ChessPiece.PieceType.KNIGHT; break;
                    case 'B': type = ChessPiece.PieceType.BISHOP; break;
                    case 'Q': type = ChessPiece.PieceType.QUEEN; break;
                    default:
                        System.out.println("ChessBoard::move() Failed to get they pice type you tried to move");
                        return false;
                }

                // Determine if capture is specified
                if (m.group(4) != null) {
                    capture = true;
                }

                // Get destination square
                if (m.group(5) != null) {
                    destinationFile = m.group(5).charAt(0);
                    destinationRank = m.group(5).charAt(1);
                } else {
                    System.out.println("ChessBoard::move() Failed to get the destination square information");
                    return false;
                }

                // If origin squares were specified grab them now
                if (m.group(2) != null) {
                    originFile = m.group(2).charAt(0);
                }
                if (m.group(3) != null) {
                    originRank = m.group(3).charAt(0);
                }

                //Find the piece we are trying to move
                ArrayList<ChessPiece> piecesThatCanMakeMove = new ArrayList<>();
                ArrayList<ChessPiece> thisColorPieces = (whosTurn == ChessPiece.PieceColor.WHITE) ? this.whitePieces : this.blackPieces;
                for (ChessPiece piece : thisColorPieces) {
                    // Continue if the pice isnt the right type
                    if (piece.type != type) {
                        continue;
                    }

                    // filter by rank if it was specified
                    if (originRank != null) {
                        if (piece.rank != originRank) {
                           continue;
                        }
                    }
                    // Filter by file if it was specified
                    if (originFile != null) {
                        if (piece.file != originFile) {
                            continue;
                        }
                    }
                    if (capture) {
                        if (piece.canCaptureAt(destinationFile, destinationRank, this)) {
                             piecesThatCanMakeMove.add(piece);
                        }
                    } else {
                        if(piece.canMoveTo(destinationFile, destinationRank, this)) {
                            piecesThatCanMakeMove.add(piece);
                       }
                    }
                }

                if(piecesThatCanMakeMove.size() < 1) {
                    System.out.println("ChessBoard::move() FAIL: No pieces can make the specified move");
                    return false;
                } else if (piecesThatCanMakeMove.size() > 1) {
                    System.out.println("ChessBoard::move() FAIL: Too many pieces can make the specified move");
                    return false;
                } else {
                    pieceToMove = piecesThatCanMakeMove.get(0);
                    logStr = logStr + "Moving " + pieceToMove.color + " " + pieceToMove.type + " at " + pieceToMove.file + pieceToMove.rank + " with capture=" + capture + " to " + destinationFile + destinationRank;
                }
            }
        } else if (Pattern.matches(kingPieceMoveStr,command)) {
            Pattern p = Pattern.compile(kingPieceMoveStr);
            Matcher m = p.matcher(command);

            if (m.matches()) {
                ChessPiece king = null;

                // Get the king from all pieces
                ArrayList<ChessPiece> myPieces = (whosTurn == ChessPiece.PieceColor.WHITE) ? whitePieces : blackPieces;
                for (ChessPiece piece : myPieces) {
                    if (piece.type == ChessPiece.PieceType.KING) {
                        king = piece;
                    }
                }

                if (king == null) {
                    System.out.println("ChessBoard::move() FAIL: Couldnt find king for " + whosTurn + " pieces");
                    return false;
                }

                if (m.group(1) != null){
                    capture = true;
                }

                if (m.group(2) != null) {
                    destinationFile = m.group(2).charAt(0);
                    destinationRank = m.group(2).charAt(1);
                } else {
                    System.out.println("ChessBoard::move() FAIL: couldnt get the destination square");
                    return false;
                }

                // Make sure the king can make the move
                if (capture) {
                    if (!king.canCaptureAt(destinationFile, destinationRank, this)) {
                        System.out.println("ChessBoard::move() FAIL: king can not capture on " + destinationFile + destinationRank);
                        return false;
                    }
                } else {
                    if (!king.canMoveTo(destinationFile, destinationRank, this)) {
                        System.out.println("ChessBoard::move() FAIL: king can not move to " + destinationFile + destinationRank);
                        return false;
                    }
                }

                // move the king
                pieceToMove = king;
                logStr = logStr + "moving " + pieceToMove.color + " " + pieceToMove.type + " at " + pieceToMove.file + pieceToMove.rank + " to " + destinationFile + destinationRank;
            }
        } else if (Pattern.matches("O-O-O", command) || Pattern.matches("0-0-0", command)) {
            // Long castle
            castle = true;
            ArrayList<ChessPiece> pieces = (whosTurn == ChessPiece.PieceColor.WHITE) ? whitePieces : blackPieces;
            ChessPiece king = null;
            for (ChessPiece piece : pieces) {
                if (piece.type == ChessPiece.PieceType.KING) {
                    king = piece;
                    break;
                }
            }

            if (king == null) {
                System.out.println("ChessBoard::move(): FAIL: Couldnt find " + whosTurn + " king");
                return false;
            }

            if (!king.canLongCastle(this)) {
                System.out.println("ChessBoard::move(): FAIL: " + king.color + " " + king.type + " can not castle right now");
                return false;
            }

            destinationFile = 'c';
            destinationRank = (whosTurn == ChessPiece.PieceColor.WHITE) ? '1' : '8';
            pieceToMove = king;

            castleRook = getPieceAtPosition('a', destinationRank);
            if (castleRook == null) {
                System.out.println("ChessBoard::move(): FAIL: Couldnt find rook for long castle");
                return false;
            }
            rookDestFile = 'd';
            rookDestRank = destinationRank;

            logStr = logStr + "performing long castle";
        } else if (Pattern.matches("O-O", command) || Pattern.matches("0-0", command)) {
            // Short castle
            castle = true;
            ArrayList<ChessPiece> pieces = (whosTurn == ChessPiece.PieceColor.WHITE) ? whitePieces : blackPieces;
            ChessPiece king = null;
            for (ChessPiece piece : pieces) {
                if (piece.type == ChessPiece.PieceType.KING) {
                    king = piece;
                    break;
                }
            }

            if (king == null) {
                System.out.println("ChessBoard::move(): FAIL: Couldnt find " + whosTurn + " king");
                return false;
            }

            if (!king.canShortCastle(this)) {
                System.out.println("ChessBoard::move(): FAIL: " + king.color + " " + king.type + " can not castle right now");
                return false;
            }

            destinationFile = 'g';
            destinationRank = (whosTurn == ChessPiece.PieceColor.WHITE) ? '1' : '8';
            pieceToMove = king;

            castleRook = getPieceAtPosition('h', destinationRank);
            if (castleRook == null) {
                System.out.println("ChessBoard::move(): FAIL: Couldnt find rook for long castle");
                return false;
            }
            rookDestFile = 'f';
            rookDestRank = destinationRank;

            logStr = logStr + "performing short castle";
        } else {
            System.out.println("ChessBoard::move(): FAIL: BAD CHESS NOTATION!");
            return false;
        }

        // TIME TO ACTUALLY MOVE EVERYTHING
        System.out.println(logStr);

        // If we are castling we need to remove the rook first
        if (castle) {
            if (castleRook != null) {
                movePiece(castleRook, rookDestFile, rookDestRank);
            } else {
                System.out.println("ChessBoard::move() FAILED: Rook to castle is null");
                return false;
            }
        }

        /* Perform the specified move! */
        if (pieceToMove != null) {
            movePiece(pieceToMove, destinationFile, destinationRank);
        } else {
            System.out.println("ChessBoard::move() FAILED: Piece determined for the move is NULL");
            return false;
        }

        if (enPassant) {
            removePieceAtPosition(enPassantCapturedPiece.file, enPassantCapturedPiece.rank);
        }

        if (promotion) {
            System.out.println("ChessBoard::move() promoting pawn to " + promotionType);
            ChessPiece removedPiece = removePieceAtPosition(pieceToMove.file, pieceToMove.rank);
            if (removedPiece != null) {
                ChessPiece newPiece;
                switch(promotionType) {
                    case BISHOP:
                        newPiece = new Bishop(removedPiece.color, removedPiece.file, removedPiece.rank);
                        break;
                    case KNIGHT:
                        newPiece = new Knight(removedPiece.color, removedPiece.file, removedPiece.rank);
                        break;
                    case ROOK:
                        newPiece = new Rook(removedPiece.color, removedPiece.file, removedPiece.rank);
                        break;
                    case QUEEN:
                        newPiece = new Queen(removedPiece.color, removedPiece.file, removedPiece.rank);
                        break;
                    default:
                        System.out.println("ChessBoard::move() Something went wrong creating a new piece for promotion - adding the old one back");
                        setPieceAtPosition(removedPiece);
                        return false;
                }
                setPieceAtPosition(newPiece);
            }

        }

        /* Successful move if we got here - Now its the opponents turn */
        if (whosTurn == ChessPiece.PieceColor.WHITE){
            whosTurn = ChessPiece.PieceColor.BLACK;
        } else {
            whosTurn = ChessPiece.PieceColor.WHITE;
        }
        return true;
    }


    public boolean movePiece(ChessPiece piece, Character file, Character rank) {
        if (piece == null) {
            Logger.logStr("ChessBoard::movePice(): No piece specified");
            return false;
        } else {
            Logger.logStr("ChessBoard::movePiece(): Trying to move piece at " + piece.file + piece.rank + " to " + file + rank);
        }
        // Validate file and rank values
        Integer fileIdx = getFileIdx(file);
        Integer rankIdx = getRankIdx(rank);
        if (fileIdx > 7 || fileIdx < 0 || rankIdx > 7 || rankIdx < 0) {
            Logger.logStr("ChessBoard::movePice(): Bad index");
            return false;
        }
        ChessPiece destination = getPieceAtPosition(file, rank);
        if (destination != null && destination.color == piece.color) {
            /* Can not move to a square occupied by a friendly piece */
            Logger.logStr("ChessBoard::movePice(): destination square is occupied by a friendly piece");
            return false;
        }

        //TODO: verify that the move results in a valid board - might need to generate and verify a new resulting board (ie. no self checks)

        // Dont need to verify the move again - this function can be used to force moves
        // ChessBoard::move() is used for validating the move first
        if (destination != null && destination.color != piece.color) {
            /* remove enemy piece if it exists */
            removePieceAtPosition(file, rank);
        }
        piece.file = file;
        piece.rank = rank;
        refreshChessBoard();
        return true;
    }

    public void printChessBoard() {
        StringBuilder outputBoard = new StringBuilder();
        if (whosTurn == ChessPiece.PieceColor.WHITE) {
            outputBoard.append("  " + ConsoleColors.WHITE_PIECE + whosTurn + "S" + ConsoleColors.RESET + " TURN:\n");
        } else {
            outputBoard.append("  " + ConsoleColors.BLACK_PIECE + whosTurn + "S" + ConsoleColors.RESET + " TURN:\n");
        }
        outputBoard.append("  |-----|-----|-----|-----|-----|-----|-----|-----|\n");
        for (int ii=7; ii>=0; ii--) { //Note a0 is bottom right but we start printing from a8
            outputBoard.append( ConsoleColors.RED + (ii + 1) + ConsoleColors.RESET + " |");
            for (int jj=0; jj<8; jj++) {
                Character column = (char)((int)'a' + jj);
                Character row = (char)((int)'1' + ii);
                ChessPiece aPiece = getPieceAtPosition(column, row);

                if(aPiece == null) {
                    outputBoard.append("     ");
                } else {

                    if (aPiece.color == ChessPiece.PieceColor.WHITE) {
                        outputBoard.append("  " + ConsoleColors.WHITE_PIECE + aPiece.characterIdentifier + ConsoleColors.RESET + "  ");
                    } else {
                            outputBoard.append("  " + ConsoleColors.BLACK_PIECE + aPiece.characterIdentifier + ConsoleColors.RESET + "  ");
                    }
                }
                outputBoard.append(ConsoleColors.RESET + "|");

            }
            outputBoard.append("\n");
            outputBoard.append("  |-----|-----|-----|-----|-----|-----|-----|-----|\n");
        }
        outputBoard.append(ConsoleColors.RED + "     a     b     c     d     e     f     g     h   \n" + ConsoleColors.RESET);
        System.out.print(outputBoard);
    }

    public void printControlledSquares(ChessPiece.PieceColor color) {
        StringBuilder outputBoard = new StringBuilder();
        Character files[] = ChessBoard.files;
        Character ranks[] = {'8', '7', '6', '5', '4', '3', '2', '1'};

        Integer[][] controlledSquares;
        if (color == ChessPiece.PieceColor.WHITE) {
            controlledSquares = whiteControlledSquares;
        } else {
            controlledSquares = blackControlledSquares;
        }

        outputBoard.append("  |-----|-----|-----|-----|-----|-----|-----|-----|\n");
        for (Character rank : ranks) {
            int rankIdx = ChessBoard.getRankIdx(rank);
            outputBoard.append( ConsoleColors.RED + (rankIdx + 1) + ConsoleColors.RESET + " |");
            for ( Character file : files ) {
                int fileIdx = ChessBoard.getFileIdx(file);

                if (controlledSquares[rankIdx][fileIdx] == null || controlledSquares[rankIdx][fileIdx] == 0){
                    outputBoard.append("     ");
                } else
                    outputBoard.append("  " + controlledSquares[rankIdx][fileIdx] + "  ");

                outputBoard.append("|");
            }
            outputBoard.append("\n");
            outputBoard.append("  |-----|-----|-----|-----|-----|-----|-----|-----|\n");
        }
        outputBoard.append(ConsoleColors.RED + "     a     b     c     d     e     f     g     h   \n" + ConsoleColors.RESET);
        System.out.println(outputBoard);
    }

    public static void playChessBoard() {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                ChessBoard cb =
                        new ChessBoard(ChessBoard.GameMode.TWO_PLAYER);

                JFrame f = new JFrame("Chess Opening Trainer");
                f.add(cb.getGui());
                f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                f.setLocationByPlatform(true);

                // ensures the frame is the minimum size it needs to be
                // in order display the components within it
                f.pack();
                // ensures the minimum size is enforced.
                f.setMinimumSize(f.getSize());
                f.setVisible(true);
            }
        };
        SwingUtilities.invokeLater(r);
    }

    public void setMyPieceColor(ChessPiece.PieceColor color) {
        this.myPieceColor = color;

        // if you change the board color you must re-initialize the board
        initializeChessPieces();
    }

    public ChessPiece getPieceAtPosition(Character file, Character rank) {
        Integer fileIdx = getFileIdx(file);
        Integer rankIdx = getRankIdx(rank);

        if (fileIdx <= 7 && fileIdx >= 0 && rankIdx <= 7 && rankIdx >= 0) {
            return chessPieces[rankIdx][fileIdx];
        } else {
            return null;
        }

    }

    public ChessPiece removePieceAtPosition(Character file, Character rank) {
        ChessPiece removePiece = null;

        // Need to search through white and black pieces to remove piece
        for (int i=0; i<whitePieces.size(); i++) {
            if (whitePieces.get(i).file == file && whitePieces.get(i).rank == rank) {
                // Found piece - now remove it
                removePiece = whitePieces.remove(i);
            }
        }
        for (int i=0; i<blackPieces.size(); i++) {
            if (blackPieces.get(i).file == file && blackPieces.get(i).rank == rank) {
                // Found piece - now remove it
                removePiece = blackPieces.remove(i);
            }
        }

        refreshChessBoard();

        return removePiece;
    }

    public ChessPiece setPieceAtPosition(ChessPiece piece) {

        ChessPiece square = getPieceAtPosition(piece.file, piece.rank);
        if (square != null) {
            System.out.println("ChessBoard::setPieceAtPosition() FAIL: Can not add new " + piece.color + " " + piece.type + " at " + piece.file + piece.rank + " since the square is already occupied by " + square.color + " " + square.type);
            return null;
        }

        System.out.println("ChessBoard::setPieceAtPosition() adding new " + piece.color + " " + piece.type + " at " + piece.file + piece.rank);

        // Add piece to the list of pieces for each team
        if (piece.color == ChessPiece.PieceColor.WHITE) {
            whitePieces.add(piece);
        } else {
            // if its not white, it must be black!
            blackPieces.add(piece);
        }

        // This will update the board with the new piece
        refreshChessBoard();

        return piece;
    }

    public static Integer getFileIdx(Character file) {
        return (int)file - (int)'a';
    }

    public static Integer getRankIdx(Character rank) {
        return (int)rank - (int)'1';
    }

    public ArrayList<ChessPiece> getWhitePieces() {
        return whitePieces;
    }

    public ArrayList<ChessPiece> getBlackPieces() {
        return blackPieces;
    }

    public ChessPiece.PieceColor getWhosTurn() {
        return this.whosTurn;
    }

    public static Character fileIdxToFileChar (int fileIdx) {
        return (char)('a' + fileIdx);
    }

    public static Character rankIdxToRankChar(int rankIdx) {
        return (char)('1' + rankIdx);
    }

    public final JComponent getChessBoard() {
        return chessBoard;
    }

    public Boolean saveToFile() {
        return false;
    }

    public Boolean saveToFile(String name) {
        try {
            // add .chess extension (chessboard)
            if (!name.endsWith(".chess")) {
                name = name + ".chess";
            }

            // If the file already exists then dont overwrite
            if (Files.exists(Paths.get(name))) {
                System.out.println("ChessBoard::saveToFile(): FAIL: File already exists: " + name);
                return false;
            }

            System.out.println("ChessBoard::saveToFile(): Saving chessboard to file: " + name);
            BufferedWriter writer = new BufferedWriter(new FileWriter(name, true));
            for (ChessPiece piece : blackPieces) {
                writer.write(piece.toString() + "\n");
            }
            for (ChessPiece piece : whitePieces) {
                writer.write(piece.toString() + "\n");
            }

            writer.write("\n");
            if (myPieceColor == ChessPiece.PieceColor.WHITE) {
                writer.write("myPieceColor: WHITE\n");
            } else {
                writer.write("myPieceColor: BLACK\n");
            }
            if (whosTurn == ChessPiece.PieceColor.WHITE) {
                writer.write("whosTurn: WHITE\n");
            } else {
                writer.write("whosTurn: BLACK\n");
            }
            if (mode == GameMode.ONE_PLAYER) {
                writer.write("mode: ONE_PLAYER\n");
            } else {
                writer.write("mode: TWO_PLAYER\n");
            }

            writer.close();
            return true;
        } catch(IOException e) {
            System.out.println("BAD FILE WRITE TO FILE: " + name);
            return false;
        }
    }

    public final JComponent getGui() {
        return gui;
    }

}
