    ORG 55
    
    CMP   AL,7E
    CMP   BL,7F
    CMP   CL,80
    CMP   DL,81
    CMP   SP,82

    CMP   AL,DL
    CMP   BL,DL
    CMP   CL,DL
    CMP   SP,DL
    CMP   DL,SP

    CMP   AL,[EF]
    CMP   BL,[FF]
    CMP   CL,[1F]
    CMP   DL,[0F]
    CMP   SP,[2F]
    
    END
    