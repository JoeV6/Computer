package org.lpc.computer.CPU;

import lombok.Getter;
import lombok.Setter;
import org.lpc.computer.Motherboard;
import org.lpc.computer.RAM;

import java.io.File;

/**
 * 32-bit CPU
 * 32-bit registers
 */

@Getter @Setter
public class CPU implements Instructions, Registers{
    // ----- Registers -----
    // Where the values of the registers are stored
    int IP_VALUE;
    int EAX_VALUE, EBX_VALUE, ECX_VALUE, EDX_VALUE;
    int ESP_VALUE, EBP_VALUE;
    int ESI_VALUE, EDI_VALUE;
    boolean ZF_VALUE;

    // -------- CPU --------

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
        while (true) {
            byte opcode = fetch(); // Fetch the instruction
            decodeAndExecute(opcode); // Decode and execute the instruction
            IP_VALUE += 4; // Move the PC to the next instruction (assuming 4-byte instructions)
        }
    }

    private byte fetch() {
        return ram.read(IP_VALUE);
    }

    private void decodeAndExecute(int opcode) {
        switch(opcode) {

        }
    }






    // ------- Stack Operations -------

    public void push(int value){
        ESP_VALUE -= 4;

        if(ESP_VALUE < ram.getStackStart()){
            System.err.println("Stack Overflow");
            ESP_VALUE += 4;
            return;
        }
        ram.writeWord(value, ESP_VALUE);
    }

    public int pop(){
        if(ESP_VALUE >= ram.getStackEnd()){
            System.err.println("Stack Underflow");
            return 0;
        }
        int value = ram.readWord(ESP_VALUE);
        ESP_VALUE += 4;
        return value;
    }

    public void reset(){
        this.IP_VALUE = 0;
        this.EAX_VALUE = this.EBX_VALUE = this.ECX_VALUE = this.EDX_VALUE = 0;
        this.ESP_VALUE = this.EBP_VALUE  = 0;
        this.ESI_VALUE = this.EDI_VALUE = 0;
        this.ZF_VALUE = false;
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
            """.formatted(IP_VALUE, EAX_VALUE, EBX_VALUE, ECX_VALUE, EDX_VALUE, ESP_VALUE, EBP_VALUE, ESI_VALUE, EDI_VALUE, ZF_VALUE);
    }
}
