package org.lpc;

import org.lpc.computer.CPU;
import org.lpc.computer.Motherboard;
import org.lpc.computer.RAM;

import static org.lpc.Logger.*;

public class Main {
    static RAM ram;
    static CPU cpu;

    public static void main(String[] args) {
        Motherboard motherboard = new Motherboard();

        motherboard.boot();
        motherboard.shutdown();

        CPU cpu = motherboard.getCpu();
        RAM ram = motherboard.getRam();

        startColor(ANSI_YELLOW);
            log(cpu, ram);
        endColor();

        ram.writeWord(100000, 0x07FC);
        ram.writeWord(4, 2040);

        logln(
            ram.readWord(0x07FC),
            ram.readWord(2040)
        );

        startColor(ANSI_GREEN);
            log(ram.prettyDump());
        endColor();



        cpu.push(100);
        cpu.push(200);

        logln(
            cpu.pop(),
            cpu.pop()
        );

    }
}