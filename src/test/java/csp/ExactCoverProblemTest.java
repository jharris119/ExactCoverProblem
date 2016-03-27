package csp;

import com.google.common.collect.ImmutableSet;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class ExactCoverProblemTest {

    ExactCoverProblem<Set<Integer>, Integer> ecp;
    Set<Integer> a, b, c, d, e, f;

    public ExactCoverProblemTest() {
        ecp = new ExactCoverProblem<Set<Integer>, Integer>(
                ImmutableSet.of(
                        a = ImmutableSet.of(1,4,7),
                        b = ImmutableSet.of(1,4),
                        c = ImmutableSet.of(4,5,7),
                        d = ImmutableSet.of(3,5,6),
                        e = ImmutableSet.of(2,3,6,7),
                        f = ImmutableSet.of(2,7)
                ), ImmutableSet.of(1,2,3,4,5,6,7)) {
            @Override
            public boolean relation(Integer constraint, Set<Integer> candidate) {
                return candidate.contains(constraint);
            }
        };
    }

    @Test
    public void testSolve() {
        Set<Set<Integer>> result = ecp.solve();
        assertEquals(ImmutableSet.of(b, d, f), result);
    }
}
