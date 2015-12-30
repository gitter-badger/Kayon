# Kayon [![Circle CI](https://circleci.com/gh/RAnders00/Kayon.svg?style=svg)](https://circleci.com/gh/RAnders00/Kayon) [![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/RAnders00/Kayon?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

[![Join the chat at https://gitter.im/RAnders00/Kayon](https://badges.gitter.im/RAnders00/Kayon.svg)](https://gitter.im/RAnders00/Kayon?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Kayon is a reference utility for the Latin language. You can enter a finite Form into the graphical user interface
and get it's translation(s), what form it is, etc...

It is currently in it's creation stage. Nothing is final or finished yet.

You can find documentation for the `master` branch [here][1]. The repository for the docs is [here][2].

## Building artifacts from source

Building from source sounds scary. In this case, it really is not. All you will need is a Java Development Kit (8 or higher) and the source code of this repository.
The build setup of this project will take care of the rest for you.

If you don't have it already, get the source code by either [cloning this repository][3] or downloading the [source zip file][4] (you will need to extract the zip file into an empty new directory).
You also need a Java Development Kit (JDK) installed on your computer. Consult a search engine to find out on how to do this.

* Windows: Double-click the `gradlew.bat` file.
* Unix-like systems: Run the `./gradlew` file in a terminal. (It should already have the required `+x` mode.)

Once the process finishes, the jar files will be ready for you in `build/libs/`. **If you just wish to run the application, double-click `Kayon-<version>-all.jar`.**
The javadoc files can be accessed by  viewing the file `build/docs/javadoc/index.html` or by unzipping the javadoc jar file (located in `build/libs/`) and then viewing the extracted `index.html` file.

To re-build the project after you changed some sources, simply repeat the steps above. There is no need to delete the `build` directory, it will be done automatically for you every time.

## Executing other tasks

If you don't have it already, get the source code by either [cloning this repository][3] or downloading the [source zip file][4] (you will need to extract the zip file into an empty new directory).
You also need a Java Development Kit (JDK) installed on your computer. Consult a search engine to find out on how to do this.
Open a terminal (on windows the command prompt) and type `./gradlew <your tasks>`, for example `./gradlew test jar javadocJar`.

## Changing the version

The version of the projects is written to [`src/main/resources/version`][5]. This is where the version should be changed.
You can access this property in programming by calling [`public static String cf.kayon.core.util.KayonReference.getVersion()`][6].

The build ID is written to [`src/main/resources/build`][7]. If the project is being built by CircleCI (determined by the `CIRCLECI` environment variable being set to `true`),
the build ID [will be injected][8] into this file by Gradle.
If you build Kayon manually, the build ID will remain as `-1`.

You can access this property in programming by calling [`public static int cf.kayon.core.util.KayonReference.getBuild()`][9].

[1]: https://randers00.github.io/KayonDoc/
[2]: https://github.com/RAnders00/KayonDoc
[3]: https://help.github.com/articles/cloning-a-repository/
[4]: https://github.com/RAnders00/Kayon/archive/master.zip
[5]: https://github.com/RAnders00/Kayon/blob/master/src/main/resources/version
[6]: https://randers00.github.io/KayonDoc/cf/kayon/core/util/KayonReference.html#getVersion--
[7]: https://github.com/RAnders00/Kayon/blob/master/src/main/resources/build
[8]: https://github.com/RAnders00/Kayon/blob/master/build.gradle#L32-L33
[9]: https://randers00.github.io/KayonDoc/cf/kayon/core/util/KayonReference.html#getBuild--
