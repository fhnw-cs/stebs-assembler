    ORG   00
    ADD   AL,11
    ADD   BL,AA
    ADD   CL,F9
    ADD   DL,9F
    ADD   SP,00
    
    ADD   AL,BL
    ADD   BL,CL
    ADD   CL,DL
    ADD   DL,AL
    ADD   SP,SP
    
    
    ORG   20
    SUB   AL,11
    SUB   BL,AA
    SUB   CL,F9
    SUB   DL,9F
    SUB   SP,00
    
    SUB   AL,BL
    SUB   BL,CL
    SUB   CL,DL
    SUB   DL,AL
    SUB   SP,SP

    
    ORG   40
    MUL   AL,11
    MUL   BL,AA
    MUL   CL,F9
    MUL   DL,9F
    MUL   SP,00
    
    MUL   AL,BL
    MUL   BL,CL
    MUL   CL,DL
    MUL   DL,AL
    MUL   SP,SP
    
    
    ORG   60
    DIV   AL,11
    DIV   BL,AA
    DIV   CL,F9
    DIV   DL,9F
    DIV   SP,00
    
    DIV   AL,BL
    DIV   BL,CL
    DIV   CL,DL
    DIV   DL,AL
    DIV   SP,SP
    
    
    ORG   80
    MOD   AL,11
    MOD   BL,AA
    MOD   CL,F9
    MOD   DL,9F
    MOD   SP,00
    
    MOD   AL,BL
    MOD   BL,CL
    MOD   CL,DL
    MOD   DL,AL
    MOD   SP,SP


    ORG   A0
    AND   AL,11
    AND   BL,AA
    AND   CL,F9
    AND   DL,9F
    AND   SP,00
    
    AND   AL,BL
    AND   BL,CL
    AND   CL,DL
    AND   DL,AL
    AND   SP,SP
    

    ORG   C0
    OR    AL,11
    OR    BL,AA
    OR    CL,F9
    OR    DL,9F
    OR    SP,00
    
    OR    AL,BL
    OR    BL,CL
    OR    CL,DL
    OR    DL,AL
    OR    SP,SP
    
    
    ORG   E0
    XOR   AL,11
    XOR   BL,AA
    XOR   CL,F9
    XOR   DL,9F
    XOR   SP,00
    
    XOR   AL,BL
    XOR   BL,CL
    XOR   CL,DL
    XOR   DL,AL
    XOR   SP,SP

    
    END
    