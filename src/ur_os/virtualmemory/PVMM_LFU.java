/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.virtualmemory;

import java.util.LinkedList;
import ur_os.memory.paging.PageTable;

/**
 *
 * @author user
 */
public class PVMM_LFU extends ProcessVirtualMemoryManager{

    public PVMM_LFU(){
        type = ProcessVirtualMemoryManagerType.LFU;
    }
    
    @Override
    public int getVictim(LinkedList<Integer> memoryAccesses, PageTable pt) {
         LinkedList<Integer> validPages = new LinkedList();
        int pageIndex = 0;
        for (ur_os.memory.paging.PageTableEntry pte : pt.getList()) {
            if (pte.isValid()) {
                validPages.add(pageIndex);
            }
            pageIndex++;
        }

        int victim = -1;
        int minFreq = Integer.MAX_VALUE;

        for (Integer page : validPages) {
            int freq = 0;
            for (Integer accessedPage : memoryAccesses) {
                if (page.equals(accessedPage)) {
                    freq++;
                }
            }

            if (freq < minFreq) {
                minFreq = freq;
                victim = page;
            }
        }

        return victim;
    }
}
