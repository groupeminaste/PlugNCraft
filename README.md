# PlugNCraft
Powerful automatic minecraft servers management API

## Content of the package
- PlugNManager: The main program that generate and launch the servers
- PlugNAPI: The main plugin that manage the server, the players, the games, etc...
- PlugNHub: The hub plugin that manage menus, server switching and ask for starting
- PlugNReplica: Adapation of the Replica plugin to PlugNAPI (an example to create your own game)
- HungerGames: An HungerGames plugin that work with PlugNAPI

## How to install
- Create a folder where you will put all your servers content.
- Open PlugNManager and edit in fr.zabricraft.plugnmanager.PlugNManager the getConnection() method with your database credentials and the getDiscord() with the token of your discord bot (create one at https://discordapp.com/developers/applications/ if needed).
- Import the plugncraft.sql file in your database, to get the default tables and configurations.
- Build PlugNManager with fr.zabricraft.plugnmanager.PlugNManager as the main class and put the jar file in this folder. It's the file you will launch to start the service.
- Start it a first time to generate the sub folders, and stop it by typing "stop".
- In the main folder, near your built version of PlugNManager, place a spigot build named "spigot.jar" (or the name you gave in fr.zabricraft.plugnmanager.minecraft.MinecraftServer)
- In the libs/ folder, place your built version of PlugNAPI, with a sub folder with its config file (the libs/ folder is like the plugins/ folder of all your server, everyfile in it will be copied in the plugins/ folder of all your servers while genrating them). Also place the ProtocolLib plugin and the CraftSearch plugin except if you remove their dependencies. If you have other plugins that you want to automatically copy on all your servers, it's in this folder.
- In the templates/ folder, create one sub folder for each type of server you will have. For example, one sub folder "hub", one "replica", one "hungergames", etc... In each folder you will have to sub folder that will be copied while generating a server of this type/game. A plugins/ folder for the games plugins (for the default database there are in the hub plugins' folder PlugNHub.jar, in the replica's one PlugNReplica.jar and in the hungergames's one HungerGames.jar), and a maps/ folder containing the maps (for game hub maps and game playing map). Which map is used as hub or game is configured in maps table in mysql database.
- After configuring that, you should be able to start it and play!
