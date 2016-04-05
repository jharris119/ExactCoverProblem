# ExactCoverProblem
Java module for solving exact cover problems

This is a module for solving exact cover problems that's intended to be composited into a enclosing "problem" class and called from there. See https://github.com/jharris119/pentominoes or https://github.com/jharris119/sudoku for examples.

The constructor takes a `Set` of "candidates" and a `Set` of "constraints" and leaves space in an abstract method for defining the relation between the two. `ExactCoverProblem#relation` should return true if constraint _Q_ is satisfied by candidate _P_ and false otherwise.

With the sets of constraints and candidates created, the only other exposed method is `solve`, which returns a subset _P*_ of the candidates such that each constraint satisfies one and only member of _P*_.
