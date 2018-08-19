# PlugNCraft
Powerful automatic minecraft servers management API

## Content of the package
- PlugNManager: The main program that generate and launch the servers
- PlugNAPI: The main plugin that manage the server, the players, the games, etc...
- PlugNHub: The hub plugin that manage menus, server switching and ask for starting
- PlugNReplica: Adapation of the Replica plugin to PlugNAPI (an example to create your own game)
- HungerGames: An HungerGames plugin that work with PlugNAPI

## How to use
- Create a folder where you will put all your servers content.
- Open PlugNManager and edit the getConnection() method with your database credentials and the getDiscord() with the token of your discord bot (create one at https://discordapp.com/developers/applications/ if needed).
- Import the plugncraft.sql file in your database, to get the default tables and configurations.
- Build PlugNManager with fr.zabricraft.plugnmanager.PlugNManager as the main class and put the jar file in this folder. It's the file you will launch to start the service.
