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
        
        LinkedList<Integer> pages = new LinkedList();
        int size = 0; 
        LinkedList<Integer> validListPages = new LinkedList();
        int i = 0;
        for (PageTableEntry pte : pt.getList()) {
            if (pte.isValid()) {
                validListPages.add(i);
            }
            i++;
        }
        int temp;
        while (size < memoryAccesses.size() && pages.size() < validListPages.size()) {
            temp = memoryAccesses.get(size);
            if (!pages.contains(temp) && validListPages.contains(temp)) {
                pages.add(temp);
            }
            size++;
        }
        return pages.getFirst();
    }
    
}
