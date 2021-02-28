# ALICA Plan Designer 

[![Build status](https://ci.appveyor.com/api/projects/status/github/dasys-lab/alica-plan-designer-fx?branch=master&svg=true)](https://ci.appveyor.com/project/StefanJakob/alica-plan-designer-fx/branch/master)

This is the repository of the new ALICA Plan Designer. Please don't mix it up with the old version, because they are incompatible.

## Run

In order to run the Plan Designer, download the standalone JAR file from the [latest release](https://github.com/rapyuta-robotics/alica-plan-designer-fx/releases) and execute it on a terminal via `java -jar <filename>`. For further explanation consider the [User Guide](doc/user_guide/user_guide.md).

## Develop

In order to start contributing to the Plan Designer, please have a look at the [Developer Guide](doc/developer_guide/developer_guide.md). It will show you how to setup Intellij and explain the architecture and design paradigms the Plan Designer is following.

## Requirements

The new plan designer has only a few dependencies which will be installed when you pull them via the Maven dependencies during the setup of Intellij. 

### Operating System

The Plan Designer is developed under [Ubuntu 18.04 LTS](http://releases.ubuntu.com/18.04.4/?_ga=2.96359836.1230837367.1596109511-129399482.1596109511), but we know that it is also running under Mac. Here, from time to time there are issues with setting the task bar icon of the Plan Designer, because Mac OS is rather inconsistent about the API for setting this symbol. A common solution here, is to comment the method, which is trying to set the icon. Further, we expect new versions of Ubuntu to work as well. Please ping us or write tickets, if you have  any problem. We will try to fix all of them ;-)

### Java

The only dependency, that you need to install manually is Java OpenJDK11:

`sudo apt install openjdk-11-jdk`

Please check whether openjdk-11 is your active java installation on your system:

`java -version`

The output should say something like this:

`openjdk version "11.0.8" 2020-07-14`
`OpenJDK Runtime Environment (build 11.0.8+10-post-Ubuntu-0ubuntu118.04.1)`
`OpenJDK 64-Bit Server VM (build 11.0.8+10-post-Ubuntu-0ubuntu118.04.1, mixed mode, sharing)`

Otherwise you also might need to switch your active Java version with this command:

`sudo update-alternatives --config java`

## Maintainer

* Stephan Opfer (slack: [@Stephan](https://rapyuta-robotics.slack.com/team/UUUSVSSBY), mail: stephan.opfer@rapyuta-robotics.com)
* Alexander Jahl (mail: jahl@vs.uni-kassel.de)
