#!/bin/bash

# concatenate audio for each column from top to bottom.
# Should be run from within the public/resources/audio directory AFTER the
# 'audio-links.sh' script has run.

for col in A B C D E F G H I J K L M
do
    mp3wrap ttblong${col}.mp3 [0-9][0-9]${col}.mp3
    
done

for col in a b c d e f g h i j k l m
do
    mp3wrap ttbshort${col}.mp3 [0-9][0-9]${col}.mp3
    
done



