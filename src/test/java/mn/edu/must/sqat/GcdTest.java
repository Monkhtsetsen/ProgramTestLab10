package mn.edu.must.sqat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * All-DU-pairs coverage for {@link DataFlowTargets#gcd(int, int)} (file lines 13–20).
 */
class GcdTest {

    @Test
    @DisplayName("DU x(13,20) y(13,15) — gcd(7,0) no loop")
    void gcd_noLoop_yZero() {
        assertEquals(7, DataFlowTargets.gcd(7, 0));
    }

    @Test
    @DisplayName("DU x(13,16) y(13,16) tmp(16,18) — first loop body")
    void gcd_oneIteration() {
        assertEquals(5, DataFlowTargets.gcd(10, 5));
    }

    @Test
    @DisplayName("DU x(17,16) x(17,20) y(18,15) y(18,16) — multiple iterations")
    void gcd_multipleIterations() {
        assertEquals(4, DataFlowTargets.gcd(12, 8));
    }

    @Test
    @DisplayName("DU x(13,16) when x=0 — c-use of param x at line 16")
    void gcd_zeroX_entersLoop() {
        assertEquals(5, DataFlowTargets.gcd(0, 5));
    }
}