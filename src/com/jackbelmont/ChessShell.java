package com.jackbelmont;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChessShell {
    ChessBoard chessBoard;
    TestAssist testAssist;

    final String ERROR_STR = ConsoleColors.RED + "ERROR: " + ConsoleColors.RESET;

    ChessShell() {
        // Constructor
        chessBoard = new ChessBoard(ChessBoard.GameMode.TWO_PLAYER);
        testAssist = new TestAssist(chessBoard);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////// SHELL FUNCTIONS //////////////////////////////////////////////////////////

    public void help(ArrayList<String> possibleCommands) {
        System.out.println("\n" + ConsoleColors.BLUE + "ChessShell() commands currently available:" + ConsoleColors.RESET);
        for (String command : possibleCommands) {
            System.out.println("\t" + command);
        }
        System.out.println(ConsoleColors.YELLOW + "NOTE: " + ConsoleColors.RESET + "any command that doesnt match one of the above will be treated as a move command\n");

    }

    public void exit() {
        System.out.println("Exiting ChessShell.");
        System.exit(0);
    }

    public void load(String fileName) {
        ChessBoard tempBoard = ChessBoard.loadChessBoardFromFile(fileName);
        if (tempBoard != null) {
            System.out.println("load(" + fileName + ") " + ConsoleColors.GREEN + "SUCCESS!" + ConsoleColors.RESET);
            this.chessBoard = tempBoard;
        } else {
            System.out.println("load(" + fileName + ") " + ConsoleColors.RED + "FAILED!" + ConsoleColors.RESET);
        }
    }

    public void save(String fileName) {
        Boolean success = chessBoard.saveToFile(fileName);
        if (success) {
            System.out.println("save(" + fileName + ") " + ConsoleColors.GREEN + "SUCCESS!" + ConsoleColors.RESET);
        } else {
            System.out.println("save(" + fileName + ") " + ConsoleColors.RED + "FAILED!" + ConsoleColors.RESET);
        }
    }

    ///////////////////////////////////// END SHELL FUNCTIONS //////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////// SHELL SETUP ///////////////////////////////////////////////////////
    public void test() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("\n\n\nRunning ChessShell::test()  Enter 'help' for list of possible commands");
            while (true) {
                System.out.println("");
                chessBoard.printChessBoard();
                if (chessBoard.getWhosTurn() == ChessPiece.PieceColor.WHITE) {
                    System.out.print("ChessShell::test() enter command: ");
                } else {
                    System.out.print("ChessShell::test() enter command: ");
                }

                String command = reader.readLine();
                if (command.equals("exit")) {
                    return;
                }

                if (command.isEmpty() || command.isBlank()) {
                    System.out.println("\tPlease specify command!");
                    continue;
                }

                if (command.equals("help")) {
                    System.out.println("\nPossible Commands:");
                    System.out.println("\tmove chessNotationCommand");
                    System.out.println("\trefreshAllPieceMoves");
                    System.out.println("\tprint white pieces");
                    System.out.println("\tprint black pieces");
                    System.out.println("\tprint white controlled squares");
                    System.out.println("\tprint black controlled squares");
                    System.out.println("\tprint controlled squares a1");
                    System.out.println("\tprint possible moves a1");
                    System.out.println("\tprint possible captures a1");
                    System.out.println("\tremove a1");
                    System.out.println("\tadd white|black pawn|rook|knight|bishop|queen|king at a1");
                    continue;
                }

                if (command.equals("print white pieces")) {
                    for(ChessPiece piece: chessBoard.getWhitePieces()) {
                        System.out.println(piece);
                    }
                    continue;
                }

                if (command.equals("print black pieces")) {
                    for(ChessPiece piece: chessBoard.getBlackPieces()) {
                        System.out.println(piece);
                    }
                    continue;
                }

                if (command.equals("print black controlled squares")) {
                    chessBoard.printControlledSquares(ChessPiece.PieceColor.BLACK);
                    continue;
                }

                if (command.equals("print white controlled squares")) {
                    chessBoard.printControlledSquares(ChessPiece.PieceColor.WHITE);
                    continue;
                }
                /*
                if (Pattern.matches("print moves [a-h][1-8]", command)) {
                    Pattern p = Pattern.compile("print moves ([a-h][1-8])");
                    Matcher m = p.matcher(command);

                    if (m.matches()){
                        String position = m.group(1);
                        Character file = position.charAt(0);
                        Character rank = position.charAt(1);
                        ChessPiece piece = chessBoard.getPieceAtPosition(file, rank);
                        if (piece != null) {
                            System.out.println("Printing piece moves at: " + position);
                            piece.printPossibleMoves();
                        } else {
                            System.out.println("Square is empty!");
                        }
                    }
                    continue;
                }*/

                if(Pattern.matches("move [a-h][1-8] [a-h][1-8]", command)) {
                    Pattern p = Pattern.compile("move ([a-h][1-8]) ([a-h][1-8])");
                    Matcher m = p.matcher(command);
                    if (m.matches()) {
                        String piecePosition = m.group(1);
                        Character pieceFile = piecePosition.charAt(0);
                        Character pieceRank = piecePosition.charAt(1);

                        String destPosition = m.group(2);
                        Character destFile = destPosition.charAt(0);
                        Character destRank = destPosition.charAt(1);
                        ChessPiece piece = chessBoard.getPieceAtPosition(pieceFile, pieceRank);
                        if (!chessBoard.movePiece(piece, destFile, destRank)) {
                            System.out.println("ERROR: Couldnt move piece");
                        }
                        continue;
                    }
                }

                if (Pattern.matches("remove [a-h][1-8]", command)) {
                    Pattern p = Pattern.compile("remove ([a-h][1-8])");
                    Matcher m = p.matcher(command);

                    if (m.matches()) {
                        String position = m.group(1);
                        Character file = position.charAt(0);
                        Character rank = position.charAt(1);
                        chessBoard.removePieceAtPosition(file, rank);
                    }
                    continue;
                }

                if (Pattern.matches("^move (.*)$", command)) {
                    Pattern p = Pattern.compile("^move (.*)$");
                    Matcher m = p.matcher(command);
                    if (m.matches()) {
                        chessBoard.move(m.group(1));
                    }
                    continue;
                }

                if (Pattern.matches("[a-h][1-8] can move to [a-h][1-8]", command)) {
                    Pattern p = Pattern.compile("([a-h][1-8]) can move to ([a-h][1-8])");
                    Matcher m = p.matcher(command);
                    if (m.matches()) {
                        Character startFile = m.group(1).charAt(0);
                        Character startRank = m.group(1).charAt(1);
                        Character destFile = m.group(2).charAt(0);
                        Character destRank = m.group(2).charAt(1);
                        ChessPiece piece = chessBoard.getPieceAtPosition(startFile, startRank);
                        if (piece == null) {
                            System.out.println("ChessShell::test(): can  move to: origin square is null!");
                        } else {
                            piece.canMoveTo(destFile, destRank, chessBoard);
                        }
                    }
                    continue;
                }

                if (Pattern.matches("[a-h][1-8] can capture at [a-h][1-8]", command)) {
                    Pattern p = Pattern.compile("([a-h][1-8]) can capture at ([a-h][1-8])");
                    Matcher m = p.matcher(command);
                    if (m.matches()) {
                        Character startFile = m.group(1).charAt(0);
                        Character startRank = m.group(1).charAt(1);
                        Character destFile = m.group(2).charAt(0);
                        Character destRank = m.group(2).charAt(1);
                        ChessPiece piece = chessBoard.getPieceAtPosition(startFile, startRank);
                        if (piece == null) {
                            System.out.println("ChessShell::test(): can  move to: origin square is null!");
                        } else {
                            piece.canCaptureAt(destFile, destRank, chessBoard);
                        }
                    }
                    continue;
                }

                if (Pattern.matches("[a-h][1-8] controls square [a-h][1-8]", command)) {
                    Pattern p = Pattern.compile("([a-h][1-8]) controls square ([a-h][1-8])");
                    Matcher m = p.matcher(command);
                    if (m.matches()) {
                        Character startFile = m.group(1).charAt(0);
                        Character startRank = m.group(1).charAt(1);
                        Character destFile = m.group(2).charAt(0);
                        Character destRank = m.group(2).charAt(1);
                        ChessPiece piece = chessBoard.getPieceAtPosition(startFile, startRank);
                        if (piece == null) {
                            System.out.println("ChessShell::test(): can  move to: origin square is null!");
                        } else {
                            piece.controlsSquare(destFile, destRank, chessBoard);
                        }
                    }
                    continue;
                }

                if (Pattern.matches("print controlled squares [a-h][1-8]", command)) {
                    Pattern p = Pattern.compile("print controlled squares ([a-h][1-8])");
                    Matcher m = p.matcher(command);
                    if (m.matches()) {
                        testAssist.printControledSquaresForPieceAt(m.group(1).charAt(0), m.group(1).charAt(1));
                    }
                }

                if (Pattern.matches("print possible moves [a-h][1-8]", command)) {
                    Pattern p = Pattern.compile("print possible moves ([a-h][1-8])");
                    Matcher m = p.matcher(command);
                    if (m.matches()) {
                        testAssist.printPossibleMovesForPieceAt(m.group(1).charAt(0), m.group(1).charAt(1));
                    }
                }

                if (Pattern.matches("print possible captures [a-h][1-8]", command)) {
                    Pattern p = Pattern.compile("print possible captures ([a-h][1-8])");
                    Matcher m = p.matcher(command);
                    if (m.matches()) {
                        testAssist.printPossibleCapturesForPieceAt(m.group(1).charAt(0), m.group(1).charAt(1));
                    }
                }

                if (Pattern.matches("[a-h][1-8] can short castle", command)) {
                    Pattern p = Pattern.compile("([a-h][1-8]) can short castle");
                    Matcher m = p.matcher(command);
                    if (m.matches()) {
                        ChessPiece king = chessBoard.getPieceAtPosition(m.group(1).charAt(0), m.group(1).charAt(1));
                        if (king == null || king.type != ChessPiece.PieceType.KING) {
                            System.out.println("ChessShell::test() Can not check short castle unless the piece specified is a king!");
                            continue;
                        } else {
                            king.canShortCastle(chessBoard);
                        }

                    }
                }

                if (Pattern.matches("[a-h][1-8] can long castle", command)) {
                    Pattern p = Pattern.compile("([a-h][1-8]) can long castle");
                    Matcher m = p.matcher(command);
                    if (m.matches()) {
                        ChessPiece king = chessBoard.getPieceAtPosition(m.group(1).charAt(0), m.group(1).charAt(1));
                        if (king == null || king.type != ChessPiece.PieceType.KING) {
                            System.out.println("ChessShell::test() Can not check long castle unless the piece specified is a king!");
                            continue;
                        } else {
                            king.canLongCastle(chessBoard);
                        }

                    }
                }

                if (Pattern.matches("add (white|black) (pawn|rook|knight|bishop|king|queen) at [a-h][1-8]", command)) {
                    Pattern p = Pattern.compile("add (white|black) (pawn|rook|knight|bishop|king|queen) at ([a-h][1-8])");
                    Matcher m = p.matcher(command);
                    if (m.matches()) {
                        String position = m.group(3);
                        Character file = position.charAt(0);
                        Character rank = position.charAt(1);

                        ChessPiece.PieceColor color;
                        if (m.group(1).equals("white")) {
                            color = ChessPiece.PieceColor.WHITE;
                        } else if (m.group(1).equals("black")) {
                            color = ChessPiece.PieceColor.BLACK;
                        } else {
                            System.out.println("Pattern match couldnt determine color to add piece");
                            continue;
                        }

                        switch(m.group(2)) {
                            case "pawn":
                                System.out.println("Adding pawn at " + m.group(3));
                                chessBoard.setPieceAtPosition(new Pawn(color, file, rank));
                                break;
                            case "rook":
                                System.out.println("Adding rook at " + m.group(3));
                                chessBoard.setPieceAtPosition(new Rook(color, file, rank));
                                break;
                            case "knight":
                                System.out.println("Adding knight at " + m.group(3));
                                chessBoard.setPieceAtPosition(new Knight(color, file, rank));
                                break;
                            case "bishop":
                                System.out.println("Adding bishop at " + m.group(3));
                                chessBoard.setPieceAtPosition(new Bishop(color, file, rank));
                                break;
                            case "king":
                                System.out.println("Adding king at " + m.group(3));
                                chessBoard.setPieceAtPosition(new King(color, file, rank));
                                break;
                            case "queen":
                                System.out.println("Adding queen at " + m.group(3));
                                chessBoard.setPieceAtPosition(new Queen(color, file, rank));
                                break;
                            default:
                                System.out.println("Pattern match failed for adding a piece");
                                break;
                        }
                    }
                }

            }
        } catch (IOException e) {
            System.out.println("ChessShell::test() threw an exception!");
            e.printStackTrace();
        }
    }

    public void playOnCommandLine() {
        ArrayList<String> possibleCommands = new ArrayList<>();
        possibleCommands.add("save");
        possibleCommands.add("load");
        possibleCommands.add("exit"); //Redundant in run()
        this.run(possibleCommands);
    }

    ////////////////////////////////////////////// END SHELL SETUP /////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////// Main Shell //////////////////////////////////////////////////////////

    public void run(ArrayList<String> possibleCommands) {
        // WARNING: if the command in possibleCommands takes any input it must be a single type String
        //          since this is a text based shell

        // Make sure possibleCommands always has an exit command for obvious reasons
        if (!possibleCommands.contains("exit")) {
            possibleCommands.add("exit");
        }

        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            // Shell execution loop
            while(true) {
                // Print the chess board
                chessBoard.printChessBoard();

                // Print prompt
                if (chessBoard.getWhosTurn() == ChessPiece.PieceColor.WHITE) {
                    System.out.print(ConsoleColors.WHITE_PIECE + "WHITE" + ConsoleColors.RESET + " move: ");
                } else {
                    System.out.print(ConsoleColors.BLACK_PIECE + "BLACK" + ConsoleColors.RESET + " move: ");
                }

                String command = reader.readLine();

                // check for a command
                try {
                    //Split command by whitespace
                    String[] commandArray = command.split("\\s+");

                    if (commandArray.length < 1) {
                        continue;
                    }

                    String theCommand = commandArray[0];
                    String argString = null;

                    // Check for help command
                    if (theCommand.equalsIgnoreCase("help")) {
                        this.help(possibleCommands);
                        continue;
                    }

                    // Check if the move is in the possibleCommands array
                    // NOTE: a chess move is not considered a command
                    Boolean validCommand = false;
                    if (possibleCommands.contains(theCommand)) {
                        validCommand = true;
                    }

                    if (validCommand) {

                        try {
                            if (commandArray.length > 1) {
                                // Function expects an input
                                argString = "";
                                for (int argNum = 1; argNum < commandArray.length; argNum++) {
                                    argString = argString + commandArray[argNum];
                                    if (argNum < commandArray.length - 1) {
                                        argString = argString + " ";
                                    }
                                }

                                this.getClass().getDeclaredMethod(theCommand, String.class).invoke(this, argString);
                                continue;
                            } else {
                                // Function does not expect an input
                                this.getClass().getDeclaredMethod(theCommand).invoke(this);
                                continue;
                            }

                        } catch (Exception e) {
                            System.out.println(ERROR_STR + "Invalid command: " + theCommand);
                            continue;
                        }
                    }
                } catch (Exception e) {
                    System.out.println(ERROR_STR + "could not split command!");
                    continue;
                }

                //If we got here the user probably specified a chess move
                chessBoard.move(command);

            } // while(true)
        } catch (Exception e) {
            System.out.println(ERROR_STR + "Unknown exception thrown in ChessShell()::run()");
            System.exit(1);
        }
    }
}
