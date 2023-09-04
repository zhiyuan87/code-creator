@echo off
cd ../
@echo on
call gradlew.bat wrapper --gradle-version=8.3 --distribution-type=all
@cmd /k
