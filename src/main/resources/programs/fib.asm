.data
    n = 10 ; Define the target Fibonacci index

.fibLoop
    ; Calculate the next Fibonacci number
    ; EBX stores the current Fibonacci number
    ; ECX stores the previous Fibonacci number

    ; Temp store the next Fibonacci number
    ADD EDX, EBX, ECX  ; tempNext = current + previous
    MOV ECX, EBX       ; previous = current
    MOV EBX, EDX       ; current = tempNext

    ; Decrement the loop counter
    SUB EAX, EDI    ; EAX = EAX - 1
    JNZ fibLoop     ; If EAX is not zero, repeat the loop

    RET             ; Otherwise, return to the caller

.start
    MOV EDI, 1      ; Set EDI to 1 (used for decrementing EAX)

    MOV EBX, 1      ; F(1) = 1
    MOV ECX, 0      ; F(0) = 0
    MOV EAX, n - 1  ; Set EAX to n-1 (loop counter)
    CALL fibLoop    ; Calculate the nth Fibonacci number

    STORE 1100, EBX ; Store the result (F(n)) in memory location 1100



