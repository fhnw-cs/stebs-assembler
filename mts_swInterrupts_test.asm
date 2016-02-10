; ==============================================================================
; Invoke the multitasking system to test it with software interrupts (02).
;
; Memory map
;   00 - 01 : CODE  Jump to start of mts
;   02      : DATA  HW interrupt vector
;   08 - 09 : CODE  Task switcher
;   10      : DATA  SP table pointer
;   11 - 14 : DATA  SP table and delimiter
;   20 - 24 : CODE  Start of mts
;
;   30 - 4F : CODE  Task One
;   50 ..   : DATA  Task 1 global vars
;     .. 5F : DATA  Task 1 stack
;
;   60 - 7F : CODE  Task Two
;   80 ..   : DATA  Task 2 global vars
;     .. 8F : DATA  Task 2 stack
;
;   90 - AF : CODE  Task Three
;   B0 ..   : DATA  Task 2 global vars
;     .. BF : DATA  Task 2 stack
;
;   E0 - FF : CODE  Subroutine
; ==============================================================================
    ORG   00
    JMP   MtsStart  ; Jump to start multitasking system (mts)
; ------------------------------------------------------------------------------


; ==============================================================================
    ORG   02
    DB    08      ; Vector to hardware interrupt routine (multitasker)
; ==============================================================================

; ==============================================================================
; Multitasker
; The Multitasker (MT) is invoked by servicing the software interrupt INT 02.
; The context (= IP, DL, CL, BL, AL and SR) of the current task is saved on the
; stack of this task. The SP is saved into the SP_Table as organised below
; (strictly this way when using NEXT <SP_Table_Ptr>).
; ------------------------------------------------------------------------------
    ORG   08
TaskSwitcher:     ; Switch to next tasks
    NEXT  10      ;  defined in SP_Table
; ==============================================================================

; ==============================================================================
; Round Robin table to manage tasks via their SPs
; ==============================================================================
    ORG   10
SP_Table_Ptr:
    DB    11      ; Initial pointer to SP_Table = SP_Table_Ptr + 1
; ------------------------------------------------------------------------------
SP_Table:
    DB    5F      ; Initial SP; SP = [SP_Table_Ptr]
    DB    89      ; Initial SP of task two (stack with SR .. IP)
    DB    B9      ; Initial SP of task three (stack with SR .. IP)
    DB    00      ; Table delimiter
; ==============================================================================


; ------------------------------------------------------------------------------
    ORG   20
MtsStart:
    MOV   SP,[11] ; Init SP to SP-Table
    JMP   Task_One  ; Jump to first task
; ------------------------------------------------------------------------------


; ==============================================================================
; Task One
; ------------------------------------------------------------------------------
    ORG   30
Task_One:
    MOV   AL,50   ; Init Task1 global var
    MOV   BL,11   ; Init marker_byte
    MOV   CL,1C
    MOV   DL,1D
    
Loop1:
    CALL  E0      ; Call AddStoreGlobals
    
    INC   BL
    INC   CL
    INC   DL

    INT   02      ; Force task switch
    
    CMP   DL,22
    JNZ   Loop1
    
    HALT          ; Halt system
; ------------------------------------------------------------------------------
; ------------------------------------------------------------------------------
    ORG   50      ; Task1 global vars
T1Globals:
    DB    FF
    DB    FF
; ------------------------------------------------------------------------------
    ORG   58
Task1Stack_End:
    DB    11      ; Dummy values
    DB    11      ;   for visualisation
    DB    00      ; SR
    DB    00      ; DL
    DB    00      ; CL
    DB    00      ; BL
    DB    00      ; AL
Task1Stack_Start: ; [5F] = Initial SP value (empty stack)
    DB    30      ; IP
; ==============================================================================


; ==============================================================================
; Task Two
; ------------------------------------------------------------------------------
    ORG   60
Task_Two:
    MOV   AL,80   ; Init Task2 global var
    MOV   BL,22   ; Init marker_byte
    MOV   CL,2C
    MOV   DL,2D
    
Loop2:
    CALL  E0      ; Call AddStoreGlobals
    
    INC   BL
    INC   CL
    INC   DL
    
    INT   02      ; Force task switch

    JMP   Loop2
; ------------------------------------------------------------------------------
; ------------------------------------------------------------------------------
    ORG   80      ; Task2 global vars
T2Globals:
    DB    FF
    DB    FF
; ------------------------------------------------------------------------------
    ORG   88
Task2Stack_End:
    DB    22      ; Dummy values for visualisation
    DB    22      ; [89] = Initial SP value (with SR .. IP)
    DB    00      ; SR
    DB    00      ; DL
    DB    00      ; CL
    DB    00      ; BL
    DB    00      ; AL
Task2Stack_Start:
    DB    60      ; IP
; ==============================================================================


; ==============================================================================
; Task Three
; ------------------------------------------------------------------------------
    ORG   90
Task_Three:
    MOV   AL,B0   ; Init Task2 global var
    MOV   BL,33   ; Init marker_byte
    MOV   CL,3C
    MOV   DL,3D
    
Loop3:
    CALL  E0      ; Call AddStoreGlobals
    
    INC   BL
    INC   CL
    INC   DL
    
    INT   02      ; Force task switch
    
    JMP   Loop3
; ------------------------------------------------------------------------------
; ------------------------------------------------------------------------------
    ORG   B0      ; Task3 global vars
T3Globals:
    DB    FF
    DB    FF
; ------------------------------------------------------------------------------
    ORG   B8
Task3Stack_End:
    DB    33      ; Dummy values for visualisation
    DB    33      ; [B9] = Initial SP value (with SR .. IP)
    DB    00      ; SR
    DB    00      ; DL
    DB    00      ; CL
    DB    00      ; BL
    DB    00      ; AL
Task3Stack_Start:
    DB    90      ; IP
; ==============================================================================



; ==============================================================================
; Store two values at indicated global addressess
;  [address]    = marker_byte
;  [address+01] = CL + DL
;
; Input:
;  AL = address
;  BL = marker_byte
; Output:
;  changed: --
;
; ------------------------------------------------------------------------------
    ORG   E0
AddStoreGlobals:
    PUSH  AL      ; Save registers
    PUSH  CL
    PUSHF
    
    ADD   CL,DL   ; CL := CL + DL
    MOV   [AL],BL ; Store marker_byte
    INC   AL      ;
    MOV   [AL],CL ; Store sum
    
    POPF          ; Restore registers
    POP   CL
    POP   AL
    RET
; ==============================================================================

    END
; ==============================================================================
