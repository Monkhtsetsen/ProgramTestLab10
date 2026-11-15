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

    @Nested
    @DisplayName("All-DU-pairs and branch coverage")
    class AllDuPairs {

        @Test
        @DisplayName("DU threshold L31-L50; score L51 base only")
        void baseOnly_exactThreshold() {
            assertEquals(5, DataFlowTargets.bonus(new int[] {1, 1, 1}, 3));
        }

        @Test
        @DisplayName("DU score L54; bonus only best>=5 without base")
        void bonusOnly_belowThreshold_longStreak() {
            assertEquals(3, DataFlowTargets.bonus(new int[] {1, 1, 1, 1, 1}, 10));
        }

        @Test
        @DisplayName("DU best L43 streak>best false branch L42")
        void bestUnchanged_whenStreakNotGreater() {
            assertEquals(3, DataFlowTargets.bonus(new int[] {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, 20));
        }

        @Test
        @DisplayName("DU present L40 accumulated; broken streak before five")
        void longStreakBroken_middle() {
            assertEquals(3, DataFlowTargets.bonus(new int[] {1, 1, 1, 1, 0, 1, 1, 1, 1, 1}, 100));
        }

        @Test
        @DisplayName("DU streak L41 loop; five-plus streak L53")
        void streakLongerThanFive() {
            assertEquals(8, DataFlowTargets.bonus(new int[] {1, 1, 1, 1, 1, 1, 1}, 3));
        }

        @Test
        @DisplayName("p-use threshold negative L32")
        void invalid_negativeThreshold() {
            assertThrows(IllegalArgumentException.class, () -> DataFlowTargets.bonus(new int[] {1}, -1));
        }

        /**
         * Kills M2 (best = present): max streak 3 but present=5 at L43.
         */
        @Test
        @DisplayName("M2-kill: thr=0 streak 3 present 5 at line 43")
        void m2_kill_bestMustTrackStreakNotPresent() {
            assertEquals(5, DataFlowTargets.bonus(new int[] {1, 1, 0, 1, 1, 1}, 0));
        }
    }
}