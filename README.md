# CPU Emulator in Java

A simple CPU emulator written in Java, simulating the basic operations of a CPU, including registers, stack, and basic memory management.

## Overview

This project aims to simulate the behavior of a CPU in a simplified environment. It includes:
- **Registers**: General-purpose and special-purpose registers (e.g., `EAX`, `EBX`, `ESP`, `EBP`).
- **Stack**: A stack for managing function calls and local variables, implemented in RAM.
- **Memory (RAM)**: Simulated RAM that holds program data and supports basic read/write operations.
- **Stack Operations**: Methods for pushing and popping values onto/from the stack.
- **Function Prologue/Epilogue**: Simulating stack frames for function calls with basic function prologue and epilogue handling.

### Key Features:
- **1-Byte Instruction Set**: Each instruction is represented by a 1-byte opcode.
- **CPU Registers**: A set of general-purpose, index, stack pointer, base pointer, and program counter registers.
- **Zero Flag (ZF)**: Used for conditional jumps (e.g., `JZ`, `JNZ`).
- **Stack Operations**: Supports basic stack operations like `PUSH` and `POP`.
- **Control Flow**: Implements conditional jumps and function calls with `CALL` and `RET`.

---

## Instruction Set

### Data Transfer Instructions (0x00 - 0x0F)
- **MOV** (0x01)  
  `MOV dst, src`: Move data from `src` register to `dst` register.

- **MOV_I** (0x02)  
  `MOV dst, imm`: Move an immediate value into a register.  
  **Format**: [Opcode (1 byte), Destination Register (1 byte), Padding (2 bytes), Immediate Value (4 bytes)].

- **LOAD** (0x03)  
  `LOAD reg, address`: Loads data from memory address into a register.  
  **Format**: [Opcode (1 byte), Register (1 byte), Padding (2 bytes), Address (4 bytes)].

- **STORE** (0x04)  
  `STORE address, reg`: Stores data from a register into memory.  
  **Format**: [Opcode (1 byte), Register (1 byte), Padding (2 bytes), Address (4 bytes)].

### Arithmetic Instructions (0x10 - 0x1F)
- **ADD** (0x10)  
  `ADD dst, src`: Add the value of `src` to `dst` and store the result in `dst`.

- **SUB** (0x11)  
  `SUB dst, src`: Subtract the value of `src` from `dst` and store the result in `dst`.

- **MUL** (0x12)  
  `MUL dst, src`: Multiply the value of `dst` by `src` and store the result in `dst`.

- **DIV** (0x13)  
  `DIV dst, src`: Divide `dst` by `src` and store the result in `dst`.

### Logical Instructions (0x20 - 0x2F)
- **AND** (0x20)  
  `AND dst, src`: Perform a bitwise AND between `dst` and `src`.

- **OR** (0x21)  
  `OR dst, src`: Perform a bitwise OR between `dst` and `src`.

- **XOR** (0x22)  
  `XOR dst, src`: Perform a bitwise XOR between `dst` and `src`.

- **NOT** (0x23)  
  `NOT reg`: Perform a bitwise NOT operation on `reg`.

### Control Flow Instructions (0x30 - 0x3F)
- **JMP** (0x30)  
  `JMP address`: Jump to a specific memory address.

- **JZ** (0x31)  
  `JZ address`: Jump to a memory address if the Zero Flag (`ZF`) is set.

- **JNZ** (0x32)  
  `JNZ address`: Jump to a memory address if the Zero Flag (`ZF`) is not set.

- **CALL** (0x33)  
  `CALL address`: Push the current Instruction Pointer (IP) onto the stack and jump to the address.

- **RET** (0x34)  
  `RET`: Pop the address from the stack and set it as the new Instruction Pointer (IP).

### Stack Operations (0x40 - 0x4F)
- **PUSH** (0x40)  
  `PUSH reg`: Push the value of the register onto the stack.

- **POP** (0x41)  
  `POP reg`: Pop a value from the stack into the register.

### Reserved Instructions (0x50 - 0xFE)
- Reserved for future use. These opcodes are currently unused but are set aside for potential extension of the instruction set.

### Invalid Instruction (0xFF)
- **Invalid Opcode**: This is used when an invalid opcode is encountered, and the CPU should raise an error.

---

## Registers

The CPU includes a set of registers, each represented by a 1-byte value.

### General Purpose Registers (0x00 - 0x0F)
- **EAX** (0x00) - Accumulator
- **EBX** (0x01) - Base
- **ECX** (0x02) - Counter
- **EDX** (0x03) - Data

### Index Registers (0x10 - 0x1F)
- **ESI** (0x10) - Source Index
- **EDI** (0x11) - Destination Index

### Special Purpose Registers
- **ESP** (0x20) - Stack Pointer
- **EBP** (0x21) - Base Pointer
- **IP** (0x30) - Instruction Pointer
- **ZF** (0x40) - Zero Flag (used for conditional jumps)

### Reserved Registers (0x50 - 0xFE)
- Reserved for future use.

---

## Zero Flag (`ZF`)
The Zero Flag is used by control flow instructions to determine the outcome of conditional jumps:
- **`JZ`** (Jump if Zero): Jumps if the Zero Flag (`ZF`) is set.
- **`JNZ`** (Jump if Not Zero): Jumps if the Zero Flag (`ZF`) is not set.

### Zero Flag Behavior:
- After arithmetic and logical operations like `ADD`, `SUB`, `MUL`, etc., the Zero Flag is updated to `true` if the result is `0`.
- The Zero Flag is checked before executing conditional jumps to control the flow of the program.

---

## How to Use
1. **Initialize the CPU**: Create an instance of the CPU emulator and load the instructions into memory.
2. **Execute Instructions**: Fetch and decode instructions based on their opcodes. The CPU will execute them in sequence, modifying registers and memory.
3. **Control Flow**: Use `JMP`, `JZ`, `JNZ`, `CALL`, and `RET` to control the flow of execution.
4. **Stack Operations**: Use `PUSH` and `POP` to manage the stack.

---

## Future Extensions
- **Additional Instructions**: More arithmetic, logical, and control flow instructions may be added to extend the instruction set.
- **Interrupt Handling**: Implement interrupts and handling for real-time events.
- **I/O Operations**: Add instructions for input and output handling.

---
