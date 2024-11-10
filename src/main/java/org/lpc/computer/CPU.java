package org.lpc.computer;

import lombok.Getter;
import lombok.Setter;

import static org.lpc.Logger.log;

/**
 * 32-bit CPU
 * 32-bit registers
 */

@Getter @Setter
public class CPU {
    // ----- Registers -----
    // Program Counter
    int IP;

    // General purpose registers
    int EAX, EBX, ECX, EDX;

    // Stack Pointer
    int ESP;

    // Base Pointer
    int EBP;

    // Index Registers
    int ESI, EDI;

    // ------- Flags -------
    // Zero Flag
    boolean ZF;

    // -------- CPU --------

    Motherboard motherboard;
    RAM ram;

    public CPU(Motherboard motherboard){
        this.motherboard = motherboard;
        this.ram = motherboard.getRam();
        reset();
    }

    public void init(){
        ESP = ram.getStackEnd();
    }

    // ------- Stack Operations -------

    public void push(int value){
        ESP -= 4;

        if(ESP < ram.getStackStart()){
            System.err.println("Stack Overflow");
            ESP += 4;
            return;
        }
        ram.writeWord(value, ESP);
    }

    public int pop(){
        if(ESP >= ram.getStackEnd()){
            System.err.println("Stack Underflow");
            return 0;
        }
        int value = ram.readWord(ESP);
        ESP += 4;
        return value;
    }

    public void reset(){
        this.IP = 0;
        this.EAX = this.EBX = this.ECX = this.EDX = 0;
        this.ESP = this.EBP  = 0;
        this.ESI = this.EDI = 0;
        this.ZF = false;
    }

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
            """.formatted(IP, EAX, EBX, ECX, EDX, ESP, EBP, ESI, EDI, ZF);
    }
}
