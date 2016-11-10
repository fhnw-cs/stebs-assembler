Go:
Start:
    JS    Start
    JMP   Go
Again:
    JMP   GO
    
    ORG   10
    ORG   12
    JNO   Again
    
    JS    again
    
    JZ    Go

End:
    END
    