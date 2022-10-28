MACRO
add_inner operando
    ADD @operando
MEND
*
MACRO
add_outer operando
    add_inner operando
MEND
*
add_outer 5
