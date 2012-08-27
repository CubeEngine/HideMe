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

***Watch out:*** This plugin is actually pretty hacky as it does some things that should not be done by plugins. That is needed to archive the complete invisibility, but it causes some minor problems that I won't fix (often it is nearly impossible to fix). Examples for such problems are:

- hidden players don't receive server broadcasts (/say command and commands like that, fixing this would be really hacky)
- plugins can't find the player (this is in most cases intended)
- some plugins might fail sometimes on hidden players (if have already added some fixes, but please report problematic plugins)

Dependencies:
-------------

- Bukkit 1.3.2 or newer

SpoutPlugin is ***NOT*** needed

Installation:
-------------
Just drop the jar-file into your plugins directory and reload/restart your server

Commands:
---------
- **/hide [player]** -- Hides yourself or another player
- **/unhide [player]** -- Unhides yourself or another player
- **/hidden [player]** -- Checks whether you or another player is hidden
- **/seehiddens** -- Toggles The ability to see hidden players
- **/canseehiddens [player]** -- Checks whether your or anther player can see hidden players
- **/listhiddens** -- Lists the hidden players
- **/listseehiddens** -- Lists the players who can see hidden players


Permissions:
------------

- **HideMe.\*** -- Allows the player to do everything
    - **HideMe.hide** -- Allows the player to hide and unhide himself
    - **HideMe.hide.others** -- Allows the player to hide and unhide other players
    - **HideMe.seehiddens** -- Allows the player to use the seehiddens command
    - **HideMe.canseehiddens** -- Allows the player to check whether he can see hidden players
    - **HideMe.canseehiddens.others** -- Allows the player to check whether another player can see hidden players
    - **HideMe.listhiddens** -- Allows the player list the hidden players
    - **HideMe.listseehiddens** -- Allows the player to list players who can see hiddens
    - **HideMe.drop** -- Allows the player to drop items
- **HideMe.hide.auto** -- Players with this permission are automaticly hidden
- **HideMe.seehiddens.auto** -- Players with this permission automaticly see other hidden players

***README***
============

Plugin developed by Quick_Wango - [Cube Island](http://cubeisland.de)

- You want new features?
- You want the plugin to be always up to date?
- You want good support?

I'm doing this for literally nothing in my freetime, so keep me interessted in my plugins and help pay my bills by simply donating a few bucks.

[![Donate](https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif "Donate")](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=2QU7NLJW3W58A)

Thanks in advance!

***[Talk to the developer](http://webchat.esper.net/?channels=cubeisland-dev&nick=)*** (#cubeisland-dev on EsperNet)

***[Source on Github](https://github.com/CubeIsland/HideMe)***