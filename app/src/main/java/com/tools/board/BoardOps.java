package com.tools.board;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BoardOps {

    public static int getNumber(List<Integer> list, int x, int y) {
        return list.get(coordsToIndex(x, y));
    }

    public static int coordsToIndex(int x, int y) {
        return y * 5 + x;
    }

    public static boolean isLegalCoord(int x, int y) {
        return !(x < 0 || x >= 5) && !(y < 0 || y >= 5);
    }

    public static Set<Cell> unknownNeighbors(Board board, Collection<Cell> cells) {
        return cells.stream().flatMap(cell -> board.getNeighbors(cell).filter(Cell::isUnknown))
                .collect(Collectors.toCollection(() -> new CellSet(board)));
    }

    public static Set<Set<Cell>> generateValidIslands(PivotCell pivotCell, Set<Cell> requiredCells) {
        if (pivotCell.getNumber() == requiredCells.size()) {
            // already complete
            return Collections.singleton(requiredCells);
        }
        final Set<Set<Cell>> result = new HashSet<>();
        final Set<Cell> startCells = new CellSet(pivotCell.getBoard());
        startCells.add(pivotCell);
        generateValidIslandsRecursive(pivotCell, requiredCells, startCells, new HashSet<Set<Cell>>(),
                new HashMap<Cell, Set<Cell>>(), result);
        return result;
    }

    public static void generateValidIslandsRecursive(PivotCell pivotCell, Set<Cell> requiredCells,
                                               Set<Cell> currentCells, Set<Set<Cell>> currentCellsSeen,
                                               Map<Cell, Set<Cell>> validNeighborsCache, Set<Set<Cell>> result) {
        if (pivotCell.getNumber() == currentCells.size()) {
            // potential valid island
            if (currentCells.containsAll(requiredCells)) {
                // add a copy to result (because the caller might modify currentCells)
                result.add(new CellSet(currentCells));
            }
            return;
        }

        if (currentCellsSeen.contains(currentCells)) {
            return;
        }
        else {
            currentCellsSeen.add(new CellSet(currentCells));
        }

        // recurse for each possible "neighbor"
        final Set<Cell> doneNeighbors = new CellSet(pivotCell.getBoard());
        for (final Cell cell : currentCells.toArray(new Cell[currentCells.size()])) {
            for (final Cell neighbor : getValidNeighbors(cell, pivotCell, validNeighborsCache)) {
                if (doneNeighbors.contains(neighbor)) {
                    // already seen...
                    continue;
                }
                else {
                    doneNeighbors.add(neighbor);
                }

                if (!currentCells.contains(neighbor)) {
                    // "expand" into neighbor
                    currentCells.add(neighbor);
                    generateValidIslandsRecursive(pivotCell, requiredCells, currentCells, currentCellsSeen,
                            validNeighborsCache, result);
                    currentCells.remove(neighbor);
                }
            }
        }
    }

    public static Set<Cell> getValidNeighbors(Cell cell, PivotCell pivotCell,
                                        Map<Cell, Set<Cell>> validNeighborsCache) {
        Set<Cell> result = validNeighborsCache.get(cell);
        if (result == null) {
            final Board board = cell.getBoard();
            result = new CellSet(board.getNeighborsSet(cell));

            // remove those that are not valid
            final Iterator<Cell> iterator = result.iterator();
            while (iterator.hasNext()) {
                final Cell neighbor = iterator.next();
                if (neighbor.isBlack()
                        || (neighbor.getPivotCell() != null && !neighbor.getPivotCell().equals(pivotCell))) {
                    iterator.remove();
                }
                else {
                    // check if any neighbor has a different fixed cell
                    for (final Cell neighborNeighbor : board.getNeighborsSet(neighbor)) {
                        if (neighborNeighbor.getPivotCell() != null
                                && !neighborNeighbor.getPivotCell().equals(pivotCell)) {
                            // neighboring different fixed cells are not possible
                            iterator.remove();
                            break;
                        }
                    }
                }
            }

            validNeighborsCache.put(cell, result);
        }
        return result;
    }

}
