#!/bin/bash

# concatenate audio for each row from right to left.
# Should be run from within the public/resources/audio directory AFTER the
# 'audio-links.sh' script has run.

for row in 01 02 03 04 05 06 07 08 09 10 \
    11 12 13 14 15 16 17 18 19 20 \
    21 22 23 24 25 26 27 28 29 30 \
    31 32 33 34 35 36 37 38 39
do
    longs=`ls ${row}[A-Z].mp3 | sort -r`
    ll=${longs//$ }
    mp3wrap rtllong${row}.mp3 ${longs}
    shorts=`ls ${row}[a-z].mp3 | sort -r`
    mp3wrap rtlshort${row}.mp3 ${shorts}
done