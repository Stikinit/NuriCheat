package com.tools.board;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Board implements Iterable<Cell> {

    private final int width = 5;
    private final int height = 5;

    private final int solutionWhiteCount;

    final Cell[] cells;
    private final List<Cell> cellsListView;

    private final List<Set<Cell>> neighborSets;

    public Board(List<Integer> boardList) {
        cells = new Cell[width*height];
        cellsListView = Collections.unmodifiableList(Arrays.asList(cells));

        int numberSum = 0;
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                final int number = BoardOps.getNumber(boardList, x, y);
                if (number == 0) {
                    cells[BoardOps.coordsToIndex(x, y)] = new SimpleCell(this, x, y);
                } else {
                    cells[BoardOps.coordsToIndex(x, y)] = new PivotCell(this, x, y, number);
                }
                numberSum += number;
            }
        }

        if (numberSum <= 0) {
            throw new IllegalArgumentException("no white pivot cells");
        }
        else if (numberSum > getCellCount()) {
            throw new IllegalArgumentException("solution impossible, to many expected white cells");
        }
        solutionWhiteCount = numberSum;

        neighborSets = cellsListView.stream().map(cell -> {
            final int x = cell.getX();
            final int y = cell.getY();
            final Set<Cell> builder = new HashSet<Cell>();
            if (BoardOps.isLegalCoord(x + 1, y)) {
                builder.add(getCell(x + 1, y));
            }
            if (BoardOps.isLegalCoord(x - 1, y)) {
                builder.add(getCell(x - 1, y));
            }
            if (BoardOps.isLegalCoord(x, y + 1)) {
                builder.add(getCell(x, y + 1));
            }
            if (BoardOps.isLegalCoord(x, y - 1)) {
                builder.add(getCell(x, y - 1));
            }
            return builder;
        }).collect(Collectors.toList());
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Cell getCell(int x, int y) {
        return cells[BoardOps.coordsToIndex(x, y)];
    }

    public int getCellCount() {
        return width * height;
    }

    public int getSolutionWhiteCount() {
        return solutionWhiteCount;
    }

    public int getSolutionBlackCount() {
        return getCellCount() - getSolutionWhiteCount();
    }

    public Stream<Cell> getCells() {
        return cellsListView.stream();
    }

    public Stream<Cell> getWhiteCells() {
        return getCells().filter(cell -> cell.isWhite());
    }

    public Stream<Cell> getBlackCells() {
        return getCells().filter(cell -> cell.isBlack());
    }

    public Stream<Cell> getUnknownCells() {
        return getCells().filter(cell -> cell.isUnknown());
    }

    public int getCount(CellColor cellColor) {
        return (int) getCells().filter(cell -> cell.getColor() == cellColor).count();
    }

    public Set<Cell> getNeighborsSet(Cell cell) {
        if (cell.getBoard() != this) {
            throw new IllegalArgumentException("cell");
        }
        return neighborSets.get(BoardOps.coordsToIndex(cell.getX(), cell.getY()));
    }

    public Stream<Cell> getNeighbors(Cell cell) {
        return getNeighborsSet(cell).stream();
    }

    private void connectWhiteCell(Cell cell) {
        final PivotCell pivotCell = cell.getPivotCell();
        if (pivotCell != null) {
            getNeighbors(cell).filter(neighbor -> neighbor.isWhite() && neighbor.getPivotCell() == null)
                    .forEach(neighbor -> {
                        neighbor.setPivotCell(pivotCell);
                        connectWhiteCell(neighbor);
                    });
        }
    }

    public void connectWhiteCells() {
        getWhiteCells().forEach(this::connectWhiteCell);
    }

    public Map<PivotCell, Set<Cell>> getWhiteGroupsWithPivotCell() {
        connectWhiteCells();
        return getCells().filter(cell -> cell.getPivotCell() != null)
                .collect(
                        Collectors.groupingBy(Cell::getPivotCell,
                                Collectors.toCollection(() -> new CellSet(this))));
    }

    private void findConnectedCells(Cell cell, Set<Cell> result) {
        result.add(cell);
        getNeighbors(cell).filter(neighbor -> neighbor.getColor() == cell.getColor())
                .filter(neighbor -> !result.contains(neighbor))
                .forEach(neighbor -> findConnectedCells(neighbor, result));
    }

    public Set<Set<Cell>> getGroups(CellColor cellColor) {
        final Set<Cell> cellsWithColor = new CellSet(this);
        for (final Cell cell : cells) {
            if (cell.getColor() == cellColor) {
                cellsWithColor.add(cell);
            }
        }

        final Set<Set<Cell>> result = new HashSet<>();
        while (!cellsWithColor.isEmpty()) {
            final Set<Cell> group = new CellSet(this);
            final Cell startCell = cellsWithColor.iterator().next();
            findConnectedCells(startCell, group);
            result.add(group);

            cellsWithColor.removeAll(group);
        }

        return result;
    }

    public boolean isSolution() {
        if (getSolutionWhiteCount() != getCount(CellColor.WHITE)
                || getSolutionBlackCount() != getCount(CellColor.BLACK)) {
            return false;
        }
        connectWhiteCells();

        // Nessuna cella bianca non connessa
        if (getWhiteCells().anyMatch(cell -> cell.getPivotCell() == null)) {
            return false;
        }

        // tutti i gruppi bianchi completi
        final Map<PivotCell, Set<Cell>> whiteGroups = getWhiteGroupsWithPivotCell();
        for (final Map.Entry<PivotCell, Set<Cell>> entry : whiteGroups.entrySet()) {
            if (entry.getKey().getNumber() != entry.getValue().size()) {
                return false;
            }
        }

        // tutti i vicini di una pivot sono insieme
        for (final Cell cell : cells) {
            final PivotCell pivotCell = cell.getPivotCell();
            if (pivotCell != null) {
                if (getNeighbors(cell).allMatch(
                        neighbor -> neighbor.getPivotCell() != null && neighbor.getPivotCell() != pivotCell)) {
                    return false;
                }
            }
        }

        // massimo un gruppo di celle nere
        if (getGroups(CellColor.BLACK).size() > 1) {
            return false;
        }

        // nessun "blocco" (2x2) di celle nere
        for (int x = 0; x < getWidth() - 1; ++x) {
            for (int y = 0; y < getHeight() - 1; ++y) {
                final Cell cell1 = getCell(x, y);
                final Cell cell2 = getCell(x + 1, y);
                final Cell cell3 = getCell(x, y + 1);
                final Cell cell4 = getCell(x + 1, y + 1);
                if (cell1.isBlack() && cell2.isBlack() && cell3.isBlack() && cell4.isBlack()) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public Iterator<Cell> iterator() {
        return cellsListView.iterator();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Cell cell: cells) {
            sb.append(cell.getColor().ordinal()+"");
            if(cell.getPivotCell()!=null) {
                sb.append(cell.getPivotCell().getX()+""+cell.getPivotCell().getY());
            }
        }

        return sb.toString();
    }

}













