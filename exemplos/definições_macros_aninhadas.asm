        MACRO
        OUTER outer_arg
               MACRO
               INNER inner_arg
               ADD outer_arg
               ADD inner_arg
               MEND
        MEND
*
        OUTER 5
        INNER 9
*
        STOP

* saída esperada:
* ADD 5
* ADD 9
* STOP
