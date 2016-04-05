package csp;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of Algorithm X with dancing links to solve the exact cover problem.
 *
 * The problem is as follows: Given a collection \(S\) of subsets of a set \(X\),
 * an exact cover of \(X\) is a subcollection \(S^*\) of \(S\) such that every
 * element in \(X\) appears in one and only one member of \(S^*\).
 *
 * More generally, given sets \(P\) and \(Q\) and a relation \(R \subseteq P \times Q\),
 * then \(P^*\) is an exact cover of \(Q\) if each element in \(Q\) is \(R^{-1}\)-related
 * to exactly one element in \(P\).
 *
 * @param <P> the type of elements in \(P\)
 * @param <Q> the type of elements in \(Q\)
 */
public abstract class ExactCoverProblem<P, Q> {

    Set<P> universe;
    Set<Q> constraints;

    DancingLinksNode root;

    /**
     * Constructor.
     *
     * @param universe all candidates in the problem, all subsets of \(X\)
     * @param constraints problem constraints, each constraint satisfies
     *                    exactly one candidate in the solution
     */
    public ExactCoverProblem(Set<P> universe, Set<Q> constraints) {
        this.universe = universe;
        this.constraints = constraints;
    }

    /**
     * The relation between the candidate and constraint.
     *
     * @param constraint a constraint
     * @param candidate a candidate
     * @return <code>true</code> iff \(R\left(\texttt{candidate}, \texttt{constraint}\right)\) obtains
     */
    public abstract boolean relation(Q constraint, P candidate);

    /**
     * Solve the puzzle.
     *
     * @return the solution, a subset of <code>universe</code>
     */
    public Set<P> solve() {
        Deque<DancingLinksNode> answer = search();
        if (answer == null) {
            return null;
        }

        return answer.stream().map(node -> node.candidate).collect(Collectors.toSet());
    }

    private Deque<DancingLinksNode> search() {
        root = new HeaderNode(null);
        constraints.forEach(this::addConstraint);
        universe.forEach(this::addCandidate);

        return search(0, new LinkedList<>());
    }

    private Deque<DancingLinksNode> search(int k, Deque<DancingLinksNode> solution) {
        if (root.right == root) {
            return solution;
        }

        DancingLinksNode candidate, current;
        HeaderNode header = chooseColumn();

        header.cover();
        for (candidate = header.down; candidate != header; candidate = candidate.down) {
            solution.add(candidate);
            for (current = candidate.right; current != candidate; current = current.right) {
                current.column.cover();
            }
            if (search(k + 1, solution) != null) {
                return solution;
            }
            candidate = solution.removeLast();
            for (current = candidate.left; current != candidate; current = current.left) {
                current.column.uncover();
            }
        }
        candidate.column.uncover();

        return null;
    }

    private void addConstraint(Q constraint) {
        HeaderNode node = new HeaderNode(constraint);

        node.up = node.down = node.column = node;

        node.left = root.left;
        node.right = root;
        node.left.right = node;
        node.right.left = node;
    }

    private void addCandidate(P candidate) {
        HeaderNode column = (HeaderNode) root.right;
        DancingLinksNode first = null, node;

        while (column != root) {
            if (relation(column.constraint, candidate)) {
                node = new DancingLinksNode(candidate);
                node.down = node.column = column;
                node.up = node.column.up;
                node.up.down = node;
                node.down.up = node;

                if (first == null) {
                    first = node.left = node.right = node;
                } else {
                    node.left = first.left;
                    node.right = first;
                    node.left.right = node;
                    node.right.left = node;
                }

                ++column.count;
            }
            column = (HeaderNode) column.right;
        }
    }

    private HeaderNode chooseColumn() {
        HeaderNode j, c;
        for (j = (HeaderNode) root.right, c = j; j != root; j = (HeaderNode) j.right) {
            if (j.count == 0) {
                return j;
            }
            if (j.count < c.count) {
                c = j;
            }
        }
        return c;
    }

    class DancingLinksNode {
        DancingLinksNode left, right, up, down;
        HeaderNode column;
        final P candidate;

        DancingLinksNode(P candidate) {
            this.candidate = candidate;
            left = right = up = down = this;
            column = null;
        }
    }

    class HeaderNode extends DancingLinksNode {
        Q constraint;
        int count = 0;

        HeaderNode(Q constraint) {
            super(null);
            this.constraint = constraint;
        }

        private void cover() {
            right.left = left;
            left.right = right;

            for (DancingLinksNode i = down; i != this; i = i.down) {
                for (DancingLinksNode j = i.right; j != i; j = j.right) {
                    j.down.up = j.up;
                    j.up.down = j.down;
                    --j.column.count;
                }
            }
        }

        private void uncover() {
            for (DancingLinksNode i = up; i != this; i = i.up) {
                for (DancingLinksNode j = i.left; j != i; j = j.left) {
                    ++j.column.count;
                    j.up.down = j;
                    j.down.up = j;

                }
            }
            left.right = this;
            right.left = this;
        }
    }
}