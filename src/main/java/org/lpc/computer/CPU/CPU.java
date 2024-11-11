package org.lpc.computer.CPU;

import lombok.Getter;
import lombok.Setter;
import org.lpc.Logger;
import org.lpc.computer.Motherboard;
import org.lpc.computer.RAM.RAM;

import java.io.File;

import static org.lpc.Logger.*;

/**
 * 32-bit CPU
 * 32-bit registers
 */

@Getter @Setter
public class CPU implements Opcodes, Registers{
    // ----------------- Registers -----------------
    // Where the values of the registers are stored
    int IP_VALUE;
    int EAX_VALUE, EBX_VALUE, ECX_VALUE, EDX_VALUE;
    int ESP_VALUE, EBP_VALUE;
    int ESI_VALUE, EDI_VALUE;
    boolean ZF_VALUE;

    // -------------------- CPU --------------------

    Motherboard motherboard;
    RAM ram;
    Assembler assembler;

    public CPU(Motherboard motherboard){
        this.motherboard = motherboard;
        reset();
    }

    public void init(){
        this.ram = motherboard.getRam();
        this.assembler = new Assembler(this);
        ESP_VALUE = ram.getStackEnd();
        IP_VALUE = ram.getProgramStart();
    }

    public void loadProgram(String programFile){
        File file = new File(programFile);
        try {
            assembler.assemble(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (IP_VALUE < ram.getProgramEnd()) {
            byte opcode = fetch(0);
            System.out.println("IP: " + IP_VALUE + " Opcode: " + getOpcodeName(opcode));
            int next = decodeAndExecute(opcode);
            IP_VALUE += next;
        }
        logLnColor(ANSI_GREEN, "Program execution complete. \n");
    }

    private byte fetch(int index){
        return ram.read(IP_VALUE + index);
    }

    // Fetches the next word (4 bytes) from the program memory
    // Used for instructions that require a 32-bit value
    // The word will always be stored 4 bytes after the opcode
    private int fetchWord(){
        return ram.readWord(IP_VALUE + 4);
    }

    // Returns the pointer increase needed to get the next instruction
    // Used for instructions that require a 32-bit value (they are 8 bytes long)
    private int decodeAndExecute(byte opcode) {
        switch(opcode){
            case MOV -> {
                byte dest = fetch(1);
                byte src = fetch(2);
                setRegister(dest, getRegisterValue(src));
                return 4;
            }
            case MOV_I -> {
                byte dest = fetch(1);
                int value = fetchWord();
                setRegister(dest, value);
                return 8;
            }
            case LOAD -> {
                byte dest = fetch(1);
                int address = fetchWord();
                setRegister(dest, ram.readWord(address));
                return 8;
            }
            case STORE -> {
                byte src = fetch(1);
                int address = fetchWord();
                ram.writeWord(getRegisterValue(src), address);
                return 8;
            }
            case ADD -> {
                byte src = fetch(1);
                byte src2 = fetch(2);
                byte dest = fetch(3);

                int result = getRegisterValue(src) + getRegisterValue(src2);
                setRegister(dest, result);
                ZF_VALUE = (result == 0);

                log(dumpRegisters());

                return 4;
            }
            case SUB -> {
                byte src = fetch(1);
                byte src2 = fetch(2);
                byte dest = fetch(3);

                int result = getRegisterValue(src) - getRegisterValue(src2);
                setRegister(dest, result);
                ZF_VALUE = (result == 0);
                return 4;
            }
            case MUL -> {
                byte src = fetch(1);
                byte src2 = fetch(2);
                byte dest = fetch(3);

                int result = getRegisterValue(src) * getRegisterValue(src2);
                setRegister(dest, result);
                ZF_VALUE = (result == 0);
                return 4;
            }
            case DIV -> {
                byte src = fetch(1);
                byte src2 = fetch(2);
                byte dest = fetch(3);

                int divisor = getRegisterValue(src2);
                if (divisor == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                int result = getRegisterValue(src) / divisor;
                setRegister(dest, result);
                ZF_VALUE = (result == 0);
                return 4;
            }

            case AND -> {
                byte src = fetch(1);
                byte src2 = fetch(2);
                byte dest = fetch(3);

                setRegister(dest, getRegisterValue(src) & getRegisterValue(src2));
                return 4;
            }
            case OR -> {
                byte src = fetch(1);
                byte src2 = fetch(2);
                byte dest = fetch(3);

                setRegister(dest, getRegisterValue(src) | getRegisterValue(src2));
                return 4;
            }
            case XOR -> {
                byte src = fetch(1);
                byte src2 = fetch(2);
                byte dest = fetch(3);

                setRegister(dest, getRegisterValue(src) ^ getRegisterValue(src2));
                return 4;
            }
            case NOT -> {
                byte reg = fetch(1);
                setRegister(reg, ~getRegisterValue(reg));
                return 4;
            }
            case JMP -> {
                int address = fetchWord();
                stackPush(IP_VALUE);
                IP_VALUE = address;
                return 0;
            }
            case JZ -> {
                int address = fetchWord();
                if(ZF_VALUE){
                    stackPush(IP_VALUE);
                    IP_VALUE = address;
                }  else {
                    return 8; // next instruction
                }
                return 0;
            }
            case JNZ -> {
                int address = fetchWord();
                if(!ZF_VALUE){
                    stackPush(IP_VALUE);
                    IP_VALUE = address;
                } else {
                    return 8; // next instruction
                }
                return 0;
            }
            case CALL -> {
                int address = fetchWord();
                stackPush(IP_VALUE); // Push return address onto the stack
                IP_VALUE = address; // Jump to the function
                return 0;
            }
            case RET -> {
                IP_VALUE = stackPop(); // Pop return address from the stack
                return 8;         // Return 8 bytes after the call instruction
            }
            case PUSH -> {
                byte reg = fetch(1);
                stackPush(getRegisterValue(reg));
                return 4;
            }
            case POP -> {
                byte reg = fetch(1);
                setRegister(reg, stackPop());
                return 4;
            }
        }
        throw new IllegalArgumentException("Invalid opcode: " + opcode);
    }

    private void setRegister(byte reg, int value){
        switch(reg){
            case EAX -> EAX_VALUE = value;
            case EBX -> EBX_VALUE = value;
            case ECX -> ECX_VALUE = value;
            case EDX -> EDX_VALUE = value;
            case ESP -> ESP_VALUE = value;
            case EBP -> EBP_VALUE = value;
            case ESI -> ESI_VALUE = value;
            case EDI -> EDI_VALUE = value;
            case IP -> IP_VALUE = value;
            case ZF -> ZF_VALUE = (value != 0);

            default -> System.err.println("Invalid register: " + reg);
        }
    }

    private int getRegisterValue(byte reg){
        switch(reg){
            case EAX -> {
                return EAX_VALUE;
            }
            case EBX -> {
                return EBX_VALUE;
            }
            case ECX -> {
                return ECX_VALUE;
            }
            case EDX -> {
                return EDX_VALUE;
            }
            case ESP -> {
                return ESP_VALUE;
            }
            case EBP -> {
                return EBP_VALUE;
            }
            case ESI -> {
                return ESI_VALUE;
            }
            case EDI -> {
                return EDI_VALUE;
            }
            case IP -> {
                return IP_VALUE;
            }
            case ZF -> {
                return ZF_VALUE ? 1 : 0;
            }
            default -> {
                System.err.println("Invalid register: " + reg);
                return -1;
            }
        }
    }

    public String getOpcodeName(byte opcode){
        return switch(opcode){
            case MOV -> "MOV";
            case MOV_I -> "MOV_I";
            case LOAD -> "LOAD";
            case STORE -> "STORE";
            case ADD -> "ADD";
            case SUB -> "SUB";
            case MUL -> "MUL";
            case DIV -> "DIV";
            case AND -> "AND";
            case OR -> "OR";
            case XOR -> "XOR";
            case NOT -> "NOT";
            case JMP -> "JMP";
            case JZ -> "JZ";
            case JNZ -> "JNZ";
            case CALL -> "CALL";
            case RET -> "RET";
            case PUSH -> "PUSH";
            case POP -> "POP";
            default -> "Invalid opcode: " + opcode;
        };
    }

    // ------------------------ Stack Operations ------------------------

    public void stackPush(int value){
        ESP_VALUE -= 4;

        if(ESP_VALUE < ram.getStackStart()){
            throw new IllegalStateException("Stack Overflow");
        }
        ram.writeWord(value, ESP_VALUE);
    }

    public int stackPop(){
        if(ESP_VALUE >= ram.getStackEnd()){
            throw new IllegalStateException("Stack Underflow");
        }
        int value = ram.readWord(ESP_VALUE);
        ESP_VALUE += 4;
        return value;
    }

    // ------------------------ Reset ------------------------

    public void reset(){
        this.IP_VALUE = 0;
        this.EAX_VALUE = this.EBX_VALUE = this.ECX_VALUE = this.EDX_VALUE = 0;
        this.ESP_VALUE = this.EBP_VALUE  = 0;
        this.ESI_VALUE = this.EDI_VALUE = 0;
        this.ZF_VALUE = false;
    }

    // ----------------------------- Debugging -----------------------------

    @Override
    public String toString() {
        return """
            CPU {
                IP=%d,
                EAX=%d,
                EBX=%d,
                ECX=%d,
                EDX=%d,
                ESP=%d,
                EBP=%d,
                ESI=%d,
                EDI=%d,
                ZF=%b
            }
            """.formatted(IP_VALUE, EAX_VALUE, EBX_VALUE, ECX_VALUE, EDX_VALUE, ESP_VALUE, EBP_VALUE, ESI_VALUE, EDI_VALUE, ZF_VALUE);
    }

    public String dumpRegisters(){
        StringBuilder sb = new StringBuilder();
        sb.append(ANSI_BLUE);
        sb.append("\nRegisters: \n");
        sb.append("EAX: ").append(EAX_VALUE).append("\n");
        sb.append("EBX: ").append(EBX_VALUE).append("\n");
        sb.append("ECX: ").append(ECX_VALUE).append("\n");
        sb.append("EDX: ").append(EDX_VALUE).append("\n");
        sb.append("\n");
        sb.append("IP: ").append(IP_VALUE).append("\n");
        sb.append(ANSI_RESET);

        return sb.toString();
    }
}
