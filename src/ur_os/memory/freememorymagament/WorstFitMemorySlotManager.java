/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.memory.freememorymagament;

/**
 *
 * @author super
 */
public class WorstFitMemorySlotManager extends FreeMemorySlotManager{
    
    public WorstFitMemorySlotManager(int memSize){
        super(memSize);
    }
    @Override

    public MemorySlot getSlot(int size) {
    System.out.println("\n==============================");
    System.out.println("WORST FIT -> " + size);

    MemorySlot m = null;

    for (MemorySlot slot : list) {
        System.out.println("Slot disponible: " + slot.getSize());

        if (slot.canContain(size)) {
            if (m == null || slot.getSize() > m.getSize()) {
                m = slot;
            }
        }
    }

    if (m == null) {
        System.out.println(" No hay espacio suficiente");
        return null;
    }
   
    System.out.println(" Bloque elegido (debe ser el mayor) " + m.getSize());

    if (m.getSize() == size) {
        list.remove(m);
        return m;
    }

    return m.assignMemory(size);
}
}