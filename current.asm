    ORG   00
    JMP   Start
    
Start:
    MOV   AL,AA   ; Init registers
    MOV   BL,BB
    MOV   CL,CC
    MOV   DL,DD
    MOV   CL,DL
    MOV   DL,AL   ; jkflsfksdf

    XCHG  AL,BL
    XCHG  BL,AL   ; asddhjkasd
    XCHG  CL,DL
    
    XCHG  SP,SP


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
