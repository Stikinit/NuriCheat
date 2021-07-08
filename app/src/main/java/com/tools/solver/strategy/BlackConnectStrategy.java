package com.tools.solver.strategy;

import com.tools.board.Board;
import com.tools.board.BoardOps;
import com.tools.board.Cell;
import com.tools.board.CellColor;
import com.tools.board.CellSet;
import com.tools.solver.SolverStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * For each unknown neighbor cell of a black group, assume that it is white and then try to find a
 * path to another black group, if there is none, than that cell must be black.
 */
public class BlackConnectStrategy implements SolverStrategy {

    @Override
    public void improveSolution(Board board) {
        final Set<Set<Cell>> blackGroups = board.getGroups(CellColor.BLACK);
        if (blackGroups.size() < 2) {
            // we need at least 2 groups
            return;
        }
        for (final Set<Cell> blackGroup : blackGroups) {
            for (final Cell unknownNeighbor : BoardOps.unknownNeighbors(board, blackGroup)) {
                if (!pathToOtherGroupExists(board, blackGroup, unknownNeighbor)) {
                    unknownNeighbor.setBlack();
                    // return when one is found, we would need to start from the beginning anyway...
                    return;
                }
            }
        }
    }

    private boolean pathToOtherGroupExists(Board board, Set<Cell> blackGroup, Cell tabooCell) {
        // "flood fill" starting from one of the blackCells
        final Cell startCell = blackGroup.iterator().next();
        final Set<Cell> seen = new CellSet(board);
        final List<Cell> todo = new ArrayList<>();
        todo.add(startCell);
        while (!todo.isEmpty()) {
            final Cell current = todo.remove(todo.size() - 1);
            if (!seen.add(current)) {
                // already done
                continue;
            }
            for (final Cell neighbor : board.getNeighborsSet(current)) {
                if (neighbor.isBlack() && !blackGroup.contains(neighbor)) {
                    // we found a path to another black group
                    return true;
                }
                else if (!neighbor.isWhite() && !neighbor.equals(tabooCell)) {
                    todo.add(neighbor);
                }
            }
        }
        // nothing found
        return false;
    }

}
