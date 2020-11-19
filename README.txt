### How to compile and run
All the java files can be compiled in the src directory as below:
javac edu/asu/Constants.java
javac edu/asu/GpsValue.java
javac edu/asu/InsertionInfo.java
javac edu/asu/VehicleInfoEntry.java
javac edu/asu/VehicleInfoLogger.java

The program can be run in the src directory as:
java edu.asu.VehicleInfoLogger > out.txt

The output of the program is stored in the file "out.txt".


### IMPORTANT POINTS:
- I had originally planned to submit the code as a zip file with all the necessary packages, .java and .txt files,
  but Canvas is not letting me upload anything other than .java and .txt files.
- As seen in the video, all the java files are part of package edu/asu
- Please create the directory structure :

src -> edu -> asu -> *.java files

- This is because each java file is assumed to be part of the package ("package edu.asu;")
- You can try removing this from the first line of every java file as well, but the former is recommended and tested multiple times.
