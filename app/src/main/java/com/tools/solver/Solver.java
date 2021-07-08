package com.tools.solver;

import com.tools.board.Board;
import com.tools.board.BoardOps;
import com.tools.board.BoardState;
import com.tools.board.Cell;
import com.tools.board.CellColor;
import com.tools.board.PivotCell;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;


public class Solver {

    private final List<SolverStrategy> strategies;
    private final int maxTries = 2;
    public Solver(List<SolverStrategy> strategies) {
        this.strategies = new ArrayList<>(strategies);
    }

    /**
     * @param board
     * @return whether the board could be solved
     */
    public boolean tryToSolve(Board board) {
        String oldStringState = "";
        while (true) {
            for (final SolverStrategy strategy : strategies) {
                System.out.println("Trying the following strategy:" + strategy.toString());
                try {
                    strategy.improveSolution(board);
                }
                catch(IllegalStateException e) {
                    return false;
                }
            }
            String newStringState = board.toString();

            if (board.isSolution()) {
                return true;
            }
            else {
                if(newStringState.contains(CellColor.UNKNOWN.ordinal()+"") && oldStringState.equals(newStringState)) {
                    BoardState boardState = new BoardState(board);
                    if (inferIsland(board, boardState)) { // IN CASE THE STRATEGIES GET STUCK IN A LOOP
                        return true;
                    }
                    else break;
                }
                if(!newStringState.contains(CellColor.UNKNOWN.ordinal()+"")) {
                    break;
                }
            }
            oldStringState = newStringState;
        }

        return false;
    }

    private boolean inferIsland(Board board, BoardState boardState) {

        final Set<PivotCell> hungryPivots = new HashSet<>();
        Set<Cell> whiteGroup = null;
        for (final Map.Entry<PivotCell, Set<Cell>> entry: board.getWhiteGroupsWithPivotCell().entrySet()) {
            if(entry.getKey().getNumber() > entry.getValue().size()) {
                hungryPivots.add(entry.getKey());
            }
        }
        final Optional<PivotCell> bigPivotOpt = hungryPivots.stream().reduce((pivotCell, pivotCell2) -> pivotCell.getNumber() > pivotCell2.getNumber() ? pivotCell : pivotCell2);
        if(!bigPivotOpt.isPresent()) {
            return false;
        }
        final PivotCell bigBoi = bigPivotOpt.get();

        for (final Entry<PivotCell, Set<Cell>> entry: board.getWhiteGroupsWithPivotCell().entrySet()) {
            if(entry.getKey() == bigBoi) {
                whiteGroup = entry.getValue();
                break;
            }
        }

        final Set<Set<Cell>> validIslands = BoardOps.generateValidIslands(bigBoi, whiteGroup); // FIND ALL VALID ISLANDS OF THE BIGGEST HUNGRY PIVOT
        for (final Set<Cell> validIsland: validIslands) {
            if(tryWithNewIsland(bigBoi, validIsland)) {
                return true;
            }
            else {
                boardState.restoreState();
            }
        }
        return false;
    }

    private boolean tryWithNewIsland(PivotCell pivotCell, Set<Cell> validIsland) {
        for (final Cell cell: validIsland) {
            cell.setColor(CellColor.WHITE);
            cell.setPivotCell(pivotCell);
        }
        return tryToSolve(pivotCell.getBoard());
    }

}