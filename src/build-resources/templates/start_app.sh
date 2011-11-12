#!/bin/sh

# This file was automatically created by @creator@ for "@i.am@" - do not edit

# setting pid
PID=$!
echo $PID > /var/run/@unix.script.name@.pid

# java vm on @i.am@
JAVA=@java.home.dir@/bin/java

cd @program.path@

CLASSPATH=@classpath.unix@

${JAVA} @java.vm.args@ -cp ${CLASSPATH} @java.main.class@ $@