import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Reader {
    public static int rows, cols, pieceCount;
    private static List<Piece> extractedPieces = new ArrayList<>();
    private static char[][] board;
    private static boolean hasLeftExit = false;
    private static boolean hasRightExit = false;
    private static boolean hasTopExit = false;
    private static boolean hasBottomExit = false;
    private static int exitRow = -1;
    private static int exitCol = -1;
    public static boolean readInputFile(String filePath) {
        System.out.println("DEBUG: Starting to read file: " + filePath);
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String[] dimensions = br.readLine().split(" ");
            rows = Integer.parseInt(dimensions[0]);
            cols = Integer.parseInt(dimensions[1]);
            System.out.println("Rows: " + rows + ", Cols: " + cols);
            pieceCount = Integer.parseInt(br.readLine());
            pieceCount += 1; // + PRIMARY PIECE
            System.out.println("DEBUG: Total expected pieces: " + pieceCount);

            ArrayList<String> boardLines = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) continue;
                boardLines.add(line);
                System.out.println("DEBUG: Read board line: " + line);
            }
            System.out.println("DEBUG: Total board lines read: " + boardLines.size());

            findExitLocations(boardLines);

            if (hasTopExit) {
                boardLines.remove(0); // Skip first line
            }

            board = new char[rows][cols];
            for (int i = 0; i < rows; i++) {
                char[] rowChars = boardLines.get(i).toCharArray();
                for (int j = 0; j < cols; j++) {
                    if (hasLeftExit && j < rowChars.length - 1) {
                        board[i][j] = rowChars[j + 1];
                    } else if (!hasLeftExit && j < rowChars.length) {
                        board[i][j] = rowChars[j];
                    } else {
                        board[i][j] = '.';
                    }
                }
            }

            System.out.println("DEBUG: Board:");
            for (int i = 0; i < rows; i++) {
                System.out.println("DEBUG: " + new String(board[i]));
            }

            // Extract unique pieces
            HashSet<Character> uniquePieces = new HashSet<>();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (board[i][j] != '.' && board[i][j] != 'K' && board[i][j] != ' ') {
                        uniquePieces.add(board[i][j]);
                    }
                }
            }

            System.out.println("DEBUG: Unique pieces: " + uniquePieces);

            List<Piece> pieces = new ArrayList<>();
            for (char pieceChar : uniquePieces) {
                System.out.println("DEBUG: Extracting piece: " + pieceChar);
                Piece extractedPiece = extractPiece(pieceChar);
                if (extractedPiece != null) {
                    pieces.add(extractedPiece);
                    System.out.println("DEBUG: Added piece " + pieceChar);
                } else {
                    System.out.println("DEBUG: Failed to extract piece " + pieceChar);
                }
            }

            extractedPieces = pieces;

            System.out.println("DEBUG: Total pieces: " + pieces.size() + "/" + pieceCount);
            return pieces.size() == pieceCount;
        } catch (Exception e) {
            System.out.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static Piece extractPiece(char pieceChar) {
        int minRow = rows, maxRow = -1, minCol = cols, maxCol = -1;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j] == pieceChar) {
                    minRow = Math.min(minRow, i);
                    maxRow = Math.max(maxRow, i);
                    minCol = Math.min(minCol, j);
                    maxCol = Math.max(maxCol, j);
                }
            }
        }

        int height = maxRow - minRow + 1;
        int width = maxCol - minCol + 1;

        boolean isHorizontal = width > height;
        int length = isHorizontal ? width : height;

        return new Piece(pieceChar, minRow, minCol, length, isHorizontal);
    }

    public static State getInitialState() {
        if (extractedPieces == null || extractedPieces.isEmpty()) {
            System.out.println("Initial State gagal dibuat");
            return null;
        }

        // Sort Primary piece at 0
        extractedPieces.sort((a, b) -> {
            if (a.getId() == 'P') return -1;
            if (b.getId() == 'P') return 1;
            return Character.compare(a.getId(), b.getId());
        });

        return new State(extractedPieces, 0, 0, null, cols, rows);
    }

    private static void findExitLocations(ArrayList<String> boardLines) {
        for (int i = 0; i < boardLines.size(); i++) {
            String line = boardLines.get(i);
            for (int j = 0; j < line.length(); j++) {
                if (line.charAt(j) == 'K') {
                    if (i == 0) { // Top
                        hasTopExit = true;
                        exitRow = 0;
                        exitCol = j;
                        System.out.println("DEBUG: Exit di atas: " + exitCol);
                    } else if (i == boardLines.size() - 1) { // Bottom
                        hasBottomExit = true;
                        exitRow = rows - 1;
                        exitCol = j;
                        System.out.println("DEBUG: Exit di bawah: " + exitCol);
                    } else if (j == 0) { // Left
                        hasLeftExit = true;
                        exitRow = i;
                        exitCol = 0;
                        System.out.println("DEBUG: Exit di kiri: " + exitRow);
                    } else if (j == line.length() - 1) { // Right
                        hasRightExit = true;
                        exitRow = i;
                        exitCol = cols - 1;
                        System.out.println("DEBUG: Exit di kanan: " + exitRow);
                    }
                }
            }
        }

        // Default to right exit if no 'K' is found
        if (!hasLeftExit && !hasRightExit && !hasTopExit && !hasBottomExit) {
            System.out.println("Exit tidak ditemukan, program akan keluar.");
            System.exit(0);
        } else {
            System.out.println("DEBUG: Exit [" + exitRow + "," + exitCol + "]");
        }
    }

    public static boolean hasLeftExit() { return hasLeftExit; }
    public static boolean hasRightExit() { return hasRightExit; }
    public static boolean hasTopExit() { return hasTopExit; }
    public static boolean hasBottomExit() { return hasBottomExit; }
    public static int getExitRow() { return exitRow; }
    public static int getExitCol() { return exitCol; }
}

