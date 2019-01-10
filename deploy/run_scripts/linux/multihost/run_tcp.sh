#!/usr/bin/env bash

nValue=3;
baseFolderPath="../../../TORrent_";

lockFile=""
myPort=""

function getClientNumber {
    let "number=$RANDOM % $nValue + 1 | bc";
    let "port=$number + 10000";

    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null ; then
        echo $(getClientNumber)
    else
        echo $(checkFolder $number);
    fi
}

function getClientNumbers {
    except=$1
    clientsPorts=""
    for ((i=1;i<=$nValue;i++)); do
        if [[ ${i} != ${except} ]]; then
            let "otherPort=10000 + $i"
            clientsPorts="$clientsPorts $otherPort"
        fi;
    done
    echo ${clientsPorts};
}

function checkFolder {
    path="$baseFolderPath$1/"
    file="lock"
    lockFile="${path}${file}"

    echo "Lock file: $lockFile" >&2

    if [[ -e ${lockFile} ]]; then
        echo $(getClientNumber);
    else
        touch "${lockFile}";
        echo $1
    fi
}

myNumber=$(getClientNumber)
let "myPort=10000+${myNumber}";
myFolder="$baseFolderPath$myNumber";

echo "Client number ${myNumber}";
echo "Client port ${myPort}";
echo "Client folder ${myFolder}";

otherPorts=$(getClientNumbers ${myNumber})
echo "Other client ports ${otherPorts}";


java -jar ../../../torro-1.0-SNAPSHOT.jar false false ${myPort} ${myFolder} ${otherPorts};

rm "$myFolder/lock";

