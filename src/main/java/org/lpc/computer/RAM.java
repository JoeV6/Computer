package org.lpc.computer;


import lombok.Getter;
import lombok.Setter;
import org.lpc.computer.CPU.CPU;

import java.util.Arrays;

import static org.lpc.Logger.logErr;

@Getter @Setter
public class RAM {
    Motherboard motherboard;
    CPU cpu;

    byte[] memory;  // A single memory array for both stack and data
    int stackStart;
    int stackEnd;
    int stackSize;

    int dataStart;
    int dataEnd;
    int dataSize;

    /***
     * ----- Memory Layout -----
     * Example memory layout for 1KB program memory, 1KB stack and 1KB data:
     * 0000 - 1023: Data
     * 1024 - 2047: Program
     * 2048 - 3071: Stack
     */

    public RAM(Motherboard motherboard, int memorySize, int stackSize) {
        this.motherboard = motherboard;

        this.memory = new byte[memorySize];
        this.stackSize = stackSize;

        // Set up stack region (starts at the end of the memory and grows downward)
        this.stackStart = memorySize - stackSize; // stack starts at memory[1024]
        this.stackEnd = memorySize; // stack ends at memory[2047]

        // Set up data region (starts at the beginning of the memory)
        this.dataStart = 0;
        this.dataEnd = stackStart - 1; // data ends right before the stack starts
        this.dataSize = dataEnd + 1;

        reset();
    }

    public void init() {
        this.cpu = motherboard.getCpu();
    }

    public void write(int address, byte value) {
        if (address >= 0 && address < memory.length) {
            memory[address] = value;
        } else {
            logErr("Memory address out of bounds");
        }
    }

    public byte read(int address) {
        if (address >= 0 && address < memory.length) {
            return memory[address];
        } else {
            logErr("Memory address out of bounds");
            return 0;
        }
    }

    // -------- Stack operations --------

    public void writeWord(int value, int address) {
        write(address, (byte) (value & 0xFF));
        write(address + 1, (byte) ((value >> 8) & 0xFF));
        write(address + 2, (byte) ((value >> 16) & 0xFF));
        write(address + 3, (byte) ((value >> 24) & 0xFF));
    }

    public int readWord(int address) {
        return (read(address) & 0xFF) |
                ((read(address + 1) & 0xFF) << 8) |
                ((read(address + 2) & 0xFF) << 16) |
                ((read(address + 3) & 0xFF) << 24);
    }

    // 64-bit operations, don't know if this is necessary
    public void writeDWord(long value, int address) {
        writeWord((int) (value & 0xFFFFFFFFL), address);
        writeWord((int) (value >> 32), address + 4);
    }

    public long readDWord(int address) {
        long lower = readWord(address) & 0xFFFFFFFFL;
        long upper = readWord(address + 4) & 0xFFFFFFFFL;
        return (upper << 32) | lower;
    }


    public void reset() {
        Arrays.fill(memory, (byte) 0);  // Reset all memory to 0
    }

    @Override
    public String toString() {
        return """
            RAM {
                memorySize=%d,
                
                Stack {
                    size=%d,
                    start=%d,
                    end=%d
                },
                Data {
                    size=%d,
                    start=%d,
                    end=%d
                }
            }
            """.formatted(memory.length, stackSize, stackStart, stackEnd, dataSize, dataStart, dataEnd);
    }

    public String Dump() {
        StringBuilder sb = new StringBuilder();
        int address = 0;

        // Iterate through memory in steps of 4 bytes (word size)
        for (int i = address; i < memory.length; i += 4) {
            int value = readWord(i);

            // If the value is non-zero, print it
            if (value != 0) {
                sb.append(String.format("%08X %08X %08X %08X (0x%04X : %04d) [int: %06d]\n",
                        memory[i] & 0xFF, memory[i + 1] & 0xFF,
                        memory[i + 2] & 0xFF, memory[i + 3] & 0xFF, i, i, value));
            }
        }

        return sb.toString();
    }

    public String DumpHex() {
        StringBuilder sb = new StringBuilder();

        // Iterate through memory in steps of 4 bytes (word size)
        for (int i = 0; i < memory.length; i += 4) {
            int value = readWord(i);

            // If the value is non-zero, print it in hexadecimal
            if (value != 0) {
                sb.append(String.format("%02X %02X %02X %02X (0x%04X : %04d) [hex: 0x%08X]\n",
                        memory[i] & 0xFF, memory[i + 1] & 0xFF,
                        memory[i + 2] & 0xFF, memory[i + 3] & 0xFF, i, i, value));
            }
        }

        return sb.toString();
    }


    public String DumpAll(){
        StringBuilder sb = new StringBuilder();
        int address = 0;

        for (int i = 0; i < memory.length; i += 4) {
            int value = readWord(i);

            sb.append(String.format("%08X %08X %08X %08X (0x%04X : %04d) [int: %d]\n",
                    memory[i] & 0xFF, memory[i + 1] & 0xFF,
                    memory[i + 2] & 0xFF, memory[i + 3] & 0xFF, i, i, value));
        }

        return sb.toString();
    }
}
