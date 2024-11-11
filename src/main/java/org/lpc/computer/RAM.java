package org.lpc.computer;


import lombok.Getter;
import lombok.Setter;
import org.lpc.Logger;
import org.lpc.computer.CPU.CPU;

import java.util.Arrays;

import static org.lpc.Logger.*;

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

    int programStart;
    int programEnd;
    int programSize;

    /***
     * ----- Memory Layout -----
     * Example memory layout for 1KB program memory, 1KB stack and 1KB data:
     * 0000 - 1023: Data
     * 1024 - 2047: Program
     * 2048 - 3071: Stack
     */

    public RAM(Motherboard motherboard, int dataSize, int stackSize, int programSize) {
        this.motherboard = motherboard;

        this.memory = new byte[programSize + dataSize + stackSize];
        this.stackSize = stackSize;

        // Set up stack region (starts at the end of the memory and grows downward)
        this.stackStart = memory.length - stackSize; // stack starts at memory[1024]
        this.stackEnd = memory.length; // stack ends at memory[2047]

        // Set up data region (starts at the beginning of the memory)
        this.dataStart = stackStart - dataSize;
        this.dataEnd = stackStart - 1;
        this.dataSize = dataSize;

        // Set up program region (starts at the end of the data region)
        this.programStart = dataStart - programSize;
        this.programEnd = dataStart - 1;
        this.programSize = programSize;

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

    public static byte[] convertIntToBytes(int value) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (value & 0xFF);
        bytes[1] = (byte) ((value >> 8) & 0xFF);
        bytes[2] = (byte) ((value >> 16) & 0xFF);
        bytes[3] = (byte) ((value >> 24) & 0xFF);
        return bytes;
    }

    public static int convertBytesToInt(byte[] bytes) {
        return (bytes[0] & 0xFF) |
                ((bytes[1] & 0xFF) << 8) |
                ((bytes[2] & 0xFF) << 16) |
                ((bytes[3] & 0xFF) << 24);
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
                Program {
                    size=%d,
                    start=%d,
                    end=%d
                }
            }
            """.formatted(memory.length, stackSize, stackStart, stackEnd, dataSize, dataStart, dataEnd, programSize, programStart, programEnd);
    }

    public String dump() {
        StringBuilder sb = new StringBuilder();

        // Define and iterate over each memory segment
        sb.append(ANSI_RED).append(strLine("Dumping memory segments", 100)).append(ANSI_RESET);


        dumpSegment(sb, "Program", ANSI_PURPLE, programStart, programEnd);
        dumpSegment(sb, "Data", ANSI_BLUE, dataStart, dataEnd);
        dumpSegment(sb, "Stack", ANSI_GREEN, stackStart, stackEnd);

        sb.append(ANSI_RED).append(strLine(100)).append(ANSI_RESET);

        return sb.toString();
    }

    // Helper method to dump a segment
    private void dumpSegment(StringBuilder sb, String name, String color, int start, int end) {
        sb.append(ANSI_YELLOW).append(strLine(name, 100)).append(ANSI_RESET);

        for (int i = start; i < end; i += 4) {
            int value = readWord(i);
            String opcode = cpu.getOpcodeName(memory[i]);

            // Print non-zero memory contents only
            if (value != 0) {
                sb.append(String.format(
                        color + "%08X %08X %08X %08X (0x%04X : %04d) [int: %06d] | opcode: %s" + Logger.ANSI_RESET + "\n",
                        memory[i] & 0xFF, memory[i + 1] & 0xFF, memory[i + 2] & 0xFF, memory[i + 3] & 0xFF,
                        i, i, value, opcode));
            }
        }
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
