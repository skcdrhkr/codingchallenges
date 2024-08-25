#!/bin/bash

byteCount='false'
lineCount='false'
wordCount='false'

while getopts ':clw' 'flag' ;
do
  case ${flag} in
    'c') byteCount='true';;
    'l') lineCount='true';;
    'w') wordCount='true';;
    \?) echo "Error: Invalid Option"
    exit;;
  esac
done

java -DbyteCount=$byteCount -DlineCount=$lineCount -DwordCount=$wordCount WordCount.java "${@: -1}"
