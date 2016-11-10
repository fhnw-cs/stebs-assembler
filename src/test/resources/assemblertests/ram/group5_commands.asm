    MOV   AL,00
    MOV   BL,99
    MOV   CL,AA
    MOV   DL,7D
    MOV   SP,E8

    MOV   AL,BL
    MOV   BL,CL
    MOV   DL,SP
    MOV   BL,BL
    MOV   AL,SP

    MOV   SP,[01]
    MOV   DL,[9A]
    MOV   CL,[AB]
    MOV   BL,[7C]
    MOV   AL,[D7]

    MOV   AL,[BL]
    MOV   BL,[BL]
    MOV   CL,[DL]
    MOV   SP,[SP]
    MOV   SP,[AL]

    MOV   [02],BL
    MOV   [17],BL
    MOV   [A3],BL
    MOV   [CD],BL
    MOV   [A1],BL
    
    MOV   [AL],BL 
    MOV   [CL],BL 
    MOV   [DL],CL 
    MOV   [SP],BL 
    MOV   [SP],SP


    ORG   77
    MOV   AL,00
    MOV   BL,99
    MOV   CL,AA
    MOV   DL,7D
    MOV   SP,E8

    MOV   AL,BL
    MOV   BL,CL
    MOV   DL,SP
    MOV   BL,BL
    MOV   AL,SP

    MOV   SP,[01]
    MOV   DL,[9A]
    MOV   CL,[AB]
    MOV   BL,[7C]
    MOV   AL,[D7]

    MOV   AL,[BL]
    MOV   BL,[BL]
    MOV   CL,[DL]
    MOV   SP,[SP]
    MOV   SP,[AL]

    MOV   [02],BL
    MOV   [17],BL
    MOV   [A3],BL
    MOV   [CD],BL
    MOV   [A1],BL
    
    MOV   [AL],BL 
    MOV   [CL],BL 
    MOV   [DL],CL 
    MOV   [SP],BL 
    MOV   [SP],SP
    
    END