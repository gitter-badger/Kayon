# Kayon

Kayon is a reference utility for the Latin language. You can enter a finite Form into the graphical user interface
and get it's translation(s), what form it is, etc...

## Building on your own

1. If you don't have it already, get the source code by either [cloning this repository][1] or downloading the [source zip file][2] (you will need to extract it into a empty new directory).
2. Double-click the `gradlew.bat` file on windows, on unix-like systems run the file `./gradlew` in a terminal. It should already have the required `+x` mode. This will run the two tasks `clean` and `build`.
3. Once the process finishes, the jar files will be ready for you in `build/libs/`.
   Test results can be seen by viewing the file `build/reports/tests/index.html`.
   The javadoc files can be accessed by  viewing the file `build/docs/javadoc/index.html` or by unzipping the javadoc jar file (located in `build/libs/`) and then viewing the extracted `index.html` file.

To re-build the project after you changed some sources, simply repeat the steps above. There is no need to delete the `build` directory, it will be done automatically for you every time.

## Executing tasks other than `clean build`

1. If you don't have it already, get the source code by either [cloning this repository][1] or downloading the [source zip file][2] (you will need to extract it into a empty new directory).
2. Open a terminal (on windows the command prompt)

[1]: https://help.github.com/articles/cloning-a-repository/
[2]: https://github.com/RAnders00/Kayon/archive/master.zip
