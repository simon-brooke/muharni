#!/bin/bash

# concatenate audio for each column from bottom to top.
# Should be run from within the public/resources/audio directory AFTER the
# 'audio-links.sh' script has run.

for col in A B C D E F G H I J K L M
do
    longs=`ls [0-9][0-9]${col}.mp3 | sort -r`
    mp3wrap bttlong${col}.mp3 ${longs}
done

for col in a b c d e f g h i j k l m
do
    shorts=`ls [0-9][0-9]${col}.mp3 | sort -r`
    mp3wrap bttshort${col}.mp3 ${shorts}
done



