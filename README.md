# Quark-a-fying Web Applications

This set of projects looks at taking a simple JSP + Servlet based application and converting it to run on the [Quarkus](https://quarkus.io/) framework.  This repository includes the following projects:

| Project | Description |
| ----------- | ----------- |
| **jspServlet**| Demonstrates a servlet I created to add JSP support to Quarkus |
| **insultcounter1** | A simple web application designed to be deployed on an app server such as Tomcat or EAP |
| **insultcounter2** | The same web app as above running in Quarkus |

You will also find several scripts:
| Script | Description |
| ----------- | ----------- |
| **start-jspServlet-dev.sh**| Starts the `jspServlet` application in dev mode with live coding |
| **start-insultcounter2-dev.sh** | Starts the `insultcounter2` application in dev mode with live coding |
| **build-insultcounter2-native.sh** | Performs the native build of `insultcounter2` and also creates a container image. |
| **start-insultcounter2-native.sh** | Starts the `insultcounter2` application using the native binary. |
| **start-insultcounter2-native-container.sh** | Starts the `insultcounter2` application using the native binary (container). |

Note that these scripts use podman but adapting to docker would be trivial.
