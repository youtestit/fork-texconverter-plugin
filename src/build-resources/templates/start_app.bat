@echo off
TITLE @java.main.class@
@rem This file was automatically created by @creator@ for "@i.am@"

echo working dir is:
cd

%JAVA_HOME%/bin/java -version
echo Java home is: 
echo %JAVA_HOME%

echo Starting TexConverter
echo ---------------------

set CLASSPATH=@classpath.windows@

%JAVA_HOME%\bin\java @java.vm.args@ -classpath %CLASSPATH% @java.main.class@ %*
