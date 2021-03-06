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

    final String ERROR = ConsoleColors.RED + "ERROR: " + ConsoleColors.RESET;
    final String FAIL = ConsoleColors.RED + "FAIL: " + ConsoleColors.RESET;
    final String SUCCESS = ConsoleColors.GREEN + "SUCCESS: " + ConsoleColors.RESET;

    ChessShell() {
        // Constructor
        chessBoard = new ChessBoard(ChessBoard.GameMode.TWO_PLAYER);
        testAssist = new TestAssist(chessBoard);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////// ASSISTING FUNCTIONS //////////////////////////////////////////////////////

    public void printSuccess(String message) {
        System.out.println(SUCCESS + message);
    }

    public void printFail(String message) {
        System.out.println(FAIL + message);
    }

    public void printError(String message) {
        System.out.println(ERROR + message);
    }

    public String waitForUserInput(String prompt) {
        System.out.print(prompt);
        String input;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            input = reader.readLine();
        } catch (Exception e){
            input = "";
        }
        return input;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////// SHELL FUNCTIONS //////////////////////////////////////////////////////////
    // NOTE: All shell functions MUST take a single string argument
    // NOTE: All shell functions should return Boolean true for success or Boolean false for failure

    public void help(ArrayList<String> possibleCommands) {
        System.out.println("\n" + ConsoleColors.BLUE + "ChessShell() commands currently available:" + ConsoleColors.RESET);
        for (String command : possibleCommands) {
            System.out.println("\t" + command);
        }
        System.out.println(ConsoleColors.YELLOW + "NOTE: " + ConsoleColors.RESET + "any command that doesnt match one of the above will be treated as a move command");
        System.out.println(ConsoleColors.YELLOW + "NOTE: " + ConsoleColors.RESET + "pass the argument '" + ConsoleColors.RED + "help" + ConsoleColors.RESET + "' to any command for input expectations\n");
        System.out.println("Chess Notation Guide: " + "https://en.wikipedia.org/wiki/Chess_notation\n\n");
    }

    public void exit(String input) {
        System.out.println("Exiting ChessShell.");
        System.exit(0);
    }

    public Boolean load(String fileName) {
        String func = "load(" + fileName + ")";

        if (fileName.equals("help")) {
            System.out.println("\n" + ConsoleColors.GREEN + "load " + ConsoleColors.RESET + " filename");
            System.out.println("Function: Loads chess board from specified file");

            waitForUserInput("\nHit ENTER to continue: ");
            return true;
        }

        try {
            ChessBoard tempBoard = ChessBoard.loadChessBoardFromFile(fileName);

            if (tempBoard != null) {
                printSuccess(func);
                this.chessBoard = tempBoard;
                return true;
            } else {
                printFail(func);
                return false;
            }
        } catch (Exception e) {
            printError(func + " exception thrown " + e.toString());
            return false;
        }
    }

    public Boolean save(String fileName) {
        String func = "save(" + fileName + ")";

        if (fileName.equals("help")) {
            System.out.println("\n" + ConsoleColors.GREEN + "save " + ConsoleColors.RESET + " filename");
            System.out.println("Function: Saves chess board to specified file");

            waitForUserInput("\nHit ENTER to continue: ");
            return true;
        }

        try {
            Boolean success = chessBoard.saveToFile(fileName);
            if (success) {
                printSuccess(func);
                return true;
            } else {
                printFail(func);
                return false;
            }
        } catch (Exception e) {
            printError(func + " exception thrown " + e.toString());
            return false;
        }
    }

    public Boolean removePiece(String square) {
        // square in form: [a-h][1-8]
        String func = "removePiece(" + square + ")";

        if (square.equals("help")) {
            System.out.println("\n" + ConsoleColors.GREEN + "removePiece " + ConsoleColors.RESET + " [a-h][1-8]");
            System.out.println("Function: removes a piece from the board on square specified by file and rank");

            waitForUserInput("\nHit ENTER to continue: ");
            return false;
        }

        try {
            String squareMatchStr = "^([a-h][1-8])$";
            Pattern pattern = Pattern.compile(squareMatchStr);
            Matcher matcher = pattern.matcher(square);
            if (matcher.matches()) {
                String position = matcher.group(1);
                Character file = position.charAt(0);
                Character rank = position.charAt(1);
                ChessPiece removedPiece = chessBoard.removePieceAtPosition(file, rank);

                if (removedPiece != null) {
                    printSuccess(func);
                    return true;
                } else {
                    printFail(func + ": no piece on square");
                    return false;
                }
            } else {
                printFail(func);
                return false;
            }
        } catch (Exception e) {
            printError(func + " exception thrown: " + e.toString());
            return false;
        }
}

    public Boolean addPiece(String command) {
        String func = "addPiece(\"" + command + "\")";

        // Match command against this regEx
        String matchString = "(white|black) (pawn|rook|knight|bishop|king|queen) at ([a-h][1-8])";

        if (command.equals("help")) {
            System.out.println("\n" + ConsoleColors.GREEN + "addPiece" + ConsoleColors.RESET + " [white|black] [pawn|rook|knight|bishop|king|queen] at [a-h][1-8]");
            System.out.println("Function: Adds a new piece to the chess board");
            System.out.println("");

            waitForUserInput("\nHit ENTER to continue: ");
            return true;
        }

        try {
            command = command.toLowerCase(Locale.ROOT);


            // Setup this piece to add into chessboard
            ChessPiece newPiece = null;

            if (Pattern.matches(matchString, command)) {
                Pattern p = Pattern.compile(matchString);
                Matcher m = p.matcher(command);
                if (m.matches()) {
                    String position = m.group(3);
                    char file = position.charAt(0);
                    char rank = position.charAt(1);

                    ChessPiece.PieceColor color;
                    if (m.group(1).equals("white")) {
                        color = ChessPiece.PieceColor.WHITE;
                    } else if (m.group(1).equals("black")) {
                        color = ChessPiece.PieceColor.BLACK;
                    } else {
                        printFail("Pattern match couldnt determine piece color");
                        return false;
                    }

                    switch (m.group(2)) {
                        case "pawn":
                            newPiece = new Pawn(color, file, rank);
                            break;
                        case "rook":
                            newPiece = new Rook(color, file, rank);
                            break;
                        case "knight":
                            newPiece = new Knight(color, file, rank);
                            break;
                        case "bishop":
                            newPiece = new Bishop(color, file, rank);
                            break;
                        case "king":
                            newPiece = new King(color, file, rank);
                            break;
                        case "queen":
                            newPiece = new Queen(color, file, rank);
                            break;
                        default:
                            break;
                    }
                }
            }

            if (chessBoard.setPieceAtPosition(newPiece) != null) {
                printSuccess("added new piece: " + newPiece);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            printError(func + ": Exception thrown: " + e.toString());
            return false;
        }
    }

    public Boolean printControlledSquares(String input) {
        String func = "printControlledSquares(" + input + ")";

        try {
            String matchInput = "^([a-h][1-8])$";
            if (Pattern.matches(matchInput, input)) {
                Pattern p = Pattern.compile(matchInput);
                Matcher m = p.matcher(input);
                if (m.matches()) {
                    testAssist.printControledSquaresForPieceAt(m.group(1).charAt(0), m.group(1).charAt(1));
                    printSuccess(func);
                    return true;
                }
            }
        } catch (Exception e) {
            printError(func + ": Exception Thrown: " + e.toString());
        }
        return false;
    }

    public Boolean printMoves(String input) {
        String func = "printMoves(" + input + ")";

        try {
            String matchInput = "^([a-h][1-8])$";
            if (Pattern.matches(matchInput, input)) {
                Pattern p = Pattern.compile(matchInput);
                Matcher m = p.matcher(input);
                if (m.matches()) {
                    testAssist.printPossibleMovesForPieceAt(m.group(1).charAt(0), m.group(1).charAt(1));
                    printSuccess(func);
                    return true;
                }
            }
        } catch (Exception e) {
            printError(func + ": Exception Thrown: " + e.toString());
        }
        return false;
    }

    public Boolean printCaptures(String input) {
        String func = "printCaptures(" + input + ")";

        String matchInput = "^([a-h][1-8])$";
        try {
            if (Pattern.matches(matchInput, input)) {
                Pattern p = Pattern.compile(matchInput);
                Matcher m = p.matcher(input);
                if (m.matches()) {
                    testAssist.printPossibleCapturesForPieceAt(m.group(1).charAt(0), m.group(1).charAt(1));
                    printSuccess(func);
                    return true;
                }
            }
        } catch (Exception e) {
            printError(func + ": Exception Thrown: " + e.toString());
        }
        return false;
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

        possibleCommands.add("removePiece");
        possibleCommands.add("addPiece");
        possibleCommands.add("printMoves");
        possibleCommands.add("printCaptures");
        possibleCommands.add("printControlledSquares");

        // Important commands
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

        // Always put these commands in
        if (!possibleCommands.contains("exit")) {
            possibleCommands.add("exit");
        }

        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            // Shell execution loop
            while(true) {
                // Print the chess board
                System.out.println("");
                chessBoard.printChessBoard();

                // Print prompt
                String prompt = "";
                if (chessBoard.getWhosTurn() == ChessPiece.PieceColor.WHITE) {
                    prompt = ConsoleColors.WHITE_PIECE + "WHITE" + ConsoleColors.RESET + " move: ";
                } else {
                    prompt = ConsoleColors.BLACK_PIECE + "BLACK" + ConsoleColors.RESET + " move: ";
                }

                String command = waitForUserInput(prompt);

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
                            // Set up input for command
                            argString = "";
                            for (int argNum = 1; argNum < commandArray.length; argNum++) {
                                argString = argString + commandArray[argNum];
                                if (argNum < commandArray.length - 1) {
                                    argString = argString + " ";
                                }
                            }

                            this.getClass().getDeclaredMethod(theCommand, String.class).invoke(this, argString);
                            continue;
                        } catch (Exception e) {
                            printError("Invalid command [" + theCommand + "] Exception Thrown: " + e.toString());
                            continue;
                        }
                    }
                } catch (Exception e) {
                    printError("could not split command!" + e.toString());
                    continue;
                }

                //If we got here the user probably specified a chess move
                chessBoard.move(command);

            } // while(true)
        } catch (Exception e) {
            printError("Unknown exception thrown in ChessShell()::run(): " + e.toString());
            System.exit(1);
        }
    }
}
