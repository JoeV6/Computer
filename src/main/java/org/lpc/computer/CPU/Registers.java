package org.lpc.computer.CPU;

public interface Registers {
    /**
     * Registers for the CPU
     *
     */

    // General purpose registers
    byte EAX = 0x00; // Accumulator
    byte EBX = 0x01; // Base
    byte ECX = 0x02; // Counter
    byte EDX = 0x03; // Data

    // Index Registers
    byte ESI = 0x04; // Source Index
    byte EDI = 0x05; // Destination Index

    // Stack Pointer
    byte ESP = 0x06; // Stack Pointer

    // Base Pointer
    byte EBP = 0x07; // Base Pointer

    // Program Counter
    byte IP = 0x08; // Instruction Pointer

    // Zero Flag
    byte ZF = 0x09; // Zero Flag
}
