# CPU Emulator in Java

A simple CPU emulator written in Java, simulating the basic operations of a CPU, including registers, stack, and basic memory management.

## Overview

This project aims to simulate the behavior of a CPU in a simplified environment. It includes:
- **Registers**: General-purpose and special-purpose registers (e.g., `EAX`, `EBX`, `ESP`, `EBP`).
- **Stack**: A stack for managing function calls and local variables, implemented in RAM.
- **Memory (RAM)**: Simulated RAM that holds program data and supports basic read/write operations.
- **Stack Operations**: Methods for pushing and popping values onto/from the stack.
- **Function Prologue/Epilogue**: Simulating stack frames for function calls with basic function prologue and epilogue handling.

## Features

- **Registers**: Support for 32-bit and 64-bit registers (e.g., `EAX`, `EBX`, `RAX`, `RBX`).
- **Stack Management**: Push and pop operations for managing function calls and local variables in the stack.
- **Memory (RAM)**: Simulated memory using a byte array to store values.
- **Basic Flags**: Support for basic flags like `ZF` (Zero Flag).
- **Function Simulation**: Simulates function prologue and epilogue for managing stack frames.

### `CPU.java`
The `CPU` class simulates the CPU's registers, stack, and basic operations. It includes methods for interacting with the stack (push/pop), managing registers, and simulating a very basic CPU cycle.


### `RAM.java`
The `RAM` class simulates system memory (RAM), providing methods for reading and writing data. The stack is implemented as part of this RAM class.

## How It Works

1. **CPU Class**:
    - The `CPU` class contains general-purpose registers (e.g., `EAX`, `EBX`) and special-purpose registers (e.g., `ESP`, `EBP`).
    - Stack operations like `push` and `pop` manipulate the stack in RAM by adjusting the `ESP` (stack pointer) register.

2. **RAM Class**:
    - The `RAM` class simulates memory using an array of bytes. It provides methods to read and write values to specific memory addresses.
    - The stack is implemented as part of the RAM class, using a fixed-size stack that grows downwards in memory.
