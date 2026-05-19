/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.virtualmemory;

import java.util.LinkedList;
import ur_os.memory.paging.PageTable;
import ur_os.memory.paging.PageTableEntry;
/**
 *
 * @author user
 */
public class PVMM_FIFO extends ProcessVirtualMemoryManager{

    public PVMM_FIFO(){
        type = ProcessVirtualMemoryManagerType.FIFO;
    }
    
    @Override
 public int getVictim(LinkedList<Integer> memoryAccesses, PageTable pt) {
        
        int victimPage = -1;
        int oldestLoadClock = Integer.MAX_VALUE;
        int pageIndex = 0;

        for (PageTableEntry pte : pt.getList()) {
            if (pte.isValid()) {
                if (pte.getClock() < oldestLoadClock) {
                    oldestLoadClock = pte.getClock();
                    victimPage = pageIndex;
                }
            }
            pageIndex++;
        }

        return victimPage;
    }   
        
}
