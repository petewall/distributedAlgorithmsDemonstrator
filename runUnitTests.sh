echo BUILDING...
sh buildAll.sh
if [ $? != 0 ] ; then
    exit 1;
fi

echo RUNNING...
java -cp junit-4.11.jar:hamcrest-core-1.3.jar:. org.junit.runner.JUnitCore \
    pwall.Test_Network \
    pwall.Test_ProcessGroup \
    pwall.lamport.Test_LamportProcess \
    pwall.simple.Test_SimpleProcess
