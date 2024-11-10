package org.lpc.computer;

import lombok.Getter;
import lombok.Setter;
import org.lpc.computer.CPU.CPU;

@Setter
@Getter
public class Motherboard {
    private CPU cpu;
    private RAM ram;

    private int ramMemorySize;
    private int ramStackSize;
    private int ramDataSize;

    public Motherboard(int ramMemorySize, int ramStackSize) {
        this.ramMemorySize = ramMemorySize;
        this.ramStackSize = ramStackSize;
        this.ramDataSize = ramMemorySize - ramStackSize;
    }

    public void boot() {
        this.ram = new RAM(this, ramMemorySize, ramStackSize);  // 1KB memory, 1KB stack
        this.cpu = new CPU(this);

        cpu.init();
        ram.init();

        // TODO: Load RAM and CPU state from disk
    }

    public void shutdown() {

    }
}
