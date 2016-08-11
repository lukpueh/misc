#! /bin/bash

# On OS X 10.11
gcc -I/System/Library/Frameworks/JavaVM.framework/Headers -shared -o libHelloWorld.jnilib -fPIC HelloWorld.c
javac HelloWorld.java
export LD_LIBRARY_PATH="."
java  HelloWorld
