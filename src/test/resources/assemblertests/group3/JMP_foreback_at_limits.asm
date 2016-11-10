FORWARDS:

    ORG   00
    JMP   WithinForwardReach_0

    ORG   7F
WithinForwardReach_0:


    ORG   71
    JMP   WithinForwardReach_1

    ORG   EF
WithinForwardReach_1:

; ------------------------------


BACKWARDS:

    ORG   00
WithinBackwardReach_0:

    ORG   80
    JMP   WithinBackwardReach_0
    
    
    ORG   71
WithinBackwardReach_1:
    
    ORG   F0
    JMP   WithinBackwardReach_1

; ------------------------------

    END
