#/usr/bin/env bash
rm -rf out/package/FileWatcher
~/.jdks/openjdk-25.0.1-1/bin/jpackage.exe \
--type app-image  \
--app-version 0.1.0 --description "TCSS360 FileWatcher" \
--name "FileWatcher" --vendor "Group 5" \
--main-jar "./Drone Simulator Project.jar" \
--input ./out/artifacts/Drone_Simulator_Project_jar --main-class app.simulation \
--dest out/package
