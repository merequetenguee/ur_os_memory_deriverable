package ur_os.memory.freememorymagament;


/**
 *
 * @author super
 */
public class NextFitMemorySlotManager extends FreeMemorySlotManager{
    int point=0;

    public NextFitMemorySlotManager(int memSize){
        super(memSize);
    }

    @Override
    public MemorySlot getSlot(int size) {
        MemorySlot m;
        //System.out.println("length");
        //System.out.println(list.size());
        while (((point+1)%(list.size()+1))!=0){

            m=list.get(point%list.size());
            if (m.canContain(size)){
                //System.out.println("allocate command on point/ size/slotsize");
                //System.out.println(point);
                //System.out.println(size);
                //System.out.println(m.getSize());

                if (m.size == size){
                    System.out.println("equal size");

                    list.remove(point%list.size());
                    return m;
                }
                m=m.assignMemory(size);
                return m;

            }
            point++;


        }
        point=point-list.size();
        System.out.println("not possible to allocate");
        return null;

    }



}