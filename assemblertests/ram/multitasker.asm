; ------------------------------------------------------------------------------
;  TIME SLICE MULTITASKER PROGRAM.
; ------------------------------------------------------------------------------
;  This program controls the heater and the thermostat continuously in one task.
;  The traffic lights are controlled in a second task.
;  Task three displays the numbers 0 to 9 on the 7-segment display.
;  Task switching is enforced by the multitasker program on a round robin
;  basis.
;  The task running is in active state, all other tasks are in ready state.
;
;  Settings:
;  Step Type:           Instruction Steps
;  Program Speed:       Fastest
;  Periodic Interrupt:  10 sec
;  Single Interrupt:    May be used
;  Close the Architecture Window to improve performance  
;
;  Note:
;  Do NOT use the stack in the code of the tasks.
; ------------------------------------------------------------------------------
    JMP    Start

; ------------------------------------------------------------------------------
    ORG   02
    DB    06      ; Vector to hardware interrupt routine (multitasker)
; ------------------------------------------------------------------------------

Start:
    STI           ; Enable hardware interrupts.
    JMP   Segment

; ------------------------------------------------------------------------------
;  Multitasker
;  The Multitasker (MT)is invoked by answering the interrupt request set by
;  the periodic hardware interrupt or a single interrupt.
;  MT saves the context (= AL, BL, CL, DL, SR and IP) of the active task into
;  the context table of this task.
;  The next task (cp. ContextTableEntries) is activated. Its context is re-
;  stored. Control is then passed to this new task (IRET).
; ------------------------------------------------------------------------------
    ORG   06
Tasker:
    CLI           ; Inhibit hardware interrupts.
                  ; Switch Contexts.
    PUSHF         ; Save SR to stack.
    PUSH  DL      ;      DL
    PUSH  CL      ;      CL
    PUSH  BL      ;      BL
    PUSH  AL      ;      AL 
    
    MOV   DL,[60] ; Get CTEPointer
    MOV   DL,[DL] ; Get context table address of last active task.
    MOV   CL,06   ; Init loop counter.
SaveContext:
    POP   AL      ; Get saved values from the stack
    MOV   [DL],AL ; Save values into the context table.
    INC   DL
    DEC   CL
    JNZ   SaveContext
    
    MOV   AL,[60] ; Get CTEPointer.
    INC   AL
    MOV   DL,[AL] ; Get next context table address.
    CMP   DL,00   ; Delimiter?
    JNZ   Continue
    MOV   AL,61   ; Delimiter reached, wrap.
    MOV   DL,[61] ; Get first context table address.
Continue:
    MOV   [60],AL ; Update CTEPointer (active task).
    
    ADD   DL,05
    MOV   CL,06   ; Init loop counter. 
RestoreContext:
    MOV   AL,[DL] ; Get IP.
    PUSH  AL      ; Put values onto the stack.
    DEC   DL
    DEC   CL
    JNZ   RestoreContext
    
    POP   AL      ; Restore AL from stack.
    POP   BL      ;         BL
    POP   CL      ;         CL
    POP   DL      ;         DL
    POPF          ;         SR
    
    STI           ; Enable hardware interrupts again.
    IRET          ; Perform task switch, invoke active task.?
    ORG   60
CTEPointer:
    DB    61      ; Pointer to entry of currently active task (var)
ContextTableEntries:
    DB    70      ; Context Table Address of Task One (const)
    DB    9A      ; Context Table Address of Task Two (const)
    DB    C0      ; Context Table Address of Task Three (const)
    DB    00      ; Delimiter: EOT
; ------------------------------------------------------------------------------


; ------------------------------------------------------------------------------
; Task One (7 Segment Display)
; 7 segment numbers are consecutively read from a table and sent to the
; display. At the end of table a wrap over takes place.
; ------------------------------------------------------------------------------
    ORG   70
ContextTbl1:      ; Context table of 7 Segment Display Task
    DB    00      ; Saved AL
    DB    00      ;       BL
    DB    00      ;       CL
    DB    00      ;       DL
    DB    00      ;       SR (I Bit always cleared/set by MT).
    DB    76      ;       IP, initially points to task start address Segment

Segment:
    MOV   DL,87   ; Initialise with Seg7LookupTableStart
NextNumber:
    MOV   AL,[DL]
    OUT   02      ; Display number
    INC   DL      ; Next number

    CMP   DL,91       ; Check if DL is at number 9
    JNZ   NextNumber  ; if not jump to NextNumber 
    
    JMP   Segment     ; else to Segment to reinitialize DL

    ORG   87
Seg7LookupTableStart:
    DB    3F      ; Left hand number 0
    DB    06      ;                  1
    DB    5B      ;                  2
    DB    4F      ;                  3
    DB    66      ;                  4
    DB    6D      ;                  5
    DB    7D      ;                  6
    DB    07      ;                  7
    DB    7F      ;                  8
    DB    6F      ;                  9
Seg7LookupTableEnd:
; ------------------------------------------------------------------------------


; ------------------------------------------------------------------------------
; Task Two (Traffic Lights)
; Traffic light data is consecutively read from a table and sent to the
; lights. At the end of table a wrap over takes place.
; ------------------------------------------------------------------------------
    ORG   9A
ContextTbl2:      ; Context table of Traffic Light Task
    DB    00      ; Saved AL
    DB    00      ;       BL
    DB    00      ;       CL
    DB    00      ;       DL
    DB    00      ;       SR (I Bit always cleared/set by MT).
    DB    A0      ;       IP, initially points to task start address Lights

Lights:
    MOV   DL,B2   ; Initialise pointer with LightTableStart
NextLight:
    MOV   AL,[DL]
    OUT   00      ; Display lights
    INC   DL      ; Next lights

    CMP   DL,B6      ; Check if beyond end of table
    JNZ   NextLight  ; if not jump to NextLight 
    
    JMP   Lights     ; else reinitialize pointer

    ORG   B2
LightTableStart:
    DB    82      ; Red           Green             (const)
    DB    C4      ; Red+Amber     Amber             (const)
    DB    28      ; Green         Red               (const)
    DB    4C      ; Amber         Red+Amber         (const)
LightTableEnd:
; ------------------------------------------------------------------------------


; ------------------------------------------------------------------------------
; Task Three (Heater)
; The heater is turned on if temperature falls below target temperature.
; ------------------------------------------------------------------------------
    ORG   C0
ContextTbl3:      ; Context table of Heater Task
    DB    00      ; Saved AL
    DB    00      ;       BL
    DB    00      ;       CL
    DB    00      ;       DL
    DB    00      ;       SR (I Bit always cleared/set by MT).
    DB    C6      ;       IP, initially points to task start address Heater

Heater:
    MOV   BL,14   ; Init target temperature: 20°C
NextHeater:   
    IN    01      ; Input from heater port
    AND   AL,40   ; Mask with 01000000
    CMP   AL,40   ; Calculate difference to affect Z flag
    JZ    Off     ; If the result is zero, turn heater on
On:
    MOV   AL,80   ; Code to turn heater on
    OR    AL,BL   ; Add target temperature to code
    OUT   01      ; Send code to heater port
    JMP   NextHeater
Off:
    MOV   AL,00   ; Code to turn heater off
    OR    AL,BL   ; Add target temperature to code
    OUT   01      ; Send code to heater port
    JMP   NextHeater
; ------------------------------------------------------------------------------

    END
; ------------------------------------------------------------------------------
