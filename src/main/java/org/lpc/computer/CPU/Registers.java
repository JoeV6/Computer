package org.lpc.computer.CPU;

public interface Registers {
    /**
     * Registers for the CPU
     * 1 byte each
     * 0x00 - 0x0F: General purpose registers
     * 0x10 - 0x1F: Index registers
     * 0x20 - 0x2F: Stack Pointer
     * 0x30 - 0x3F: Base Pointer
     * 0x40 - 0x4F: Program Counter
     * 0x50 - 0xFE: Reserved for future use
     * 0xFF: -1, Invalid register
     */

    // General purpose registers
    byte EAX = 0x00; // Accumulator
    byte EBX = 0x01; // Base
    byte ECX = 0x02; // Counter
    byte EDX = 0x03; // Data

    // Index Registers
    byte ESI = 0x1; // Source Index
    byte EDI = 0x11; // Destination Index

    // Stack Pointer
    byte ESP = 0x20; // Stack Pointer

    // Base Pointer
    byte EBP = 0x21; // Base Pointer

    // Program Counter
    byte IP = 0x30; // Instruction Pointer

    // Zero Flag
    byte ZF = 0x40; // Zero Flag
}
