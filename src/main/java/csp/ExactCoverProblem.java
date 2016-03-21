package csp;

import java.util.*;

public abstract class ExactCoverProblem<P, Q> {

    Set<P> universe;
    Set<Q> constraints;

    Node root;

    public ExactCoverProblem(Set<P> universe, Set<Q> constraints) {
        this.universe = universe;
        this.constraints = constraints;
    }

    public abstract boolean relation(Q constraint, P candidate);

    public Set<P> solve2() {
        Set<P> solution = new HashSet();
        Queue<Node> result = search();

        for (Node node : result) {
            solution.add(node.column.payload);
        }
        return solution;
    }

    public Set<Set<P>> solve() {
        Set<Set<P>> solution = new HashSet();
        Queue<Node> result = search();

        for (Node node : result) {
            Set<P> p = new HashSet();
            Node current = node;
            do {
                p.add(current.column.payload);
                current = current.right;
            } while (current != node);
            solution.add(p);
        }

        return solution;
    }

    public Queue<Node> search() {
        root = new HeaderNode(null);
        universe.forEach(this::addCandidate);
        constraints.forEach(this::addConstraint);

        return search(0, new LinkedList<>());
    }

    private Queue<Node> search(int k, Queue<Node> solution) {
        if (root.right == root) {
            return solution;
        }

        Node candidate, current;
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
            candidate = solution.remove();
            for (current = candidate.left; current != candidate; current = current.left) {
                current.column.uncover();
            }
        }
        candidate.column.uncover();

        return null;
    }

    private void addCandidate(P candidate) {
        HeaderNode node = new HeaderNode(candidate);

        node.up = node.down = node.column = node;

        node.left = root.left;
        node.right = root;
        node.left.right = node;
        node.right.left = node;
    }

    private void addConstraint(Q constraint) {
        P candidate;
        HeaderNode column = (HeaderNode) root.right;
        Node first = null, node;

        while (column != root) {
            candidate = column.payload;
            if (relation(constraint, candidate)) {
                node = new Node();
                node.down = node.column = column;
                node.up = node.column.up;
                node.up.down = node;
                node.down.up = node;

                if (first == null) {
                    first = node.left = node.right = node;
                }
                else {
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

    class Node {
        Node left, right, up, down;
        HeaderNode column;

        Node() {
            left = right = up = down = this;
            column = null;
        }
    }

    class HeaderNode extends Node {
        P payload;
        int count = 0;

        HeaderNode(P payload) {
            this.payload = payload;
        }

        private void cover() {
            right.left = left;
            left.right = right;

            for (Node i = down; i != this; i = i.down) {
                for (Node j = i.right; j != i; j = j.right) {
                    j.down.up = j.up;
                    j.up.down = j.down;
                    --j.column.count;
                }
            }
        }

        private void uncover() {
            for (Node i = up; i != this; i = i.up) {
                for (Node j = i.left; j != i; j = j.left) {
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