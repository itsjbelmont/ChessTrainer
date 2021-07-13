package com.jackbelmont;
// Code from: https://stackoverflow.com/questions/21077322/create-a-chess-board-with-jpanel
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Array;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.*;

import static java.lang.Math.abs;

public class ChessBoard {
    public enum GameMode{TWO_PLAYER, ONE_PLAYER};
    public static final Character files[] = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
    public static final Character ranks[] = {'1', '2', '3', '4', '5', '6', '7', '8'};

    private final JPanel gui = new JPanel(new BorderLayout(3, 3));
    private JButton[][] chessBoardSquares = new JButton[8][8];
    private JPanel chessBoard;
    private JToolBar tools;
    private final JLabel message = new JLabel("Chess Openings Trainer is ready!");

    private ChessPiece[][] chessPieces = new ChessPiece[8][8];
    private ArrayList<ChessPiece> whitePieces = null;
    private ArrayList<ChessPiece> blackPieces = null;
    private Integer[][] blackControlledSquares = new Integer[8][8];
    private Integer[][] whiteControlledSquares = new Integer[8][8];
    private ChessPiece.PieceColor myPieceColor;
    private ChessPiece.PieceColor whosTurn = ChessPiece.PieceColor.WHITE;
    private GameMode mode;


    ChessBoard() {
        this.myPieceColor = ChessPiece.PieceColor.WHITE;
        this.mode = GameMode.ONE_PLAYER;

        // Game always starts with WHITE
        this.whosTurn = ChessPiece.PieceColor.WHITE;

        initializeChessPieces();
        initializeGui();
    }

    ChessBoard(GameMode mode) {
        this.mode = mode;
         // Default to giving the user the white pieces
         if (this.mode == GameMode.ONE_PLAYER) {
             myPieceColor = ChessPiece.PieceColor.WHITE;
         } else if (this.mode == GameMode.TWO_PLAYER) {
             myPieceColor = null;
         }

         // Game always starts with WHITE
         this.whosTurn = ChessPiece.PieceColor.WHITE;

         initializeChessPieces();
         initializeGui();
    }

