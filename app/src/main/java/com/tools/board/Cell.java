package com.tools.board;

public abstract class Cell {

    private final Board board;

    private final int x;
    private final int y;

    public Cell(Board board, int x, int y) {
        this.board = board;
        this.x = x;
        this.y = y;
    }

    public Board getBoard() { // UTILE ?
        return board;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public abstract boolean isPivot();

    public abstract boolean isWhite();

    public abstract void setWhite();

    public abstract boolean isBlack();

    public abstract void setBlack();

    public boolean isUnknown() {
        return !isWhite() && !isBlack();
    }

    public abstract void setUnknown();

    // Se la cella è SIMPLE, ritorna la PIVOT a lei collegata, negli altri casi null
    public abstract PivotCell getPivotCell();

    public abstract void setPivotCell(PivotCell pivotCell);

    public CellColor getColor() {
        if (isBlack()) {
            return CellColor.BLACK;
        }
        else if (isWhite()) {
            return CellColor.WHITE;
        }
        else {
            return CellColor.UNKNOWN;
        }
    }

    public void setColor(CellColor cellColor) {
        if (cellColor == CellColor.BLACK) {
            setBlack();
        }
        else if (cellColor == CellColor.WHITE) {
            setWhite();
        }
        else if (cellColor == CellColor.UNKNOWN) {
            setUnknown();
        }
        else {
            throw new IllegalArgumentException("unexpected cellColor: " + cellColor);
        }
    }

    public boolean isNeighbor(Cell cell) { // UTILE ?
        if (getBoard() == cell.getBoard()) {
            if (getX() == cell.getX()) {
                return Math.abs(getY() - cell.getY()) == 1;
            }
            else if (getY() == cell.getY()) {
                return Math.abs(getX() - cell.getX()) == 1;
            }
        }
        return false;
    }

}
