#!/bin/bash
cd "$(dirname "$0")"
rm -f *.class backend/*.class frontend/*.class
javac -cp . Main.java backend/*.java frontend/*.java
java -cp . Main