package mn.edu.must.sqat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BonusTest {

    @Nested
    @DisplayName("All-definitions (minimum set)")
    class AllDefinitions {

        @Test
        @DisplayName("def attendance/threshold/a; present/streak/best/score path L35-56")
        void fullScore_thresholdAndStreakBonus() {
            assertEquals(8, DataFlowTargets.bonus(new int[] {1, 1, 1, 1, 1}, 5));
        }

        @Test
        @DisplayName("def score L49 only; branches present>=T false, best>=5 false")
        void zeroScore_allAbsent() {
            assertEquals(0, DataFlowTargets.bonus(new int[] {0, 0, 0}, 2));
        }

        @Test
        @DisplayName("def streak L46 kill; else branch a<=0")
        void streakKill_midStreakBreak() {
            assertEquals(5, DataFlowTargets.bonus(new int[] {1, 1, 1, 0, 1, 1, 0, 1}, 4));
        }

        @Test
        @DisplayName("p-use attendance L32 null; exception path")
        void invalid_nullAttendance() {
            assertThrows(IllegalArgumentException.class, () -> DataFlowTargets.bonus(null, 1));
        }

        @Test
        @DisplayName("p-use threshold L32; empty array; for-each zero trips")
        void edge_empty_thresholdZero() {
            assertEquals(5, DataFlowTargets.bonus(new int[] {}, 0));
        }
    }
}