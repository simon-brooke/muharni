#!/bin/bash

# concatenate audio for each row from left to right.
# Should be run from within the public/resources/audio directory AFTER the
# 'audio-links.sh' script has run.

for row in 01 02 03 04 05 06 07 08 09 10 \
    11 12 13 14 15 16 17 18 19 20 \
    21 22 23 24 25 26 27 28 29 30 \
    31 32 33 34 35 36 37 38 39
do
    mp3wrap ltrlong${row}.mp3 ${row}[A-Z].mp3
    mp3wrap ltrshort${row}.mp3 ${row}[a-z].mp3
done