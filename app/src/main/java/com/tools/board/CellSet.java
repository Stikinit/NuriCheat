package com.tools.board;

import java.util.AbstractSet;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

public class CellSet extends AbstractSet<Cell> {

    private final Board board;
    private final BitSet bitSet;

    public CellSet(Board board) {
        this.board = Objects.requireNonNull(board);
        bitSet = new BitSet(board.getCellCount());
    }

    /**
     * @param cells
     *          must either be a {@link CellSet} or not empty.
     */
    public CellSet(Collection<? extends Cell> cells) {
        if (cells instanceof CellSet) {
            final CellSet cellSet = (CellSet) cells;
            board = cellSet.board;
            bitSet = BitSet.valueOf(cellSet.bitSet.toLongArray());
        }
        else {
            board = cells.iterator().next().getBoard();
            bitSet = new BitSet(board.getCellCount());
            addAll(cells);
        }
    }

    private int toIndex(Cell cell) {
        return BoardOps.coordsToIndex(cell.getX(), cell.getY());
    }

    private void checkCell(Cell cell) {
        if (cell.getBoard() != board) {
            throw new IllegalArgumentException("cell does not belong to board");
        }
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof Cell) {
            final Cell cell = (Cell) o;
            if (cell.getBoard() == board) {
                return bitSet.get(toIndex(cell));
            }
        }
        return false;
    }

    @Override
    public boolean add(Cell cell) {
        checkCell(cell);
        final boolean contained = contains(cell);
        bitSet.set(toIndex(cell));
        return !contained;
    }

    @Override
    public boolean remove(Object o) {
        final boolean contained = contains(o);
        if (contained) {
            bitSet.set(toIndex((Cell) o), false);
        }
        return contained;
    }

    @Override
    public void clear() {
        bitSet.clear();
    }

    @Override
    public Iterator<Cell> iterator() {
        return new Iterator<Cell>() {
            private Cell last;
            private int nextIdx = bitSet.nextSetBit(0);

            @Override
            public boolean hasNext() {
                return nextIdx >= 0;
            }

            @Override
            public Cell next() {
                last = board.cells[nextIdx];
                nextIdx = bitSet.nextSetBit(nextIdx + 1);
                return last;
            }

            @Override
            public void remove() {
                if (last != null) {
                    CellSet.this.remove(last);
                }
                else {
                    throw new IllegalStateException("next() not called");
                }
            }
        };
    }

    @Override
    public int size() {
        return bitSet.cardinality();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof CellSet) {
            final CellSet cellSet = (CellSet) obj;
            return board == cellSet.board && bitSet.equals(cellSet.bitSet);
        }
        else {
            return super.equals(obj);
        }
    }

}
