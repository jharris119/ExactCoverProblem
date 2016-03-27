package csp;

import java.util.*;
import java.util.stream.Collectors;

public abstract class ExactCoverProblem<P, Q> {

    Set<P> universe;
    Set<Q> constraints;

    DLXNode root;

    public ExactCoverProblem(Set<P> universe, Set<Q> constraints) {
        this.universe = universe;
        this.constraints = constraints;
    }

//    public Set<P> solve() {
//        return search().stream().map(node -> node.column.constraint).collect(Collectors.toSet());
//    }

    public abstract boolean relation(Q constraint, P candidate);

    public Queue<DLXNode> search() {
        root = new HeaderNode(null);
        constraints.forEach(this::addConstraint);
        universe.forEach(this::addCandidate);

        return search(0, new LinkedList<>());
    }

    private Queue<DLXNode> search(int k, Queue<DLXNode> solution) {
        if (root.right == root) {
            return solution;
        }

        DLXNode candidate, current;
        HeaderNode header = chooseColumn();

        String s = "";
        for (int i = 0; i < k; ++i) {
            s += " ";
        }
        System.err.println(s + header.constraint.toString());

        header.cover();


        for (candidate = header.down; candidate != header; candidate = candidate.down) {
            solution.add(candidate);
            for (current = candidate.right; current != candidate; current = current.right) {
                current.column.cover();
            }
            if (search(k + 1, solution) != null) {
                return solution;
            }
            candidate = solution.remove();
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
        DLXNode first = null, node;

        while (column != root) {
            if (relation(column.constraint, candidate)) {
                node = new DLXNode(candidate.toString());
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
            if (j.count < c.count) {
                c = j;
            }
        }
        return c;
    }

    class DLXNode {
        DLXNode left, right, up, down;
        HeaderNode column;

        String candidate;

        DLXNode() {
            left = right = up = down = this;
            column = null;
        }

        DLXNode(String candidate) {
            this.candidate = candidate;
            left = right = up = down = this;
            column = null;
        }

        @Override
        public String toString() {
            return "row: " + candidate + " in column: " + column.toString();
        }
    }

    class HeaderNode extends DLXNode {
        Q constraint;
        int count = 0;

        HeaderNode(Q constraint) {
            this.constraint = constraint;
        }

        private void cover() {
            System.err.println("covering " + this.toString());

            right.left = left;
            left.right = right;

            for (DLXNode i = down; i != this; i = i.down) {
                for (DLXNode j = i.right; j != i; j = j.right) {
                    j.down.up = j.up;
                    j.up.down = j.down;
                    --j.column.count;
                }
            }
        }

        private void uncover() {
            for (DLXNode i = up; i != this; i = i.up) {
                for (DLXNode j = i.left; j != i; j = j.left) {
                    ++j.column.count;
                    j.up.down = j;
                    j.down.up = j;

                }
            }
            left.right = this;
            right.left = this;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("HeaderNode{");
            sb.append("constraint=").append(constraint);
            sb.append('}');
            return sb.toString();
        }
    }
}