; -------------------------------------------------------------------------
; Program to sort an array in-place using the bubble sort algorithm.
; Values in the array are recognised as two's complement numbers
; 
; Pseudocode:
;  bubbleSort(Array A) {
;    for (n = A.size; n > 1; n = n-1){
;      for (i = 0; i < n-1; i = i+1){
;        if (A[i] > A[i+1]){
;          A.swap(i, i+1)
;        }
;      }
;    }
;  }
;
; Modified algorithm here:
;  Instead of using the array indeces, absolute array addresses are used.
; -------------------------------------------------------------------------

    ORG   00
; =========================================================================
; Testing Main:
; Sort an array in-place.
;
; Description:
; The array at addr A0 is sorted in ascending order.
;
; Expected result at HALT:
; Addr A0:
;  | 97 | F3 | 01 | 04 | 09 | 
; =========================================================================
Main:
    MOV   AL,A0   ; Set up array start address
    MOV   BL,05   ; Set up array size
    CALL  20      ; Call Bubblesort
    HALT
; -------------------------------------------------------------------------


    ORG   20
; =========================================================================
; Sort an array A in-place using the bubble sort algorithm.
; Lowest element at lowest address etc. (ascending order).
;
; Register Input:
;   AL = array_startaddress
;   BL = array_size
; Register Output:
;   changed: AL, BL, CL, DL, SP, SR
; =========================================================================
Bubblesort:
    CMP   BL,01   ; No sorting...
    JZ    EndOL   ;  ...if array size = 1
    CMP   BL,00   ; No sorting...
    JZ    EndOL   ;  ...if array size = 0

    PUSH  AL      ; Save array start addr
    ADD   BL,AL   ; Calculate array end addr

Outerloop:
    POP   AL      ; Restore
    PUSH  AL      ;  array start addr

    DEC   BL      ; Lower array end addr
    CMP   BL,AL   ; Are array end addr and start addr equal, sorting done?
    JZ    Exit    ; If equal: exit

    
Innerloop:        ; See if neighbouring elements need swapping
    CMP   BL,AL   ; Array end reached?
    JZ    EndIL   ; If reached; jump

    MOV   CL,[AL] ; CL = array[addr]
    INC   AL      ; addr++,
    MOV   DL,[AL] ;  therefore: DL = array[addr+1]
    CMP   CL,DL   ; array[addr] > array[addr+1] ?
    JS    NoSwap  ; If negative: swap
                  ;  else jump
Swap:
    MOV   [AL],CL ; array[addr+1] = CL
    DEC   AL      ; addr--,
    MOV   [AL],DL ;  therefore: array[addr] = DL
    INC   AL      ; addr++

NoSwap:
    JMP   Innerloop
EndIL:            ; End of inner loop


    JMP   Outerloop
EndOL:            ; End of outer loop

Exit:
    POP   AL      ; Balance stack
    RET
; -------------------------------------------------------------------------



    ORG   A0
; -------------------------------------------------------------------------
; Values in array are recognised as two's complement:
; 97 < F3 < 01
; -------------------------------------------------------------------------
Array:            ; Array to be sorted inplace
    DB    09
    DB    04
    DB    F3
    DB    01
    DB    97
; -------------------------------------------------------------------------

    END
