package ur_os.memory.freememorymagament;


public class FlexFitMemorySlotManager extends FreeMemorySlotManager{
    float alpha;

    public FlexFitMemorySlotManager(int memSize){
        super(memSize);
        alpha = 1.5f;


    }

    @Override
    public MemorySlot getSlot(int size) {
        MemorySlot m;
        //System.out.println(list.size());
        for (MemorySlot memorySlot : list) {
            if(memorySlot.getSize()<= alpha*size && memorySlot.getSize()>=size){
                if(memorySlot.getSize() == size){
                    //System.out.println(memorySlot);
                    /*If the requested amount is the slot's size, then the slot
                      is removed from the list, and the original one is sent to
                      the process
                    */
                    m = memorySlot;
                    list.remove(m);
                    return m;
                }else{
                    //System.out.println(memorySlot);
                    /*If the requested amount is not the slot's size, then a new
                      memory slot is created to be returned and the existing one
                      is updated*/
                    m = memorySlot.assignMemory(size);
                    return m;
                }
            }
        }
        System.out.println("falling to first fit");
        for (MemorySlot memorySlot : list) {
            if( memorySlot.getSize()>=size){
                if(memorySlot.getSize() == size){
                    //System.out.println(memorySlot);
                    /*If the requested amount is the slot's size, then the slot
                      is removed from the list, and the original one is sent to
                      the process
                    */
                    m = memorySlot;
                    list.remove(m);
                    return m;
                }else{
                    //System.out.println(memorySlot);
                    /*If the requested amount is not the slot's size, then a new
                      memory slot is created to be returned and the existing one
                      is updated*/
                    m = memorySlot.assignMemory(size);
                    return m;
                }
            }
        }


        //If there is no slot big enough to contain the requested memory, it will return null
        System.out.println("Error - Memory cannot allocate a slot big enough for the requested memory");
        return null;
    }



}