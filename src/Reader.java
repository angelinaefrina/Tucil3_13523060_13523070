import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Reader {
    public static int rows, cols, pieceCount;
    private static List<Piece> extractedPieces = new ArrayList<>();
    private static char[][] board;
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
            board = new char[rows][cols];

            ArrayList<String> boardLines = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) continue;
                boardLines.add(line);
                System.out.println("DEBUG: Read board line: " + line);
            }

            System.out.println("DEBUG: Total board lines read: " + boardLines.size());
            for (int i = 0; i < Math.min(rows, boardLines.size()); i++) {
                char[] rowChars = boardLines.get(i).toCharArray();
                for (int j = 0; j < Math.min(cols, rowChars.length); j++) {
                    board[i][j] = rowChars[j];
                }
            }

            System.out.println("DEBUG: Board:");
            for (int i = 0; i < rows; i++) {
                System.out.println("DEBUG: " + new String(board[i]));
            }

            HashSet<Character> uniquePieces = new HashSet<>();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (board[i][j] != '.' && board[i][j] != 'K') {
                        uniquePieces.add(board[i][j]);
                    }
                }
            }
            
            System.out.println("DEBUG: Amount of pieces: " + uniquePieces);

            List<Piece> pieces = new ArrayList<>();
            for (char pieceChar : uniquePieces) {
                System.out.println("DEBUG: Extracting piece: " + pieceChar);
                Piece extractedPiece = extractPiece(pieceChar);
                if (extractedPiece != null) {
                    pieces.add(extractedPiece);
                    System.out.println("DEBUG: Added piece " + pieceChar + " to list");
                } else {
                    System.out.println("DEBUG: Failed to extract piece " + pieceChar);
                }
            }

            extractedPieces = pieces;


            System.out.println("DEBUG: Total pieces extracted: " + pieces.size() + ", Expected: " + pieceCount);
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
            System.out.println("DEBUG: Cannot create initial state");
            return null;
        }

        // Sort Primary piece at 0
        extractedPieces.sort((a, b) -> {
            if (a.getId() == 'P') return -1;
            if (b.getId() == 'P') return 1;
            return Character.compare(a.getId(), b.getId());
        });

        return new State(extractedPieces, 0, 0, null, cols);
    }

}

