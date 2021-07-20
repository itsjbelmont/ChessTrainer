package com.jackbelmont;

public class TestAssist {
    private ChessBoard board = null;

    TestAssist(ChessBoard board) {
        this.board = board;
    }

    public void printControledSquaresForPieceAt(Character file, Character rank) {
        ChessPiece piece = this.board.getPieceAtPosition(file, rank);
        if (piece == null) {
            Logger.logStr("TestAssist::printControledSquaresForPieceAt(" + file + "," + rank + "): FAIL: no piece on the square");
            return;
        }
        Boolean previousLogginStatus = Logger.disableConsoleLogging();
        try {
            StringBuilder outputBoard = new StringBuilder();
            Character files[] = ChessBoard.files;
            Character ranks[] = {'8', '7', '6', '5', '4', '3', '2', '1'};
            int curFile = ChessBoard.getFileIdx(piece.file);
            int curRank = ChessBoard.getRankIdx(piece.rank);

            outputBoard.append("  " + piece.color + " " + piece.type + " at [" + piece.file + piece.rank + "]:\n");
            outputBoard.append("  |-----|-----|-----|-----|-----|-----|-----|-----|\n");
            for (Character r : ranks) {
                int rankIdx = ChessBoard.getRankIdx(rank);
                outputBoard.append(ConsoleColors.RED + (rankIdx + 1) + ConsoleColors.RESET + " |");
                for (Character f : files) {

                    if (f == piece.file && r == piece.rank) {
                        outputBoard.append("  O  ");
                    } else if (piece.controlsSquare(f, r, this.board)) {
                        outputBoard.append("  X  ");
                    } else {
                        outputBoard.append("     ");
                    }
                    outputBoard.append("|");
                }
                outputBoard.append("\n");
                outputBoard.append("  |-----|-----|-----|-----|-----|-----|-----|-----|\n");
            }
            outputBoard.append(ConsoleColors.RED + "     a     b     c     d     e     f     g     h   \n" + ConsoleColors.RESET);
            System.out.println(outputBoard);
        } catch (Exception e){
            if (previousLogginStatus) {
                Logger.enableConsoleLogging();
            }
            Logger.logStr("Exception thrown while printing controlled squares for " + file + rank);
        }
        if (previousLogginStatus) {
            Logger.enableConsoleLogging();
        }
    }

    public void printPossibleMovesForPieceAt(Character file, Character rank) {
        ChessPiece piece = this.board.getPieceAtPosition(file, rank);
        if (piece == null) {
            Logger.logStr("TestAssist::printPossibleMovesForPieceAt(" + file + "," + rank + "): FAIL: no piece on the square");
            return;
        }
        Boolean previousLogginStatus = Logger.disableConsoleLogging();
        try {
            StringBuilder outputBoard = new StringBuilder();
            Character files[] = ChessBoard.files;
            Character ranks[] = {'8', '7', '6', '5', '4', '3', '2', '1'};
            int curFile = ChessBoard.getFileIdx(piece.file);
            int curRank = ChessBoard.getRankIdx(piece.rank);

            outputBoard.append("  " + piece.color + " " + piece.type + " at [" + piece.file + piece.rank + "]:\n");
            outputBoard.append("  |-----|-----|-----|-----|-----|-----|-----|-----|\n");
            for (Character r : ranks) {
                int rankIdx = ChessBoard.getRankIdx(rank);
                outputBoard.append(ConsoleColors.RED + (rankIdx + 1) + ConsoleColors.RESET + " |");
                for (Character f : files) {

                    if (f == piece.file && r == piece.rank) {
                        outputBoard.append("  O  ");
                    } else if (piece.canMoveTo(f, r, this.board)) {
                        outputBoard.append("  X  ");
                    } else {
                        outputBoard.append("     ");
                    }
                    outputBoard.append("|");
                }
                outputBoard.append("\n");
                outputBoard.append("  |-----|-----|-----|-----|-----|-----|-----|-----|\n");
            }
            outputBoard.append(ConsoleColors.RED + "     a     b     c     d     e     f     g     h   \n" + ConsoleColors.RESET);
            System.out.println(outputBoard);
        } catch (Exception e){
            if (previousLogginStatus) {
                Logger.enableConsoleLogging();
            }
            Logger.logStr("TestAssist::printPossibleMovesForPieceAt(" + file + "," + rank + "): Exception thrown while printing movable squares for " + file + rank);
        }
        if (previousLogginStatus) {
            Logger.enableConsoleLogging();
        }
    }
    public void printPossibleCapturesForPieceAt(Character file, Character rank) {
        ChessPiece piece = this.board.getPieceAtPosition(file, rank);
        if (piece == null) {
            Logger.logStr("TestAssist::printPossibleCapturesForPieceAt(" + file + "," + rank + "): FAIL: no piece on the square");
            return;
        }
        Boolean previousLogginStatus = Logger.disableConsoleLogging();
        try {
            StringBuilder outputBoard = new StringBuilder();
            Character files[] = ChessBoard.files;
            Character ranks[] = {'8', '7', '6', '5', '4', '3', '2', '1'};
            int curFile = ChessBoard.getFileIdx(piece.file);
            int curRank = ChessBoard.getRankIdx(piece.rank);

            outputBoard.append("  " + piece.color + " " + piece.type + " at [" + piece.file + piece.rank + "]:\n");
            outputBoard.append("  |-----|-----|-----|-----|-----|-----|-----|-----|\n");
            for (Character r : ranks) {
                int rankIdx = ChessBoard.getRankIdx(rank);
                outputBoard.append(ConsoleColors.RED + (rankIdx + 1) + ConsoleColors.RESET + " |");
                for (Character f : files) {

                    if (f == piece.file && r == piece.rank) {
                        outputBoard.append("  O  ");
                    } else if (piece.canCaptureAt(f, r, this.board)) {
                        outputBoard.append("  X  ");
                    } else {
                        outputBoard.append("     ");
                    }
                    outputBoard.append("|");
                }
                outputBoard.append("\n");
                outputBoard.append("  |-----|-----|-----|-----|-----|-----|-----|-----|\n");
            }
            outputBoard.append(ConsoleColors.RED + "     a     b     c     d     e     f     g     h   \n" + ConsoleColors.RESET);
            System.out.println(outputBoard);
        } catch (Exception e){
            if (previousLogginStatus) {
                Logger.enableConsoleLogging();
            }
            Logger.logStr("TestAssist::printPossibleCapturesForPieceAt(" + file + "," + rank + "): Exception thrown while printing movable squares for " + file + rank);
        }
        if (previousLogginStatus) {
            Logger.enableConsoleLogging();
        }
    }

}
