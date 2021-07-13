package com.jackbelmont;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChessShell {
    ChessBoard chessBoard;

    ChessShell() {
        chessBoard = new ChessBoard(ChessBoard.GameMode.TWO_PLAYER);
    }

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

                if (command == null || command.isEmpty() || command.isBlank()) {
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
                    System.out.println("\tprint moves a1");
                    System.out.println("\tremove a1");
                    System.out.println("\tadd white|black pawn|rook|knight|bishop|queen|king at a1");
                    continue;
                }

                if (command.equals("refreshAllPieceMoves")) {
                    System.out.println("Refreshing all piece moves");
                    chessBoard.refreshAllPieceMoves();
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
                            color = null;
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
                    chessBoard.refreshAllPieceMoves();
                    continue;
                }

            }
        } catch (IOException e) {
            return;
        }
    }

}
