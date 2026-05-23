package ur_os.virtualmemory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import ur_os.memory.paging.PageTable;

/**
 * Verbose test showing the internal process of each page replacement algorithm.
 *
 * Scenario A — ref: 0, 1, 2, 1, 1, 1, 3
 * Scenario B — ref: 0, 1, 2, 2, 0, 1, 3 (splits FIFO from LRU)
 */
public class VirtualMemoryAlgorithmsTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {

        runScenario("A", new int[] { 0, 1, 2, 1, 1, 1, 3 }, 3,
                map("FIFO", 0, "LRU", 0, "MRU", 1, "LFU", 2, "MFU", 1));

        runScenario("B", new int[] { 0, 1, 2, 2, 0, 1, 3 }, 3,
                map("FIFO", 0, "LRU", 2, "MRU", 1, "LFU", 1, "MFU", 0));

        System.out.println();
        System.out.println("══════════════════════════════════════════════");
        System.out.printf("  %d PASSED   %d FAILED%n", passed, failed);
        System.out.println("══════════════════════════════════════════════");
        if (failed > 0)
            System.exit(1);
    }

    // ── Scenario runner ─────────────────────────────────────────────────────

    static void runScenario(String name, int[] refs, int frames, Map<String, Integer> expected) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.printf("║  SCENARIO %s  │  ref: %s%n", name, joinInts(refs));
        System.out.printf("║  %d frames, PAGE_SIZE=64, pg0 pre-loaded%n", frames);
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        // ── Simulate the reference string ────────────────────────────────────
        boolean[] valid = new boolean[10];
        int[] pgFrame = new int[10];
        valid[0] = true;
        pgFrame[0] = 0;
        int loadedPages = 1, nextFrame = 1;
        LinkedList<Integer> ma = new LinkedList<>();

        System.out.println();
        System.out.printf("  %-5s %-4s  %-34s %-18s  %s%n",
                "Step", "Ref", "Frames (pg@frame)", "memoryAccesses", "Event");
        System.out.println("  " + "─".repeat(78));

        for (int step = 1; step <= refs.length; step++) {
            int pg = refs[step - 1];
            String event;
            if (valid[pg]) {
                ma.add(pg);
                event = "HIT";
            } else if (loadedPages < frames) {
                valid[pg] = true;
                pgFrame[pg] = nextFrame++;
                loadedPages++;
                event = "FAULT  (load pg" + pg + ")";
            } else {
                event = "FAULT  --> VICTIM NEEDED";
            }
            System.out.printf("  %-5d %-4d  %-34s %-18s  %s%n",
                    step, pg, framesStr(valid, pgFrame), ma.toString(), event);
        }

        // Collect valid pages and build PageTable for the algorithms
        LinkedList<Integer> validPages = new LinkedList<>();
        for (int p = 0; p < 10; p++)
            if (valid[p])
                validPages.add(p);

        PageTable pt = new PageTable((validPages.size() - 1) * 64, validPages.size(), true);
        for (int p : validPages)
            pt.addFrameID(pgFrame[p], true);

        int faultPage = refs[refs.length - 1];
        System.out.println();
        System.out.printf("  Fault on pg%d  │  valid=%s  │  ma=%s%n", faultPage, validPages, ma);

        // ── Run each algorithm with a verbose trace ──────────────────────────
        System.out.println();
        runAlgo("FIFO", new PVMM_FIFO(), ma, pt, validPages, expected.getOrDefault("FIFO", -1));
        runAlgo("LRU", new PVMM_LRU(), ma, pt, validPages, expected.getOrDefault("LRU", -1));
        runAlgo("MRU", new PVMM_MRU(), ma, pt, validPages, expected.getOrDefault("MRU", -1));
        runAlgo("LFU", new PVMM_LFU(), ma, pt, validPages, expected.getOrDefault("LFU", -1));
        runAlgo("MFU", new PVMM_MFU(), ma, pt, validPages, expected.getOrDefault("MFU", -1));
    }

    static void runAlgo(String name, ProcessVirtualMemoryManager algo,
            LinkedList<Integer> ma, PageTable pt,
            LinkedList<Integer> validPages, int expected) {
        System.out.println("  ┌─ " + name);
        traceAlgo(name, ma, validPages);
        int victim = algo.getVictim(new LinkedList<>(ma), pt);
        boolean ok = (victim == expected);
        if (ok)
            passed++;
        else
            failed++;
        System.out.printf("  └─ [%s] victim = pg%d%s%n%n",
                ok ? "PASS" : "FAIL", victim,
                ok ? "" : "  (expected pg" + expected + ")");
    }

    // ── Per-algorithm trace ──────────────────────────────────────────────────

    static void traceAlgo(String name, LinkedList<Integer> ma, LinkedList<Integer> valid) {
        switch (name) {

            case "FIFO": {
                System.out.println("  │  Scan →  (forward, oldest → newest)  ma = " + ma);
                LinkedList<Integer> seen = new LinkedList<>();
                for (int j = 0; j < ma.size(); j++) {
                    int pg = ma.get(j);
                    if (!seen.contains(pg) && valid.contains(pg)) {
                        seen.add(pg);
                        System.out.printf("  │    [%d] pg%d  new unique valid  → seen=%s%n", j, pg, seen);
                    } else {
                        System.out.printf("  │    [%d] pg%d  %s → skip%n",
                                j, pg, seen.contains(pg) ? "duplicate " : "not valid ");
                    }
                }
                System.out.println("  │  order: " + seen + "  →  getFirst() = pg" + seen.getFirst()
                        + "  (oldest first-accessed)");
                break;
            }

            case "LRU": {
                System.out.println("  │  Scan ←  (backward, newest → oldest)  ma = " + ma);
                LinkedList<Integer> seen = new LinkedList<>();
                for (int j = ma.size() - 1; j >= 0; j--) {
                    int pg = ma.get(j);
                    if (!seen.contains(pg) && valid.contains(pg)) {
                        seen.add(pg);
                        System.out.printf("  │    [%d] pg%d  new unique valid  → seen=%s%n", j, pg, seen);
                    } else {
                        System.out.printf("  │    [%d] pg%d  duplicate → skip%n", j, pg);
                    }
                }
                System.out.println("  │  order: " + seen + "  →  getLast()  = pg" + seen.getLast()
                        + "  (least recently used)");
                break;
            }

            case "MRU": {
                System.out.println("  │  Scan ←  (backward, newest → oldest)  ma = " + ma);
                LinkedList<Integer> seen = new LinkedList<>();
                for (int j = ma.size() - 1; j >= 0; j--) {
                    int pg = ma.get(j);
                    if (!seen.contains(pg) && valid.contains(pg)) {
                        seen.add(pg);
                        System.out.printf("  │    [%d] pg%d  new unique valid  → seen=%s%n", j, pg, seen);
                    } else {
                        System.out.printf("  │    [%d] pg%d  duplicate → skip%n", j, pg);
                    }
                }
                System.out.println("  │  order: " + seen + "  →  getFirst() = pg" + seen.getFirst()
                        + "  (most recently used)");
                break;
            }

            case "LFU":
            case "MFU": {
                boolean lfu = name.equals("LFU");
                System.out.println("  │  Count frequency of each valid page in ma = " + ma);
                int bestFreq = lfu ? Integer.MAX_VALUE : -1;
                int bestPg = -1;
                for (int pg : valid) {
                    int freq = 0;
                    for (int acc : ma)
                        if (acc == pg)
                            freq++;
                    boolean newBest = lfu ? (freq < bestFreq) : (freq > bestFreq);
                    if (newBest) {
                        bestFreq = freq;
                        bestPg = pg;
                    }
                    System.out.printf("  │    pg%d  freq=%-3d%s%n", pg, freq,
                            newBest ? "  ← new " + (lfu ? "min" : "max") : "");
                }
                System.out.printf("  │  → pg%d has %s freq=%d%n",
                        bestPg, lfu ? "min" : "max", bestFreq);
                break;
            }
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    static String framesStr(boolean[] valid, int[] pgFrame) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (int p = 0; p < 10; p++) {
            if (valid[p]) {
                if (!first)
                    sb.append(", ");
                sb.append("pg").append(p).append("@f").append(pgFrame[p]);
                first = false;
            }
        }
        return sb.append("]").toString();
    }

    static String joinInts(int[] a) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < a.length; i++) {
            if (i > 0)
                sb.append(", ");
            sb.append(a[i]);
        }
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    static Map<String, Integer> map(Object... pairs) {
        Map<String, Integer> m = new HashMap<>();
        for (int i = 0; i < pairs.length; i += 2)
            m.put((String) pairs[i], (Integer) pairs[i + 1]);
        return m;
    }
}