package org.lpc.computer.CPU;

import org.lpc.computer.RAM;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lpc.computer.CPU.Opcodes.*;
import static org.lpc.computer.RAM.convertIntToBytes;

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
        ram.setProgramEnd(currentAddress - 1);
        reader.close();
    }

    public void prepLine(String line) {
        line = line.trim();
        if (line.isEmpty() || line.startsWith(";")) {
            return;
        }
        line = line.split(";")[0].trim(); // Remove comments
        line = line.replace(",", "");
    }



    public byte[] decodeInstruction(String instruction) {
        String[] parts = instruction.split(" ");
        if (parts.length == 0) {
            throw new IllegalArgumentException("Empty instruction.");
        }

        String opcode = parts[0];
        byte op;
        try{
            op = getOpCode(opcode);
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Invalid opcode: " + opcode + " in instruction: " + instruction);
        }

        switch (op) {
            case MOV -> {
                return handleMOV(parts);
            }
            case LOAD -> {
                return handleLOAD(parts);
            }
            case STORE -> {
                return handleSTORE(parts);
            }
            case ADD, SUB, MUL, DIV -> {
                return handleArithmetic(parts, op);
            }
            case AND, OR, XOR -> {
                return handleLogical(parts, op);
            }
            case NOT, PUSH, POP -> {
                return handleSingleRegister(parts, op);
            }
            case JMP, JZ, JNZ, CALL -> {
                return handleControlFlow(parts, op);
            }
            case RET -> {
                return handleRET();
            }
            default -> {
                throw new IllegalArgumentException("Invalid opcode: " + opcode);
            }
        }
    }

    public byte[] handleMOV(String[] parts) {
        String dst = parts[1];
        String src = parts[2]; // Source register or immediate value

        byte[] bytes;

        if (src.matches("-?\\d+")) { // MOV dst, IMM (immediate value)
            bytes = new byte[8];
            bytes[0] = MOV_I;
            bytes[1] = getRegister(dst);

            // Use the convertIntToBytes method to convert the immediate value (integer) to 4-byte representation
            byte[] valueBytes = convertIntToBytes(Integer.parseInt(src));
            System.arraycopy(valueBytes, 0, bytes, 4, 4); // Copy the 4 bytes of immediate value into the instruction bytes
        } else { // MOV dst, REG (register-to-register)
            bytes = new byte[4];
            bytes[0] = MOV;
            bytes[1] = getRegister(dst);
            bytes[2] = getRegister(src);
        }

        return bytes;
    }


    public byte[] handleLOAD(String[] parts) {
        String reg = parts[1];
        String address = parts[2];

        byte[] bytes = new byte[8];

        bytes[0] = LOAD;
        bytes[1] = getRegister(reg);

        // Use convertIntToBytes for the address
        byte[] addrBytes = convertIntToBytes(Integer.parseInt(address));
        System.arraycopy(addrBytes, 0, bytes, 4, 4); // Store the address in the last 4 bytes

        return bytes;
    }

    public byte[] handleSTORE(String[] parts) {
        String address = parts[1];
        String reg = parts[2];

        byte[] bytes = new byte[8];

        bytes[0] = STORE;
        bytes[1] = getRegister(reg);

        // Use convertIntToBytes for the address
        byte[] addrBytes = convertIntToBytes(Integer.parseInt(address));
        System.arraycopy(addrBytes, 0, bytes, 4, 4); // Store the address in the last 4 bytes

        return bytes;
    }



    public byte[] handleArithmetic(String[] parts, byte opcode) {
        String dst = parts[1];
        String src1 = parts[2];
        String src2 = parts.length > 3 ? parts[3] : null; // Second source register (optional)

        byte[] bytes = new byte[4]; // Always 4 bytes for arithmetic operations

        bytes[0] = opcode;

        if (src2 != null) { // Case for 3 operands: src1, src2, dst
            bytes[1] = getRegister(src1);
            bytes[2] = getRegister(src2);
        } else { // Case for 2 operands: dst, src
            bytes[1] = getRegister(dst);
            bytes[2] = getRegister(src1);
        }

        bytes[3] = getRegister(dst);

        return bytes;
    }

    public byte[] handleLogical(String[] parts, byte opcode) {
        String dst = parts[1];
        String src = parts[2];

        byte[] bytes = new byte[4]; // Always 4 bytes for logical operations

        bytes[0] = opcode;

        bytes[1] = getRegister(dst);
        bytes[2] = getRegister(src);
        bytes[3] = getRegister(dst);

        return bytes;
    }

    public byte[] handleSingleRegister(String[] parts, byte opcode) {
        String reg = parts[1];

        byte[] bytes = new byte[4];

        bytes[0] = opcode;
        bytes[1] = getRegister(reg);

        return bytes;
    }

    public byte[] handleControlFlow(String[] parts, byte opcode) {
        String address = parts[1];

        byte[] bytes = new byte[8];

        bytes[0] = opcode;

        // Use convertIntToBytes for the address
        byte[] addrBytes = convertIntToBytes(Integer.parseInt(address));
        System.arraycopy(addrBytes, 0, bytes, 4, 4); // Store the address in the last 4 bytes

        return bytes;
    }

    public byte[] handleRET() {
        byte[] bytes = new byte[4];
        bytes[0] = RET;

        return bytes;
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

            default -> throw new IllegalArgumentException("Invalid register: " + register);
        };
    }
}
