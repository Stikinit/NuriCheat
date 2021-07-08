
package com.tools.solver;

import com.tools.board.Board;

public interface SolverStrategy {

     /*The strategy implementation should try to improve the existing partial solution on the given
     board*/

    void improveSolution(Board board);

}