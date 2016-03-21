package csp;

import com.google.common.collect.ImmutableSet;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class ExactCoverProblemTest {

    ExactCoverProblem<Integer, Set<Integer>> ecp;
    Set<Integer> a, b, c, d, e, f;

    public ExactCoverProblemTest() {
        ecp = new ExactCoverProblem<Integer, Set<Integer>>(
                ImmutableSet.of(1,2,3,4,5,6,7),
                ImmutableSet.of(
                        a = ImmutableSet.of(1,4,7),
                        b = ImmutableSet.of(1,4),
                        c = ImmutableSet.of(4,5,7),
                        d = ImmutableSet.of(3,5,6),
                        e = ImmutableSet.of(2,3,6,7),
                        f = ImmutableSet.of(2,7)
                )) {

            @Override
            public boolean relation(Set<Integer> constraint, Integer candidate) {
                return constraint.contains(candidate);
            }
        };
    }

    @Test
    public void testSolve() {
        Set<Set<Integer>> s = ecp.solve();
        assertTrue(s.contains(b));
        assertTrue(s.contains(d));
        assertTrue(s.contains(f));
    }
}
