public class Piece {
    private final char id;
    private final int row;
    private final int col;
    private final int length;
    private final boolean isHorizontal;

    public Piece(char id, int row, int col, int length, boolean isHorizontal) {
        this.id = id;
        this.row = row;
        this.col = col;
        this.length = length;
        this.isHorizontal = isHorizontal;
    }

    public char getId() { return id; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public int getLength() { return length; }
    public boolean isHorizontal() { return isHorizontal; }

    @Override
    public String toString() {
        return id + " at (" + row + "," + col + ") " + (isHorizontal ? "H" : "V") + " len=" + length;
    }

    public Piece move(int direction) {
        return isHorizontal
            ? new Piece(id, row, col + direction, length, true)
            : new Piece(id, row + direction, col, length, false);
    }

    public Piece copy() {
        return new Piece(id, row, col, length, isHorizontal);
    }
}
