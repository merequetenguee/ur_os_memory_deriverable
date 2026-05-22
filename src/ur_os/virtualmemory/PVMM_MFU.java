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
public class PVMM_MFU extends ProcessVirtualMemoryManager{

    public PVMM_MFU(){
        type = ProcessVirtualMemoryManagerType.MFU;
    }
    
    @Override
    public int getVictim(LinkedList<Integer> memoryAccesses, PageTable pt) {
        
      LinkedList<Integer> validListPages = new LinkedList();
        int i = 0;
        for (PageTableEntry pte : pt.getList()) {
            if (pte.isValid()) {
                validListPages.add(i);
            }
            i++;
        }
        int victim = -1;
        int maxFreq = -1;
        for (int page : validListPages) {
            int freq = 0;
            for (int size = 0; size < memoryAccesses.size(); size++) {
                if (memoryAccesses.get(size) == page) freq++;
            }
            if (freq > maxFreq) {
                maxFreq = freq;
                victim = page;
            }
        }
        return victim;
        
    }
    
}

