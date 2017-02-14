; ==============================================================================
; XCHG and CPYD Tests
; ------------------------------------------------------------------------------
; Test new commands
; ------------------------------------------------------------------------------
    ORG   00
    JMP   Start   ; Jump over hw interrupt vector

Start:
    MOV   AL,AA   ; Init registers
    MOV   BL,BB
    MOV   CL,CC
    MOV   DL,DD

    XCHG  AL,BL
    XCHG  BL,AL
    XCHG  CL,DL


    MOV   AL,60
Loop:
    CPYD  [AL],20 ; Displacement: 80 - 60 = 20
    CMP   AL,80
    JNZ   Loop
    

    HALT


    ORG   60
Source:
    DB    "0123456789ABCDEF"

    ORG   80
Destination:
    DB    "xxxxxxxxxxxxxxxx"

    END
    
; ==============================================================================
