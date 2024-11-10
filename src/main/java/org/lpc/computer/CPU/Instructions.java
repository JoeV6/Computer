package org.lpc.computer.CPU;

public interface Instructions {
    /**
     * Instruction set for the CPU
     * 1 Byte instruction set
     * 0x00 - 0x0F: Basic data transfer instructions
     * 0x10 - 0x1F: Arithmetic instructions
     * 0x20 - 0x2F: Logical instructions
     * 0x30 - 0x3F: Control flow instructions
     * 0x40 - 0x4F: Stack operations
     * 0x50 - 0xFF: Reserved for future use
     */

    // Basic data transfer instructions
    byte MOV = 0x01;  // MOV dst, src: Moves data from src to dst
    byte LOAD = 0x02; // LOAD reg, address: Loads data from memory address to register
    byte STORE = 0x03; // STORE address, reg: Stores register value at memory address

    //Arithmetic instructions
    byte ADD = 0x10;  // ADD dst, src: Adds src to dst and stores result in dst
    byte SUB = 0x11;  // SUB dst, src: Subtracts src from dst and stores result in dst
    byte MUL = 0x12;  // MUL dst, src: Multiplies dst by src and stores result in dst
    byte DIV = 0x13;  // DIV dst, src: Divides dst by src and stores result in dst

    //Logical instructions
    byte AND = 0x20;  // AND dst, src: Bitwise AND of dst and src
    byte OR = 0x21;   // OR dst, src: Bitwise OR of dst and src
    byte XOR = 0x22;  // XOR dst, src: Bitwise XOR of dst and src
    byte NOT = 0x23;  // NOT reg: Bitwise NOT of reg

    //Control flow instructions
    byte JMP = 0x30;  // JMP address: Jump to memory address
    byte JZ = 0x31;   // JZ address: Jump if zero flag (ZF) is set
    byte JNZ = 0x32;  // JNZ address: Jump if zero flag (ZF) is not set
    byte CALL = 0x33; // CALL address: Call function at address (push IP to stack)
    byte RET = 0x34;  // RET: Return from function (pop address from stack to IP)

    //Stack operations
    byte PUSH = 0x40; // PUSH reg: Push register value onto the stack
    byte POP = 0x41;  // POP reg: Pop value from the stack into register
}
