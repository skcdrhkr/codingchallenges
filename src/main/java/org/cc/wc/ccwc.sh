#!/bin/bash

byteCount='false'
lineCount='false'
wordCount='false'
charCount='false'

[ $# -ge 1 -a -f "${@: -1}" ] && input="${@: -1}" || input="-"

no_args="true"
while getopts ':clwm' 'flag' ;
do
  case ${flag} in
    'c') byteCount='true';;
    'l') lineCount='true';;
    'w') wordCount='true';;
    'm') charCount='true';;
    \?) echo "Error: Invalid Option"
    exit;;
  esac
  no_args="false"
done

if [[ $no_args == "true" ]];
then
byteCount='true'
lineCount='true'
wordCount='true'
fi

echo $input

java -DbyteCount=$byteCount -DlineCount=$lineCount -DwordCount=$wordCount -DcharCount=$charCount WordCount.java "$input"
