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

    /*
        refreshAllPieceMoves()
    */
    public void refreshAllPieceMoves() {
        //Refresh White Pieces
        for (ChessPiece piece : whitePieces) {
            piece.refreshPossibleMoves(this.chessPieces);
        }

        //Refresh Black Pieces
        for (ChessPiece piece : blackPieces) {
            piece.refreshPossibleMoves(this.chessPieces);
        }
    }

    /*
        verifyMoveSyntax(String moveStr)
            - moveStr: string representing a chess move such as Re7 or exd6

            - Function verifies that the moveStr can be made
    */
    public boolean verifyMoveSyntax(String moveStr) {
        // Make sure a non empty string was passed so we don't get indexing issues
        if (moveStr == null || moveStr.isEmpty() || moveStr.isBlank()) {
            Logger.logStr("\tverifyMoveSyntax(): moveStr is null or empty!");
            return false;
        }

        // Check special commands such as castling
        // Castle movement may be specified with 'O' or '0' depending on the standard
        if (moveStr.equals("O-O") || moveStr.equals("O-O-O") ||
            moveStr.equals("0-0") || moveStr.equals("0-0-0")){
            Logger.logStr("verifyMoveSyntax(): Verified castling movement");
            return true;
        }

        // get the piece type being commanded
        // can figure this out by taking the first character of the str
        ChessPiece.PieceType type;
        char firstChar = moveStr.charAt(0);
        switch (firstChar) {
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
                type = ChessPiece.PieceType.PAWN;
                break;
            case 'R':
                type = ChessPiece.PieceType.ROOK;
                break;
            case 'N':
                type = ChessPiece.PieceType.KNIGHT;
                break;
            case 'B':
                type = ChessPiece.PieceType.BISHOP;
                break;
            case 'Q':
                type = ChessPiece.PieceType.QUEEN;
                break;
            case 'K':
                type = ChessPiece.PieceType.KING;
                break;
            default:
                Logger.logStr("\tverifyMoveSyntax(): moveStr " + moveStr + " did not specify a piece properly");
                return false;
        }

        switch (type) {
            case PAWN:
                    if (Pattern.matches("^[a-h]x[a-h][2-7]$", moveStr)) {
                        // dont include moving to ranks 1 or 8 since back rank pawn movements should include a promotion
                        if (moveStr.charAt(0) == moveStr.charAt(2)) {
                            Logger.logStr("\tverifyMoveSyntax(): Pawns can not take directly in front of them!");
                            return false;
                        } else {
                            int startColIdx = getFileIdx(moveStr.charAt(0));
                            int endColIdx = getFileIdx(moveStr.charAt(2));
                            if (abs(startColIdx - endColIdx) != 1) {
                                Logger.logStr("\tverifyMoveSyntax(): Pawns can only capture pieces in the columns immediately bordering them");
                                return false;
                            }
                        }
                        Logger.logStr("\tverifyMoveSyntax(): Pawn capture specified");
                    } else if (Pattern.matches("^[a-h][18]$", moveStr)) {
                        // Any pawn movement to the back rank must include a promotion
                        Logger.logStr("\tverifyMoveSyntax(): Any pawn move to the back rank must include a promotion");
                        return false;
                    } else if (Pattern.matches("^[a-h]x[a-h][18]$", moveStr)) {
                        // Any pawn movement to the back rank must include a promotion
                        Logger.logStr("\tverifyMoveSyntax(): Any pawn move to the back rank must include a promotion");
                        return false;
                    } else if (Pattern.matches("^[a-h][2-7]", moveStr)) {
                        Logger.logStr("\tverifyMoveSyntax(): Pawn move specified");
                    } else if (Pattern.matches("^[a-h][18]=?[RNBQ]", moveStr)) {
                        Logger.logStr("\tverifyMoveSyntax(): Pawn promotion specified");
                    } else if (Pattern.matches("^[a-h]x[a-h][18]=?[RNBQ]", moveStr)) {
                        int startColIdx = getFileIdx(moveStr.charAt(0));
                        int endColIdx = getFileIdx(moveStr.charAt(2));
                        if (abs(startColIdx - endColIdx) != 1) {
                            Logger.logStr("\tverifyMoveSyntax(): Pawns can only capture pieces in the columns immediately bordering them");
                            return false;
                        }
                        Logger.logStr("\tverifyMoveSyntax(): Pawn capture with promotion specified");
                    } else {
                        // If we didnt match the string to a valid pawn move yet, we must have a invalid syntax
                        Logger.logStr("\tverifyMoveSyntax(): Pawn move invalid syntax!");
                        return false;
                    }
                break;
            case ROOK:
            case KNIGHT:
            case BISHOP:
                break;
            case QUEEN:
            case KING:
                break;
            default:
                Logger.logStr("\tverifyMoveSyntax(): ERROR: Failed in switch statement on the PieceType");
        }

        // If we got here the syntax is valid
        // Next make sure the piece can make its move on the current board
        getPieceFromChessNotation(moveStr);

        return true; // success
    }

    /*
        getPieceFromChessNotation(String move)
            - move: chess move in chess notation
    */
    public ChessPiece getPieceFromChessNotation(String move) {
        ChessPiece returnPiece = null;

        // Get the list of pieces to search through
        ArrayList<ChessPiece> listOfPieces;
        if (whosTurn == ChessPiece.PieceColor.WHITE) {
            listOfPieces = whitePieces;
        } else {
            listOfPieces = blackPieces;
        }

        // Make sure a non empty string was passed so we don't get indexing issues
        if (move == null || move.isEmpty() || move.isBlank()) {
            Logger.logStr("\tgetPieceFromChessNotation(): move is null or empty!");
            return null;
        }

        // get the piece type being commanded
        // can figure this out by taking the first character of the str
        ChessPiece.PieceType type;
        char firstChar = move.charAt(0);
        switch (firstChar) {
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
                type = ChessPiece.PieceType.PAWN;
                break;
            case 'R':
                type = ChessPiece.PieceType.ROOK;
                break;
            case 'N':
                type = ChessPiece.PieceType.KNIGHT;
                break;
            case 'B':
                type = ChessPiece.PieceType.BISHOP;
                break;
            case 'Q':
                type = ChessPiece.PieceType.QUEEN;
                break;
            case 'K':
                type = ChessPiece.PieceType.KING;
                break;
            default:
                Logger.logStr("\tgetPieceFromChessNotation(): moveStr " + move + " did not specify a piece properly");
                return null;
        }

        for (ChessPiece piece : listOfPieces) {
            if (piece.type != type) {
                // We already know what type we are looking for so we can skip the pieces that dont fit the match
                continue;
            }

            switch (piece.type) {
                case PAWN:
                    //First character specifies the column that the pawn is in
                    if (piece.file != firstChar) {
                        continue;
                    } else {
                        char pieceRank = piece.getRank();
                        // How to determine which pawn with string manipulation when there are doubled pawns???
                        // How to determine which pawn when e2 e3 are doubled and the command is e4?

                        System.out.println(pieceRank);
                        System.out.println(piece);
                    }

                    break;
                case ROOK:
                case KNIGHT:
                case BISHOP:
                    break;
                case QUEEN:
                case KING:
                    break;
                default:
                    Logger.logStr("\tverifyMoveSyntax(): ERROR: Failed in switch statement on the PieceType");
            }
        }

        return null;
    }

    /*
        Print the chess board to the console
    */
    public void printChessBoard() {
        StringBuilder outputBoard = new StringBuilder();
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
                        outputBoard.append("  " + ConsoleColors.CYAN + aPiece.characterIdentifier + ConsoleColors.RESET + "  ");
                    } else {
                            outputBoard.append("  " + ConsoleColors.GREEN + aPiece.characterIdentifier + ConsoleColors.RESET + "  ");
                    }
                }
                outputBoard.append(ConsoleColors.RESET + "|");

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

    public void playOnCommandLine() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                printChessBoard();
                if (whosTurn == ChessPiece.PieceColor.WHITE) {
                    System.out.print("Enter white command: ");
                } else {
                    System.out.print("Enter black command: ");
                }

                String command = reader.readLine();
                if (command.equals("exit")) {
                    return;
                }

                if (command == null || command.isEmpty() || command.isBlank()) {
                    System.out.println("\tPlease specify command!");
                    continue;
                }

                if (Pattern.matches("print [a-h][1-8]", command)) {
                    Pattern p = Pattern.compile("print ([a-h][1-8])");
                    Matcher m = p.matcher(command);

                    if (m.matches()){
                        String position = m.group(1);
                        System.out.println("Printing piece moves at: " + position);
                        Character file = position.charAt(0);
                        Character rank = position.charAt(1);
                        ChessPiece piece = getPieceAtPosition(file, rank);
                        piece.printPossibleMoves();

                    }
                    continue;
                }

                if (!verifyMoveSyntax(command)) {
                    System.out.println("\tMove syntax could not be verified!");
                } else {
                    System.out.println("\tMove syntax verified!");
                }

            }
        } catch (IOException e) {
            return;
        }
    }

    /*
        Add a piece on the board
    */
    public ChessPiece setPieceAtPosition(ChessPiece piece) {
        Integer fileIdx = getFileIdx(piece.file);
        Integer rankIdx = getRankIdx(piece.rank);
        chessPieces[rankIdx][fileIdx] = piece;

        // Add piece to the list of pieces for each team
        if (piece.color == ChessPiece.PieceColor.WHITE) {
            whitePieces.add(piece);
        } else {
            // if its not white, it must be black!
            blackPieces.add(piece);
        }

        return piece;
    }

    public void setMyPieceColor(ChessPiece.PieceColor color) {
        this.myPieceColor = color;

        // if you change the board color you must re-initialize the board
        initializeChessPieces();
    }

    /*
        Get a chess piece off the board
    */
    public ChessPiece getPieceAtPosition(Character file, Character rank) {
        Integer fileIdx = getFileIdx(file);
        Integer rankIdx = getRankIdx(rank);

        if (fileIdx <= 7 && fileIdx >= 0 && rankIdx <= 7 && rankIdx >= 0) {
            return chessPieces[rankIdx][fileIdx];
        } else {
            return null;
        }

    }

    /*
        Remove a piece from the board
            - Return a pointer to the piece
    */
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
