cd src\java02\jmx\jmxc
javac Main01.java
java -cp ../../../ ^
-Dcom.sun.management.jmxremote.port=10999 ^
-Dcom.sun.management.jmxremote.authenticate=false ^
-Dcom.sun.management.jmxremote.ssl=false ^
java02.jmx.jmxc.Main01A
pause

