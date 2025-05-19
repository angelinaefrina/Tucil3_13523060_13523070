import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class State implements Comparable<State> {
    private final List<Piece> pieces;
    private final int cost;             // g(n): path cost
    private final int heuristic;        // h(n): estimated cost to goal
    private final State parent;         // path reconstruction
    private int cols;
    private int rows;

    public State(List<Piece> pieces, int cost, int heuristic, State parent, int cols, int rows) {
        this.pieces = new ArrayList<>();
        for (Piece p : pieces) {
            this.pieces.add(new Piece(p.getId(), p.getRow(), p.getCol(), p.getLength(), p.isHorizontal()));
        }
        this.cost = cost;
        this.heuristic = heuristic;
        this.parent = parent;
        this.cols = cols;
        this.rows = rows;

    }

    public List<Piece> getPieces() { return pieces; }
    public int getCost() { return cost; }
    public int getHeuristic() { return heuristic; }
    public int getTotalCost() { return cost + heuristic; }
    public State getParent() { return parent; }
    public int getCols() { return cols; }
    public int getRows() { return rows; }

    // Mengecek apakah state sekarang adalah goal state
    public boolean isGoal() {
        Piece primary = pieces.get(0);
        
        if (Reader.hasRightExit()) {
            int endCol = primary.getCol() + primary.getLength() - 1;
            return endCol == cols - 1 && primary.getRow() == Reader.getExitRow();
        } 
        else if (Reader.hasLeftExit()) {
            return primary.getCol() == 0 && primary.getRow() == Reader.getExitRow();
        }
        else if (Reader.hasTopExit()) {
            return primary.getRow() == 0 && primary.getCol() == Reader.getExitCol();
        }
        else if (Reader.hasBottomExit()) {
            int endRow = primary.getRow() + (primary.isHorizontal() ? 0 : primary.getLength() - 1);
            return endRow == this.rows - 1 && primary.getCol() == Reader.getExitCol();
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(State other) {
        return Integer.compare(this.getTotalCost(), other.getTotalCost());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof State)) return false;
        State other = (State) o;
        return this.getPiecesString().equals(other.getPiecesString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPiecesString());
    }

    private String getPiecesString() {
        StringBuilder sb = new StringBuilder();
        for (Piece p : pieces.stream().sorted(Comparator.comparing(Piece::getId)).toList()) {
            sb.append(p.getId()).append(p.getRow()).append(p.getCol());
        }
        return sb.toString();
    }

    public void printState(int rows, int cols) {
        char[][] grid = new char[rows][cols];
        for (char[] row : grid) Arrays.fill(row, '.');
        for (Piece p : pieces) {
            int r = p.getRow(), c = p.getCol();
            for (int i = 0; i < p.getLength(); i++) {
                if (p.isHorizontal()) grid[r][c + i] = p.getId();
                else grid[r + i][c] = p.getId();
            }
        }

        for (char[] row : grid) {
            System.out.println(new String(row));
        }
        System.out.println();
    }

    // Menghasilkan semua state yang mungkin dari state sekarang
    public List<State> generateNextStates(int rows, int cols) {
        List<State> nextStates = new ArrayList<>();

        for (int i = 0; i < pieces.size(); i++) {
            Piece p = pieces.get(i);

            // Gerakan
            for (int dir = -1; dir <= 1; dir += 2) {
                int newRow = p.getRow();
                int newCol = p.getCol();

                boolean canMove = true;

                if (p.isHorizontal()) {
                    newCol += dir;
                    for (int j = 0; j < p.getLength(); j++) {
                        int colToCheck = (dir == -1) ? newCol : newCol + p.getLength() - 1;
                        if (colToCheck < 0 || colToCheck >= cols || isOccupied(p, newRow, colToCheck)) {
                            canMove = false;
                            break;
                        }
                    }
                } else {
                    newRow += dir;
                    for (int j = 0; j < p.getLength(); j++) {
                        int rowToCheck = (dir == -1) ? newRow : newRow + p.getLength() - 1;
                        if (rowToCheck < 0 || rowToCheck >= rows || isOccupied(p, rowToCheck, newCol)) {
                            canMove = false;
                            break;
                        }
                    }
                }

                if (canMove) {
                    List<Piece> newPieces = new ArrayList<>();
                    for (int k = 0; k < pieces.size(); k++) {
                        if (k == i) {
                            // Move the piece
                            newPieces.add(p.move(dir));
                        } else {
                            newPieces.add(pieces.get(k).copy());
                        }
                    }

                    State next = new State(newPieces, this.cost + 1, 0, this, this.cols, this.rows);
                    nextStates.add(next);
                }
            }
        }

        return nextStates;
    }

    private boolean isOccupied(Piece movingPiece, int row, int col) {
        for (Piece other : pieces) {
            if (other == movingPiece) continue;

            int r = other.getRow();
            int c = other.getCol();
            for (int i = 0; i < other.getLength(); i++) {
                int rr = r, cc = c;
                if (other.isHorizontal()) cc += i;
                else rr += i;

                if (rr == row && cc == col) return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Piece p : pieces) {
            sb.append(p.getId()).append(p.getRow()).append(p.getCol()).append(";");
        }
        return sb.toString();
    }

    public char getMovedPiece() {
        if (parent != null) {
            for (int i = 0; i < pieces.size(); i++) {
                Piece currentPiece = pieces.get(i);
                Piece parentPiece = parent.getPieces().get(i);
                
                if (currentPiece.getRow() != parentPiece.getRow() || 
                    currentPiece.getCol() != parentPiece.getCol()) {
                    return currentPiece.getId();
                }
            }
        }
        return ' ';
    }

    public String getMovedPieceDirection() {
        String moveDirection = "";
        if (parent != null) {
            for (int i = 0; i < pieces.size(); i++) {
                Piece currentPiece = pieces.get(i);
                Piece parentPiece = parent.getPieces().get(i);
                
                if (currentPiece.getRow() != parentPiece.getRow() || 
                    currentPiece.getCol() != parentPiece.getCol()) {
                    // Menentukan arah gerakan piece
                    if (currentPiece.isHorizontal()) {
                        if (currentPiece.getCol() > parentPiece.getCol()) {
                            moveDirection = "Kanan";
                        } else {
                            moveDirection = "Kiri";
                        }
                    } else {
                        if (currentPiece.getRow() > parentPiece.getRow()) {
                            moveDirection = "Bawah";
                        } else {
                            moveDirection = "Atas";
                        }
                    }           
                }
            }
        }
        return moveDirection;
    }

    public String getBoard(int rows, int cols) {
        char[][] grid = new char[rows][cols];
        for (char[] row : grid) Arrays.fill(row, '.');
        char[][] parentGrid = new char[rows][cols];
        for (char[] row : parentGrid) Arrays.fill(row, '.');

        char movedPiece = getMovedPiece();
        final String BG_YELLOW = "\u001B[43m";

        // Membandingkan titik / sel kosong
        for (Piece p : pieces) {
            int r = p.getRow(), c = p.getCol();
            for (int i = 0; i < p.getLength(); i++) {
                if (p.isHorizontal()) grid[r][c + i] = p.getId();
                else grid[r + i][c] = p.getId();
            }
        }
        int x = -1, y = -1;
        if (parent != null) {
            for (Piece p : getParent().pieces) {
                int r = p.getRow(), c = p.getCol();
                for (int i = 0; i < p.getLength(); i++) {
                    if (p.isHorizontal()) parentGrid[r][c + i] = p.getId();
                    else parentGrid[r + i][c] = p.getId();
                }
            }
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (grid[i][j] == '.' && parentGrid[i][j] != '.') {
                        x = i;
                        y = j;
                    }
                }
            }
        }
        

        int exitLocation = 0;
        if (Reader.hasTopExit() || Reader.hasBottomExit()) {
            exitLocation = Reader.getExitCol();
        } else if (Reader.hasLeftExit() || Reader.hasRightExit()) {
            exitLocation = Reader.getExitRow();
        }

        StringBuilder sb = new StringBuilder();
        if (Reader.hasBottomExit()) {
            // Print board
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    char cell = grid[r][c];
                    if (cell == movedPiece) { // highlight piece yang gerak
                        sb.append(BG_YELLOW).append(getWarnaPiece(cell)).append(cell).append(WARNA_DEFAULT);
                    } else if (cell == '.' && r == x && c == y) {
                        sb.append(BG_YELLOW).append(cell).append(WARNA_DEFAULT);
                    } else {
                        sb.append(getWarnaPiece(cell)).append(cell).append(WARNA_DEFAULT);
                    }
                }
                sb.append("\n");
            }
            
            // Print bottom exit
            for (int i = 0; i < cols; i++) {
                if (i == exitLocation) {
                    sb.append(getWarnaPiece('K')).append("K").append(WARNA_DEFAULT);
                } else {
                    sb.append(" ");
                }
            }
            sb.append("\n");
        }
        else if (Reader.hasTopExit()) {
            // Print top exit
            for (int i = 0; i < cols; i++) {
                if (i == exitLocation) {
                    sb.append(getWarnaPiece('K')).append("K").append(WARNA_DEFAULT);
                } else {
                    sb.append(" ");
                }
            }
            sb.append("\n");
            
            // Print board
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    char cell = grid[r][c];
                    if (cell == movedPiece) { // highlight piece yang gerak
                        sb.append(BG_YELLOW).append(getWarnaPiece(cell)).append(cell).append(WARNA_DEFAULT);
                    } else if (cell == '.' && r == x && c == y) {
                        sb.append(BG_YELLOW).append(cell).append(WARNA_DEFAULT);
                    } else {
                        sb.append(getWarnaPiece(cell)).append(cell).append(WARNA_DEFAULT);
                    }
                }
                sb.append("\n");
            }
        }
        else if (Reader.hasLeftExit()) {
            // Print board with left exit
            for (int r = 0; r < rows; r++) {
                if (r == exitLocation) {
                    sb.append(getWarnaPiece('K')).append("K").append(WARNA_DEFAULT);
                } else {
                    sb.append(" ");
                }
                
                for (int c = 0; c < cols; c++) {
                    char cell = grid[r][c];
                    if (cell == movedPiece) { // highlight piece yang gerak
                        sb.append(BG_YELLOW).append(getWarnaPiece(cell)).append(cell).append(WARNA_DEFAULT);
                    } else if (cell == '.' && r == x && c == y) {
                        sb.append(BG_YELLOW).append(cell).append(WARNA_DEFAULT);
                    } else {
                        sb.append(getWarnaPiece(cell)).append(cell).append(WARNA_DEFAULT);
                    }
                }
                sb.append("\n");
            }
        }
        else if (Reader.hasRightExit()) {
            // Print board with right exit
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    char cell = grid[r][c];
                    if (cell == movedPiece) { // highlight piece yang gerak
                        sb.append(BG_YELLOW).append(getWarnaPiece(cell)).append(cell).append(WARNA_DEFAULT);
                    } else if (cell == '.' && r == x && c == y) {
                        sb.append(BG_YELLOW).append(cell).append(WARNA_DEFAULT);
                    } else {
                        sb.append(getWarnaPiece(cell)).append(cell).append(WARNA_DEFAULT);
                    }
                }
                
                if (r == exitLocation) {
                    sb.append(getWarnaPiece('K')).append("K").append(WARNA_DEFAULT);
                }
                sb.append("\n");
            }
        }
        
        return sb.toString();
    }

    public static String getWarnaPiece(char pieceID) {
        if (pieceID == ' ') {
            return WARNA_DEFAULT;
        }
        if (pieceID < 'A' || pieceID > 'Z') {
            return WARNA_DEFAULT;
        } else  {
            int indeks = pieceID - 'A';
            return WARNA[indeks];
        }
    }

    public static final String[] WARNA = {
        "\u001B[38;2;255;255;255m", // Putih, A
        "\u001B[38;2;166;166;166m", // Abu-Abu, B
        "\u001B[38;2;255;145;76m", // Oranye, C
        "\u001B[38;2;255;222;89m", // Kuning, D
        "\u001B[38;2;0;191;98m", // Hijau, E
        "\u001B[38;2;12;193;224m", // Biru Muda, F
        "\u001B[38;2;0;74;173m", // Biru Tua, G
        "\u001B[38;2;255;101;195m", // Pink, H
        "\u001B[38;2;140;82;255m", // Ungu, I
        "\u001B[38;2;255;87;87m", // Terracota, J
        "\u001B[38;2;126;217;86m", // Hijau Muda, K (EXIT)
        "\u001B[38;2;240;255;162m", // Kuning Pucat, L
        "\u001B[38;2;254;189;89m", // Kuning Tua, M
        "\u001B[38;2;148;73;18m", // Coklat, N
        "\u001B[38;2;192;255;114m", // Lime, O
        "\u001B[38;2;255;49;49m", // Merah, P (PRIMARY)
        "\u001B[38;2;0;137;42m", // Hijau Tua, Q
        "\u001B[38;2;92;225;230m", // Cyan, R
        "\u001B[38;2;0;151;178m", // Turqoise, S
        "\u001B[38;2;56;182;255m", // Biru Langit, T
        "\u001B[38;2;82;113;255m", // Indigo, U
        "\u001B[38;2;245;57;255m", // Magenta, V
        "\u001B[38;2;240;175;255m", // Lavender, W
        "\u001B[38;2;203;107;230m", // Violet, X
        "\u001B[38;2;93;23;235m", // Ungu Tua, Y
        "\u001B[38;2;128;0;0m" // Maroon, Z
    };

    public static final String WARNA_DEFAULT = "\u001B[0m";

    public String getBoardPlain(int rows, int cols) {
        char[][] grid = new char[rows][cols];
        for (char[] row : grid) Arrays.fill(row, '.');
        char[][] parentGrid = new char[rows][cols];
        for (char[] row : parentGrid) Arrays.fill(row, '.');

        char movedPiece = getMovedPiece();

        for (Piece p : pieces) {
            int r = p.getRow(), c = p.getCol();
            for (int i = 0; i < p.getLength(); i++) {
                if (p.isHorizontal()) grid[r][c + i] = p.getId();
                else grid[r + i][c] = p.getId();
            }
        }

        int x = -1, y = -1;
        if (parent != null) {
            for (Piece p : getParent().pieces) {
                int r = p.getRow(), c = p.getCol();
                for (int i = 0; i < p.getLength(); i++) {
                    if (p.isHorizontal()) parentGrid[r][c + i] = p.getId();
                    else parentGrid[r + i][c] = p.getId();
                }
            }
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (grid[i][j] == '.' && parentGrid[i][j] != '.') {
                        x = i;
                        y = j;
                    }
                }
            }
        }

        int exitLocation = 0;
        if (Reader.hasTopExit() || Reader.hasBottomExit()) {
            exitLocation = Reader.getExitCol();
        } else if (Reader.hasLeftExit() || Reader.hasRightExit()) {
            exitLocation = Reader.getExitRow();
        }

        StringBuilder sb = new StringBuilder();
        if (Reader.hasBottomExit()) {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    sb.append(grid[r][c]);
                }
                sb.append("\n");
            }
            for (int i = 0; i < cols; i++) {
                sb.append(i == exitLocation ? "K" : " ");
            }
            sb.append("\n");
        }
        else if (Reader.hasTopExit()) {
            for (int i = 0; i < cols; i++) {
                sb.append(i == exitLocation ? "K" : " ");
            }
            sb.append("\n");
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    sb.append(grid[r][c]);
                }
                sb.append("\n");
            }
        }
        else if (Reader.hasLeftExit()) {
            for (int r = 0; r < rows; r++) {
                sb.append(r == exitLocation ? "K" : " ");
                for (int c = 0; c < cols; c++) {
                    sb.append(grid[r][c]);
                }
                sb.append("\n");
            }
        }
        else if (Reader.hasRightExit()) {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    sb.append(grid[r][c]);
                }
                sb.append(r == exitLocation ? "K" : "");
                sb.append("\n");
            }
        }

        return sb.toString();
    }
}
