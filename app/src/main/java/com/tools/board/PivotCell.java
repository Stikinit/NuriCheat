package com.tools.board;

public class PivotCell extends Cell {

    private final int number;

    public PivotCell(Board board, int x, int y, int number) {
        super(board, x, y);
        if (number < 1) {
            throw new IllegalArgumentException("number");
        }
        this.number = number;
    }

    @Override
    public boolean isPivot() {
        return true;
    }

    @Override
    public boolean isWhite() {
        return true;
    }

    @Override
    public boolean isBlack() {
        return false;
    }

    @Override
    public void setWhite() {}

    @Override
    public void setBlack() {
        throw new IllegalArgumentException();
    }

    @Override
    public void setUnknown() {
        throw new IllegalArgumentException();
    }

    public PivotCell getPivotCell() {
        return this;
    }

    @Override
    public void setPivotCell(PivotCell pivotCell) {
        if (pivotCell != this) {
            throw new IllegalArgumentException();
        }
        //do nothing
    }

    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "PivotCell(" + number + ")";
    }

}
