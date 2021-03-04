; execution of this program proceeded by following emulator commands
; cpu reset
; memory create 256
; memory set 0x0 0x8 0x1 0x2 0x3 0x4 0x5 0x6 0x7 0x8
;
; before execution, memory is
; Addr   00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F
; 0x0000 01 02 03 04 05 06 07 08 00 00 00 00 00 00 00 00
;
; after execution, memory is
; Addr   00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F
; 0x0000 01 08 07 06 05 04 03 02 01 00 00 00 00 00 00 00
;
lw $a $h ; h is 0, load a with what's at data offset 0 (which is 1)
lw $b $a ; a now 1, load b with what's at data offset 1 (which is 2)
lw $c $b ; b now 2, load c with what's at data offset 2 (which is 3)
lw $d $c ; c now 3, load d with what's at data offset 3 (which is 4)
lw $e $d ; d now 4, load e with what's at data offset 4 (which is 5)
lw $f $e ; e now 5, load f with what's at data offset 5 (which is 6)
lw $g $f ; f now 6, load g with what's at data offset 6 (which is 7)
lw $h $g ; g now 7, load h with what's at data offset 7 (which is 8)
sw $a $h ; store contents of a (1) where h points (8) mem[8]=1
sw $b $g ; store contents of b (2) where g points (7) mem[7]=2
sw $c $f ; store contents of c (3) where f points (6) mem[6]=3
sw $d $e ; store contents of d (4) where e points (5) mem[5]=4
sw $e $d ; store contents of e (5) where d points (4) mem[4]=5
sw $f $c ; store contents of f (6) where d points (3) mem[3]=6
sw $g $b ; store contents of g (7) where b points (2) mem[2]=7
sw $h $a ; store contents of h (8) where a points (1) mem[1]=8
;
; The above code produces the following 16 instructions
; addr instr
; 0000 A0700
; 0001 A4000
; 0002 A8100
; 0003 AC200
; 0004 B0300
; 0005 B4400
; 0006 B8500
; 0007 BC600
; 0008 C0700
; 0009 C0E00
; 000A C1500
; 000B C1C00
; 000C C2300
; 000D C2A00
; 000E C3100
; 000F C3800
