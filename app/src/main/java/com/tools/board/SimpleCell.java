package com.tools.board;

public class SimpleCell extends Cell{

    private boolean white = false;
    private boolean black = false;

    private PivotCell pivotCell = null;

    public SimpleCell(Board board, int x, int y) {
        super(board, x, y);
    }

    public boolean isPivot() {
        return false;
    }

    @Override
    public boolean isWhite() {
        return white;
    }

    @Override
    public void setWhite() {
        white = true;
        black = false;
    }

    @Override
    public boolean isBlack() {
        return black;
    }

    private IllegalArgumentException pivotCellException() {
        return new IllegalArgumentException("only white cells can be associated with a pivot cell");
    }

    @Override
    public void setBlack() {
        if (getPivotCell() != null) {
            throw pivotCellException();
        }
        white = false;
        black = true;
    }

    @Override
    public void setUnknown() {
        if (getPivotCell() != null) {
            throw pivotCellException();
        }
        white = false;
        black = false;
    }

    @Override
    public PivotCell getPivotCell() {
        return pivotCell;
    }

    @Override
    public void setPivotCell(PivotCell pivotCell) {
        if (!isWhite() && pivotCell != null) {
            throw pivotCellException();
        }
        this.pivotCell = pivotCell;
    }

    @Override
    public String toString() {
        return "SimpleCell(" + (isWhite() ? "w" : (isBlack() ? "b" : "?")) + ")";
    }


}
