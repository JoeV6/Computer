.data
    x = 5;
    y = 5;

.setZeroIterate
    MOV EDX, 1
    SUB EAX, EDX
    JNZ setZeroIterate
    RET

.start
    MOV EAX 5
    CALL setZeroIterate ; sets EAX to 0 iteratively
    MOV EBX 1
    ADD EAX, EBX
    STORE 1100, EAX ; should be 1






