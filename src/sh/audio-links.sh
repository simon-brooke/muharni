#!/bin/bash

# This script creats links from the audio recordings in the way I received them 
# to a naming system which better supports the way I want to use them.

# It should be run from within the resources/public/audio directory before any 
# of the 'concat' scripts.

for file in */*/*.mp3 
do
    
    new=`echo $file | sed 's?Row\([0-9]*\).*/short/[0-9]*\([A-Za-z]\).*.mp3?\1\2.mp3?'`
    
    ln $file ${new}
    # echo "$file -> $new"
done

for file in */*.mp3 
do
    
    new=`echo $file | sed 's?Row\([0-9]*\).*/[0-9]*\([A-Za-z]\).*.mp3?\1\2.mp3?'`
    
    ln $file ${new}
    # echo "$file -> $new"
done