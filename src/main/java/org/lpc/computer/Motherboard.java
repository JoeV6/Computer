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
    private int ramProgramSize;

    public Motherboard(int ramStackSize, int ramDataSize, int ramProgramSize) {
        this.ramMemorySize = ramProgramSize + ramDataSize + ramStackSize;
        this.ramStackSize = ramStackSize;
        this.ramDataSize = ramDataSize;
        this.ramProgramSize = ramProgramSize;
    }

    public void boot() {
        this.ram = new RAM(this, ramDataSize, ramStackSize, ramProgramSize);
        this.cpu = new CPU(this);

        cpu.init();
        ram.init();

        cpu.loadProgram("src/main/resources/programs/test.asm");

        // TODO: Load RAM and CPU state from disk
    }

    public void shutdown() {

    }
}