    public int initializeChessPieces() {
        try {
            Logger.logStr("Initializing the chess pieces:");

            whitePieces = new ArrayList<>();
            blackPieces = new ArrayList<>();
            getPieceAtPosition('a', '1');

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

            // Pieces are on the board - Now we need to set up their moves
            Logger.logStr(("\tRefreshing possible moves for all pieces"));
            refreshAllPieceMoves();

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

    public void refreshAllPieceMoves() {
        //Refresh White Pieces
        for (ChessPiece piece : whitePieces) {
            piece.refreshPossibleMoves(this.chessPieces);
        }

        //Refresh Black Pieces
        for (ChessPiece piece : blackPieces) {
            piece.refreshPossibleMoves(this.chessPieces);
        }

        refreshWhiteControlledSquares();
        refreshBlackControlledSquares();
    }

    public void refreshBlackControlledSquares() {
        Character files[] = ChessBoard.files;
        Character ranks[] = ChessBoard.ranks;

        //TODO: what about pieces defending thier own pieces - that is controlled right? 'd'

        // All squares default to attacked by 0 pieces
        for (Character file : files) {
            for (Character rank : ranks) {
                int fileIdx = ChessBoard.getFileIdx(file);
                int rankIdx = ChessBoard.getRankIdx(rank);
                blackControlledSquares[rankIdx][fileIdx] = 0;
            }
        }

        for (ChessPiece piece : blackPieces) {
            for (Character file : files) {
                for (Character rank : ranks) {
                    int fileIdx = ChessBoard.getFileIdx(file);
                    int rankIdx = ChessBoard.getRankIdx(rank);
                    if (piece.controlsSquare(file, rank)) {
                        blackControlledSquares[rankIdx][fileIdx]++;
                    }
                }
            }
        }
    }

    public void refreshWhiteControlledSquares() {
        Character files[] = ChessBoard.files;
        Character ranks[] = ChessBoard.ranks;

        //TODO: what about pieces defending thier own pieces - that is controlled right? 'd'

        // All squares default to attacked by 0 pieces
        for (Character file : files) {
            for (Character rank : ranks) {
                int fileIdx = ChessBoard.getFileIdx(file);
                int rankIdx = ChessBoard.getRankIdx(rank);
                whiteControlledSquares[rankIdx][fileIdx] = 0;
            }
        }

        for (ChessPiece piece : whitePieces) {
            for (Character file : files) {
                for (Character rank : ranks) {
                    int fileIdx = ChessBoard.getFileIdx(file);
                    int rankIdx = ChessBoard.getRankIdx(rank);
                    if (piece.controlsSquare(file, rank)) {
                        whiteControlledSquares[rankIdx][fileIdx]++;
                    }
                }
            }
        }
    }

    public void refreshPiecesOnBoard() {
        //Nullify board
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

        refreshAllPieceMoves();
    }

    public boolean move(String command) {
        ChessPiece pieceToMove = null;
        ChessPiece.PieceType type = null;
        Boolean capture = false;
        Boolean promotion = false;
        Character originRank = null;
        Character originFile = null;
        Character destinationFile = null;
        Character destinationRank = null;
        ChessPiece.PieceType promotionType = null;
        String logStr = "ChessBoard::move() ";

        String pawnMoveStr = "^([a-h])(x([a-h]))?([1-8])(=?([kqrbn]))?";
        if (Pattern.matches(pawnMoveStr,command)) {
            Pattern p = Pattern.compile(pawnMoveStr);
            Matcher m = p.matcher(command);

            System.out.println("Pawn move specified!");

            if (m.matches()) {
                type = ChessPiece.PieceType.PAWN;

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
                ArrayList<ChessPiece> piecesToMove;
                if (whosTurn == ChessPiece.PieceColor.WHITE) {
                    piecesToMove = whitePieces;
                } else {
                    piecesToMove = blackPieces;
                }
                for (ChessPiece piece : piecesToMove) {
                    if (piece.type == type && piece.getFile() == originFile) {
                        Boolean pieceCanMakeMove = false;
                        if ( capture ) {
                            pieceCanMakeMove = piece.canCaptureAt(destinationFile, destinationRank);
                        } else {
                            pieceCanMakeMove = piece.canMoveTo(destinationFile, destinationRank);
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
        }

        /* Perform the specified move! */
        System.out.println(logStr);
        if (pieceToMove != null) {
            movePiece(pieceToMove, destinationFile, destinationRank);
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

        if(piece.canMoveTo(file, rank)) {
            if (destination != null && destination.color != piece.color) {
                /* remove enemy piece if it exists */
                removePieceAtPosition(file, rank);
            }
            piece.file = file;
            piece.rank = rank;
            refreshPiecesOnBoard();
            return true;
        }

        return false;
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

    public ChessPiece setPieceAtPosition(ChessPiece piece) {
        Integer fileIdx = getFileIdx(piece.file);
        Integer rankIdx = getRankIdx(piece.rank);
        chessPieces[rankIdx][fileIdx] = piece;

        System.out.println("ChessBoard::setPieceAtPosition() adding new " + piece.color + " " + piece.type + " at " + piece.file + piece.rank);

        // Add piece to the list of pieces for each team
        if (piece.color == ChessPiece.PieceColor.WHITE) {
            whitePieces.add(piece);
        } else {
            // if its not white, it must be black!
            blackPieces.add(piece);
        }

        refreshAllPieceMoves();

        return piece;
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
        Integer fileIdx = getFileIdx(file);
        Integer rankIdx = getRankIdx(rank);
        ChessPiece removePiece = null;

        if (fileIdx <= 7 && fileIdx >= 0 && rankIdx <= 7 && rankIdx >= 0) {
            removePiece = chessPieces[rankIdx][fileIdx];

            if (removePiece != null) {
                Logger.logStr("ChessPiece::removePieceAtPosition() Removing piece at " + file + rank);
                chessPieces[rankIdx][fileIdx] = null;
            } else {
                Logger.logStr("ChessPiece::removePieceAtPosition() No piece to remove at " + file + rank);
            }
        }

        // Need to search through white and black pieces to remove piece
        for (int i=0; i<whitePieces.size(); i++) {
            if (whitePieces.get(i).file == file && whitePieces.get(i).rank == rank) {
                // Found piece - now remove it
                whitePieces.remove(i);
            }
        }
        for (int i=0; i<blackPieces.size(); i++) {
            if (blackPieces.get(i).file == file && blackPieces.get(i).rank == rank) {
                // Found piece - now remove it
                blackPieces.remove(i);
            }
        }

        refreshAllPieceMoves();

        return removePiece;
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

    public final JComponent getGui() {
        return gui;
    }

}
