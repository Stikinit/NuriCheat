package com.tools.solver.strategy;

import com.tools.board.Board;
import com.tools.board.BoardOps;
import com.tools.board.Cell;
import com.tools.board.CellColor;
import com.tools.board.PivotCell;
import com.tools.solver.SolverStrategy;

import java.util.Set;


/**
 * If a group (white or black) is not complete and there is only one cell where it could expand,
 * then that cell must belong to the group.
 */
public class ExpandStrategy implements SolverStrategy {

    @Override
    public void improveSolution(Board board) {
        // handle black cells
        outer: while (board.getCount(CellColor.BLACK) < board.getSolutionBlackCount()) {
            for (final Set<Cell> group : board.getGroups(CellColor.BLACK)) {
                if (tryExpand(board, group, CellColor.BLACK)) {
                    // we need to start from the beginning because the black groups could have changed
                    continue outer;
                }
            }
            break;
        }

        // handle white cells
        outer: while (true) {
            board.connectWhiteCells();
            for (final Set<Cell> group : board.getGroups(CellColor.WHITE)) {
                final PivotCell fixedCell = group.iterator().next().getPivotCell();
                if (fixedCell == null || fixedCell.getNumber() > group.size()) {
                    if (tryExpand(board, group, CellColor.WHITE)) {
                        // we need to start from the beginning because the white groups could have changed
                        continue outer;
                    }
                }
            }
            break;
        }
    }

    private boolean tryExpand(Board board, Set<Cell> group, CellColor cellColor) {
        final Set<Cell> unknownNeighbors = BoardOps.unknownNeighbors(board, group);

        if (unknownNeighbors.size() == 1) {
            unknownNeighbors.iterator().next().setColor(cellColor);
            return true;
        }
        else {
            return false;
        }
    }

}
