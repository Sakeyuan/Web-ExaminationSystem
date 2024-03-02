@echo off

set LOCAL_DIR= C:\Users\Sake\Desktop\GraduationProject\examinationSystem\examinationSystemSpringboot\target\examination_system-0.0.1-SNAPSHOT.jar
set REMOTE_USER=ubuntu
set REMOTE_HOST=111.230.14.34
set REMOTE_PATH=~/home/server

scp  %LOCAL_DIR% %REMOTE_USER%@%REMOTE_HOST%:%REMOTE_PATH%
