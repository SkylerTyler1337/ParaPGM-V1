ParaPGM
=======

ParaPenguin's public version of PGM utilising the OCN XML system!

If you have a new feature that you think should be part of this plugin, submit a PR and I'll most likely add it within 7 days.

Todo List
---------
- [ ] XML Parsing of Filters
- [ ] Filters that actually have connected events
- [ ] Add Blitz support
- [ ] Add the Rage module
- [ ] Update to the new version of Overcast Tracker
- [ ] Add support for Gear maps

Compiling your own version of ParaPGM
-------------------------------------------
First things first, you're going to need to download and **install** [Lombok] [5] onto Eclipse, this is so that the methods work. Make sure you restart Eclipse after install *cough* *cough*

Next, you're going to need to import the following jars into your Eclipse project!
- [SportBukkit Server & API] [1] (_you need both files_)
- [dom4j Library] [2]
- [Lombok] [5]

After that, export the plugin and you're ready to roll! Just add it to your plugins folder, and follow the setup process below.

1. Getting and Setting up SportBukkit
-------------------------------------
First of all, this plugin **requires** SportBukkit! So before going any further, if you don't have it, you'll need to compile or you can find a version of **SportBukkit** that my servers use, located [here] [1] (you need `sportbukkit`, not `sportbukkit-api`)

If you don't know what **SportBukkit** is, then you're pretty silly and you need to place it as though it was your CraftBukkit/Spigot jar.

2. Getting the libraries you need
---------------------------------
ParaPGM requires a few libraries, these are pretty easy to setup.
Where your craftbukkit.jar is located, make a folder called "**libs**", and add [dom4j] [2] to it.

3. Creating and adding to the maps repository
---------------------------------------------
Before you can go any further, you must make sure that your Maps repository contains AT LEAST 1 map, without this the plugin will have a little cry and fail to load correctly.

You can see the format of the repository [here] [4] or [here] [3]. Those steps are pretty easy and no modifications should be needed to setup your server, as the map.xml is read just like PGM would!

The maps repository should be a folder named 'maps' in the root of the server folder. (This is the same location as the server jar.)

[1]: http://ci.maxsa.li/job/SportBukkit/lastBuild/ "SportBukkit"
[2]: http://scrimmage1.teamloading.com/dom4j.jar "dom4j"
[3]: https://maps.oc.tc/ "Overcast Maps"
[4]: http://scrimmage1.teamloading.com/ "Scrimmage Maps"
[5]: http://projectlombok.org/ "Project Lombok"
