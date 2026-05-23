package ur_os.virtualmemory;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import ur_os.memory.paging.PageTable;
import ur_os.memory.paging.PageTableEntry;

public class PVMM_CLOCK extends ProcessVirtualMemoryManager {

    private int handIndex = 0; // clock hand — persists across fault calls for this process

    public PVMM_CLOCK() {
        type = ProcessVirtualMemoryManagerType.CLOCK;
    }

    @Override
    public int getVictim(LinkedList<Integer> memoryAccesses, PageTable pt) {
        LinkedList<Integer> validListPages = new LinkedList<>();
        int i = 0;
        for (PageTableEntry pte : pt.getList()) {
            if (pte.isValid()) validListPages.add(i);
            i++;
        }
        if (validListPages.isEmpty()) return -1;

        int size = validListPages.size();
        handIndex = handIndex % size;

        // Reference bit = 1 if page appears in memoryAccesses (was hit), 0 otherwise
        Set<Integer> referenced = new HashSet<>(memoryAccesses);

       
        for (int sweep = 0; sweep < 2 * size; sweep++) {
            int idx = (handIndex + sweep) % size;
            int pg = validListPages.get(idx);
            if (!referenced.contains(pg)) {
                handIndex = (idx + 1) % size; 
                return pg;
            }
            referenced.remove(pg); 
        }
        return validListPages.get(handIndex % size);
    }
}
