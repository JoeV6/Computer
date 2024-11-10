package org.lpc.computer;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Motherboard {
    private CPU cpu;
    private RAM ram;

    public Motherboard() {

    }

    public void boot() {
        this.ram = new RAM(this, 2048, 1024);  // 1KB memory, 1KB stack
        this.cpu = new CPU(this);

        cpu.init();

        // TODO: Load RAM and CPU state from disk
    }

    public void shutdown() {

    }
}
