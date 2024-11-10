package org.lpc;

import org.lpc.computer.CPU.CPU;
import org.lpc.computer.Motherboard;
import org.lpc.computer.RAM;

import static org.lpc.Logger.*;

public class Main {
    static RAM ram;
    static CPU cpu;

    public static void main(String[] args) {
        Motherboard motherboard = new Motherboard(1024, 1024, 1024);

        motherboard.boot();

        cpu = motherboard.getCpu();
        ram = motherboard.getRam();

        startColor(ANSI_YELLOW);
            log(cpu, ram);
        endColor();

        startColor(ANSI_GREEN);
            log(ram.dump());
        endColor();

        cpu.run();

        startColor(ANSI_BLUE);
            log(ram.dump());
        endColor();

        startColor(ANSI_YELLOW);
            log(cpu, ram);
        endColor();

        motherboard.shutdown();
    }
}