package mn.edu.must.sqat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BonusTest {

    /**
     * All-defs minimum is 2: score@49 reaches a use only when line 51 does not run,
     * and score@51 reaches a use only when line 51 does run.
     */
    @Nested
    @DisplayName("All-definitions (minimum set)")
    class AllDefinitions {

        @Test
        @DisplayName("score@49->54, score@54->56, streak@46->41; other defs except score@51")
        void defs_score49_killThenBonus() {
            assertEquals(3, DataFlowTargets.bonus(new int[] {1, 1, 1, 1, 1, 0, 1}, 100));
        }

        @Test
        @DisplayName("score@51->56; line 51 kills score@49 before any use")
        void defs_score51_reachesReturn() {
            assertEquals(5, DataFlowTargets.bonus(new int[] {1, 1, 1}, 3));
        }
    }

    @Nested
    @DisplayName("All-DU-pairs and branch coverage")
    class AllDuPairs {

        @Test
        @DisplayName("DU threshold L31-L50; score L51->L56 (best < 5)")
        void baseOnly_exactThreshold() {
            assertEquals(5, DataFlowTargets.bonus(new int[] {1, 1, 1}, 3));
        }

        @Test
        @DisplayName("DU score L49->L54; bonus only, present < threshold")
        void bonusOnly_belowThreshold_longStreak() {
            assertEquals(3, DataFlowTargets.bonus(new int[] {1, 1, 1, 1, 1}, 10));
        }

        @Test
        @DisplayName("DU best L43; long streak, present < threshold")
        void bestUnchanged_whenStreakNotGreater() {
            assertEquals(3, DataFlowTargets.bonus(new int[] {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, 20));
        }

        @Test
        @DisplayName("DU best(43,42) F; streak L46 then shorter streak")
        void longStreakBroken_middle() {
            assertEquals(3, DataFlowTargets.bonus(new int[] {1, 1, 1, 1, 0, 1, 1, 1, 1, 1}, 100));
        }

        @Test
        @DisplayName("DU present(40,40) streak(41,41) score(51,54); score 8")
        void streakLongerThanFive() {
            assertEquals(8, DataFlowTargets.bonus(new int[] {1, 1, 1, 1, 1, 1, 1}, 3));
        }

        @Test
        @DisplayName("p-use threshold negative L32")
        void invalid_negativeThreshold() {
            assertThrows(IllegalArgumentException.class, () -> DataFlowTargets.bonus(new int[] {1}, -1));
        }

        @Test
        @DisplayName("DU present(35,50) score(49,56) best(37,53) F; all absent")
        void zeroScore_allAbsent() {
            assertEquals(0, DataFlowTargets.bonus(new int[] {0, 0, 0}, 2));
        }

        @Test
        @DisplayName("p-use attendance L32 null; exception path")
        void invalid_nullAttendance() {
            assertThrows(IllegalArgumentException.class, () -> DataFlowTargets.bonus(null, 1));
        }

        @Test
        @DisplayName("DU present(35,50) score(51,56); empty array, threshold 0")
        void edge_empty_thresholdZero() {
            assertEquals(5, DataFlowTargets.bonus(new int[] {}, 0));
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
