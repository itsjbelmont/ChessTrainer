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
            while (true) {
                System.out.println("");
                chessBoard.printChessBoard();
                if (chessBoard.getWhosTurn() == ChessPiece.PieceColor.WHITE) {
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

                if (command.equals("help")) {
                    System.out.println("\nPossible Commands:");
                    System.out.println("\trefreshAllPieceMoves");
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
                }

                if (command.equals("print black pieces")) {
                    for(ChessPiece piece: chessBoard.getBlackPieces()) {
                        System.out.println(piece);
                    }
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

                if (!chessBoard.verifyMoveSyntax(command)) {
                    System.out.println("\tMove syntax could not be verified!");
                } else {
                    System.out.println("\tMove syntax verified!");
                }

            }
        } catch (IOException e) {
            return;
        }
    }

}