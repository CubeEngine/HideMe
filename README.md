HideMe
======

Description:
------------
HideMe allows you to be completely invisable on your server and with completely I mean:
- Players can't see you
- A fake disconnect event is called and the disconnect message is shown
- you can't pickup items
- monsters don't react on you
- you only drop items with the specific permission
- you don't trigger pressure plates

Installation:
-------------

Just drop the jar-file into your plugins directory and reload/restart your server

Features:
---------

- **hide [player]**: Hides yourself or another player
- **unhide [player]**: Unhides yourself or another player
- **hidden [player]**: Checks whether you or another player is hidden
- **seehiddens**: Toggles The ability to see hidden players
- **canseehiddens [player]**: Checks whether your or anther player can see hidden players
- **listhiddens**: Lists the hidden players
- **listseehiddens**: Lists the players who can see hidden players


Permissions:
------------

- **HideMe.\*** - Allows the player to do everything
    - **HideMe.hide** - Allows the player to hide and unhide himself
    - **HideMe.hide.others** - Allows the player to hide and unhide other players
    - **HideMe.seehiddens** - Allows the player to use the seehiddens command
    - **HideMe.canseehiddens** - Allows the player to check whether he can see hidden players
    - **HideMe.canseehiddens.others** - Allows the player to check whether another player can see hidden players
    - **HideMe.listhiddens** - Allows the player list the hidden players
    - **HideMe.listseehiddens** - Allows the player to list players who can see hiddens
    - **HideMe.drop** - Allows the player to drop items
- **HideMe.hide.auto** - Players with this permission are automaticly hidden
- **HideMe.seehiddens.auto** - Players with this permission automaticly see other hidden players

Configuration:
--------------

- None available

***[Source on Github](https://github.com/quickwango/HideMe)***

Plugin developed by Quick_Wango - [Parallel Universe](http://parallel-universe.de)