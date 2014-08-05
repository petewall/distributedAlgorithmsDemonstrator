javac -cp junit-4.11.jar:hamcrest-core-1.3.jar:. \
    pwall/*.java \
    pwall/isis/*.java \
    pwall/gui/*.java \
    pwall/lamport/*.java \
    pwall/paxos/*.java \
    pwall/simple/*.java \
    pwall/vector/*.java
if [ $? != 0 ] ; then
    echo BUILD FAILED!
    exit 1
fi

