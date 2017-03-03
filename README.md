# TwoBinManager - SDK

*Software Development Kit for TwoBinManager*


## Introduction

**TwoBinManager - SDK** includes interfaces and utilities for extending [TwoBinManager](https://github.com/giancosta86/TwoBinManager).

In particular, one can create new:

* **analytics providers**, by implementing *AnalyticsProvider*

* **generators**, by implementing *ProblemGenerator*

* **importers**, by implementing *Importer*


In addition to plugin-related traits, the library includes **TwoBinManagerServer**, a lightweight class wrapping network communication with *TwoBinManager's internal server* (introduced in version 3.0)

For further information, please consult the Scaladoc documentation.


## Requirements

Scala 2.11.8 or later and Java 8u101 or later are recommended to employ the library.


## Referencing the library

The library is available on [Hephaestus](https://bintray.com/giancosta86/Hephaestus) and can be declared as a Gradle or Maven dependency; please refer to [its dedicated page](https://bintray.com/giancosta86/Hephaestus/TwoBinManager-sdk).

Alternatively, you could download the JAR file from Hephaestus and manually add it to your project structure.

Finally, the library is also a standard [OSGi](http://www.slideshare.net/giancosta86/introduction-to-osgi-56290394) bundle which you can employ in your OSGi architectures! ^\_\_^



## Further references

* [TwoBinPack](https://github.com/giancosta86/TwoBinPack)

* [Facebook page](https://www.facebook.com/TwoBinPack-234021307010796)
