@echo off
title BBDU Campus Placement Tracker - GUI Dashboard
echo Launching Campus Placement Tracker Swing GUI Dashboard...
start javaw -jar "%~dp0target\campus-placement-tracker-1.0.0-jar-with-dependencies.jar" --gui
