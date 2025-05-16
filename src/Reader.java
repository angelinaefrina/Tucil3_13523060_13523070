import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Reader {
    private static int rows, cols, pieceCount;
    private static char[][] board;
    public static boolean readInputFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String[] dimensions = br.readLine().split(" ");
            rows = Integer.parseInt(dimensions[0]);
            cols = Integer.parseInt(dimensions[1]);
            System.out.println("Rows: " + rows + ", Cols: " + cols);
            pieceCount = Integer.parseInt(dimensions[2]); // AMOUNT OF NON-PRIMARY PIECES
            pieceCount += 1; // + PRIMARY PIECE
            board = new char[rows][cols];
            
            // Bekas Stima 1,
            // Edit how it reads the board with P = Primary Piece and K = Non-Primary Piece

            ArrayList<String> boardLines = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) continue;
                boardLines.add(line);
            }

            for (int i = 0; i < Math.min(rows, boardLines.size()); i++) {
                char[] rowChars = boardLines.get(i).toCharArray();
                for (int j = 0; j < Math.min(cols, rowChars.length); j++) {
                    board[i][j] = rowChars[j];
                }
            }

            HashSet<Character> uniquePieces = new HashSet<>();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (board[i][j] != '.' && board[i][j] != 'K') {
                        uniquePieces.add(board[i][j]);
                    }
                }
            }

            List<Piece> pieces = new ArrayList<>();
            for (char pieceChar : uniquePieces) {
                Piece extractedPiece = extractPiece(pieceChar);
                if (extractedPiece != null) {
                    pieces.add(extractedPiece);
                }
            }

            return pieces.size() == pieceCount;
        } catch (Exception e) {
            return false;
        }
    }

    private static Piece extractPiece(char pieceChar) {
        int minRow = rows;
        int maxRow = -1;
        int minCol = cols;
        int maxCol = -1;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j] == pieceChar) {
                    if (i < minRow) minRow = i;
                    if (i > maxRow) maxRow = i;
                    if (j < minCol) minCol = j;
                    if (j > maxCol) maxCol = j;
                }
            }
        }

        int pieceHeight = maxRow - minRow + 1;
        int pieceWidth = maxCol - minCol + 1;
        char[][] pieceMatrix = new char[pieceHeight][pieceWidth];
        
        for (char[] row : pieceMatrix) {
            Arrays.fill(row, '.');
        }

        for (int i = minRow; i <= maxRow; i++) {
            for (int j = minCol; j <= maxCol; j++) {
                if (board[i][j] == pieceChar) {
                    pieceMatrix[i - minRow][j - minCol] = pieceChar;
                }
            }
        }

        return new Piece(pieceChar, pieceMatrix);
    }
}