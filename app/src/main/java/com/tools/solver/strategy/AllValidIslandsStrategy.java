package com.tools.solver.strategy;

import com.tools.board.Board;
import com.tools.board.Cell;
import com.tools.board.CellSet;
import com.tools.board.PivotCell;
import com.tools.solver.SolverStrategy;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


/**
 * For each {@linkplain Board#getWhiteGroupsWithPivotCell() white group} generate all currently
 * valid complete islands (i.e. the islands contain all cells that are currently known to belong to
 * the fixed cell and the number of the cells in the island matches the number of the fixed cell).
 * <p>
 * With the result apply the following rules:
 * <ul>
 * <li>all cells that are in no valid island must be black</li>
 * <li>all cells that are in all valid islands of a particular white group must be white and belong
 * to the groups fixed cell</li>
 * <li>white cells that do not have a fixed cell yet and are only reachable from one group must
 * belong to that group</li>
 * <ul>
 */
public class AllValidIslandsStrategy implements SolverStrategy {

    @Override
    public void improveSolution(Board board) throws IllegalStateException {
        // first we need to ensure that all cells have fixed cell set (if possible)
        board.connectWhiteCells();

        // for each cell, from which fixed cells it can be reached
        final Map<Cell, Set<PivotCell>> reachableMap = new HashMap<>();
        for (final Cell cell : board) {
            if (!cell.isBlack()) {
                reachableMap.put(cell, new HashSet<PivotCell>());
            }
        }

        // iterate over all whiteGroups
        for (final Entry<PivotCell, Set<Cell>> entry : board.getWhiteGroupsWithPivotCell().entrySet()) {
            final PivotCell pivotCell = entry.getKey();
            final Set<Cell> whiteGroup = entry.getValue();

            final Set<Set<Cell>> validIslands = generateValidIslands(pivotCell, whiteGroup);
            if (validIslands.isEmpty()) {
                // should never happen with a valid board/state...
                throw new IllegalStateException("no valid islands found for " + pivotCell + " at "
                        + pivotCell.getX() + "," + pivotCell.getY());
            }
            else {
                Set<Cell> inAllIslands = null;
                final Set<Cell> inSomeIslands = new CellSet(board);
                for (final Set<Cell> validIsland : validIslands) {
                    if (inAllIslands == null) {
                        inAllIslands = new CellSet(validIsland);
                    }
                    else {
                        inAllIslands.retainAll(validIsland);
                    }
                    inSomeIslands.addAll(validIsland);
                }
                assert inAllIslands != null;

                for (final Cell cell : inAllIslands) {
                    // belongs to the current fixed cell
                    cell.setWhite();
                    cell.setPivotCell(pivotCell);
                }

                for (final Cell cell : inSomeIslands) {
                    // mark it as reachable
                    reachableMap.get(cell).add(pivotCell);
                }
            }
        }

        for (final Entry<Cell, Set<PivotCell>> entry : reachableMap.entrySet()) {
            final Cell cell = entry.getKey();
            final Set<PivotCell> reachableFrom = entry.getValue();
            if (reachableFrom.isEmpty()) {
                cell.setBlack();
            }
            else if (cell.isWhite() && reachableFrom.size() == 1) {
                cell.setPivotCell(reachableFrom.iterator().next());
            }
        }
    }

    private Set<Set<Cell>> generateValidIslands(PivotCell pivotCell, Set<Cell> requiredCells) {
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

    private void generateValidIslandsRecursive(PivotCell pivotCell, Set<Cell> requiredCells,
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

    private Set<Cell> getValidNeighbors(Cell cell, PivotCell pivotCell,
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