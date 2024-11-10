package org.lpc.computer.CPU;

import org.lpc.computer.RAM;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import static org.lpc.computer.CPU.Instructions.*;

public class Assembler implements Registers {
    private CPU cpu;
    private RAM ram;

    public Assembler(CPU cpu) {
        this.cpu = cpu;
        this.ram = cpu.getRam();
    }

    public void assemble(File codeFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(codeFile));
        String line;
        int currentAddress = 0;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith(";")) {
                continue;
            }
            line = line.split(";")[0].trim(); // Remove comments
            line = line.replace(",", "");

            byte[] instructionBytes = decodeInstruction(line);
            for (byte b : instructionBytes) {
                ram.write(currentAddress, b);
                currentAddress++;
            }
        }
        reader.close();
    }

    public byte[] decodeInstruction(String instruction) {
        byte[] bytes = new byte[4]; // buffer with 0s

        String[] parts = instruction.split(" ");
        if (parts.length == 0) {
            throw new IllegalArgumentException("Empty instruction.");
        }

        String opcode = parts[0];
        try{
            bytes[0] = getOpCode(opcode);
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Invalid opcode: " + opcode + " in instruction: " + instruction);
        }

        switch (bytes[0]) {
            case MOV -> {
                if (parts.length != 3) {
                    throw new IllegalArgumentException("MOV requires 2 arguments.");
                }
                if (isNumber(parts[2])) {
                    bytes[1] = getRegister(parts[1]);
                    bytes[2] = Byte.parseByte(parts[2]);
                } else if (isRegister(parts[2])) {
                    bytes[1] = getRegister(parts[1]);
                    bytes[2] = getRegister(parts[2]);
                } else {
                    throw new IllegalArgumentException("Invalid MOV instruction: " + instruction);
                }
            }
            case LOAD -> {
                if (parts.length != 3) {
                    throw new IllegalArgumentException("LOAD requires 2 arguments.");
                }
                bytes[1] = getRegister(parts[1]);
                bytes[2] = getRegister(parts[2]);
            }
            case STORE -> {
                if (parts.length != 3) {
                    throw new IllegalArgumentException("STORE requires 2 arguments.");
                }
                bytes[1] = getRegister(parts[1]);
                bytes[2] = Byte.parseByte(parts[2]);
            }
            case ADD, SUB, MUL, DIV -> {
                if (parts.length == 4) {
                    bytes[1] = getRegister(parts[1]);
                    bytes[2] = getRegister(parts[2]);
                    bytes[3] = getRegister(parts[3]);
                } else if (parts.length == 3) {
                    if (isNumber(parts[2])) {
                        bytes[1] = getRegister(parts[1]);
                        bytes[2] = Byte.parseByte(parts[2]);
                        bytes[3] = getRegister(parts[1]);
                    } else if (isRegister(parts[2])) {
                        bytes[1] = getRegister(parts[1]);
                        bytes[2] = getRegister(parts[2]);
                        bytes[3] = getRegister(parts[1]);
                    } else {
                        throw new IllegalArgumentException("Invalid ADD/SUB/MUL/DIV instruction: " + instruction);
                    }
                } else {
                    throw new IllegalArgumentException("Invalid ADD/SUB/MUL/DIV instruction: " + instruction);
                }
            }
            case AND, OR, XOR -> {
                if (parts.length != 4) {
                    throw new IllegalArgumentException("AND/OR/XOR requires 3 arguments.");
                }
                bytes[1] = getRegister(parts[1]);
                bytes[2] = getRegister(parts[2]);
                bytes[3] = getRegister(parts[3]);
            }
            case NOT, PUSH, POP -> {
                if (parts.length != 2) {
                    throw new IllegalArgumentException("NOT/PUSH/POP requires 1 argument.");
                }
                bytes[1] = getRegister(parts[1]);
            }
            case JMP, JZ, JNZ, CALL -> {
                if (parts.length != 2) {
                    throw new IllegalArgumentException("JMP/JZ/JNZ/CALL requires 1 argument.");
                }
                bytes[1] = Byte.parseByte(parts[1]);
            }
            case RET -> {
                // No arguments, no further action needed
            }
            default -> {
                throw new IllegalArgumentException("Invalid opcode: " + opcode);
            }
        }

        return bytes;
    }

    public boolean isRegister(String register) {
        try {
            getRegister(register);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isNumber(String number) {
        try {
            Byte.parseByte(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public byte getOpCode(String opcode) {
        return switch (opcode.toUpperCase()) {
            case "MOV" -> MOV;
            case "LOAD" -> LOAD;
            case "STORE" -> STORE;

            case "ADD" -> ADD;
            case "SUB" -> SUB;
            case "MUL" -> MUL;
            case "DIV" -> DIV;

            case "AND" -> AND;
            case "OR" -> OR;
            case "XOR" -> XOR;
            case "NOT" -> NOT;

            case "JMP" -> JMP;
            case "JZ" -> JZ;
            case "JNZ" -> JNZ;
            case "CALL" -> CALL;
            case "RET" -> RET;

            case "PUSH" -> PUSH;
            case "POP" -> POP;

            default -> throw new IllegalArgumentException("Invalid instruction: " + opcode);
        };
    }

    public byte getRegister(String register) {
        return switch (register.toUpperCase()) {
            case "EAX" -> EAX;
            case "EBX" -> EBX;
            case "ECX" -> ECX;
            case "EDX" -> EDX;

            case "ESI" -> ESI;
            case "EDI" -> EDI;

            case "ESP" -> ESP;
            case "EBP" -> EBP;

            case "IP" -> IP;
            case "ZF" -> ZF;

            default -> throw new IllegalArgumentException("Invalid register");
        };
    }
}
