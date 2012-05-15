@echo off

set _argNum=0

echo.

for %%i in (%*) do set /A _argNum+=1

if %_argNum% == 3 (
  start /B java -jar C:\Users\Administrator\bin\silk.jar -t %1 %2 %3
  goto:_EOF
)

if %_argNum% == 2 (
  start /B java -jar C:\Users\Administrator\bin\silk.jar -t %1 %2
  goto:_EOF
)

start /B java -jar C:\Users\Administrator\bin\silk.jar -t %1

:_EOF

echo.
