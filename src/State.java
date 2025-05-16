import java.util.*;

public class State implements Comparable<State> {
    private final List<Piece> pieces;
    private final int cost;             // g(n): path cost
    private final int heuristic;        // h(n): estimated cost to goal
    private final State parent;         // path reconstruction
    private int cols;

    public State(List<Piece> pieces, int cost, int heuristic, State parent, int cols) {
        this.pieces = new ArrayList<>();
        for (Piece p : pieces) {
            this.pieces.add(new Piece(p.getId(), p.getRow(), p.getCol(), p.getLength(), p.isHorizontal()));
        }
        this.cost = cost;
        this.heuristic = heuristic;
        this.parent = parent;
        this.cols = cols;
    }

    public List<Piece> getPieces() { return pieces; }
    public int getCost() { return cost; }
    public int getHeuristic() { return heuristic; }
    public int getTotalCost() { return cost + heuristic; }
    public State getParent() { return parent; }
    public int getCols() { return cols; }

    public boolean isGoal() {
        Piece primary = pieces.get(0); // Assume first is primary
        int endCol = primary.getCol() + primary.getLength() - 1;
        return endCol == cols - 1;
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

                    State next = new State(newPieces, this.cost + 1, 0, this, this.cols);
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

    public String getBoard(int rows, int cols) {
        char[][] grid = new char[rows][cols];
        for (char[] row : grid) Arrays.fill(row, '.');

        for (Piece p : pieces) {
            int r = p.getRow(), c = p.getCol();
            for (int i = 0; i < p.getLength(); i++) {
                if (p.isHorizontal()) grid[r][c + i] = p.getId();
                else grid[r + i][c] = p.getId();
            }
        }

        StringBuilder sb = new StringBuilder();
        for (char[] row : grid) {
            sb.append(new String(row)).append("\n");
        }
        return sb.toString();
    }
}
