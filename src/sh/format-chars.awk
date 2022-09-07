#!/usr/bin/awk -f

# Hackery to format the data structure for the entries.

BEGIN {printf( "[[ "); COL=0;}
{printf( "\"%s\" ", $1);
COL++;
if (COL == 12) {
    COL=0;
    printf(" ]\n[ ");
}}
END {print( " ]]");}