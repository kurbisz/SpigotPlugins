# SpigotPlugins
## Information
This repository contains a collection of PaperSpigot
plugins developed using Maven and utilizing a MySQL database.
These plugins are designed to enhance the functionality and gameplay experience
of your Minecraft server running on PaperSpigot.

## Requirements
Before using these plugins, make sure you have the following prerequisites installed:
 - PaperSpigot
 - Java Development Kit (JDK) (version 9 or higher)
 - MySQL Server

Optionally if you want to build jar by your own you will also need:
 - Maven

## Setup
To set up the plugins on your server you can download them from builds or build it from sources:

1. Clone this repository to your local machine:
```
https://github.com/kurbisz/SpigotPlugins
```

2. Go into folder with plugin that you want to build and use:
```
mvn clean package
```

3. After a successful build, you will find the plugin JAR file in the `target` directory of this plugin.
4. Copy the generated JAR files to the `plugins` directory of your PaperSpigot server.
5. Restart your PaperSpigot server.

## Plugins

### Custom Events
#### Description
Provides different types of time events which can be called by admin or by for example special items
for users. Customizable setup with commands (with placeholders) after some events allows this plugin
to work well with a lot of other .jar files. It also offers an easy automize OX event setup with regions
and auto questions and rewards. In similar way ZUO events can be done with this plugin.

#### Requierements
 - MMOInventory
 - MythicMobs
 - WorldGuard
 - PlaceholderAPI

## License
This repository is licensed under the MIT License. Feel free to modify and distribute the plugins according to the terms of this license.