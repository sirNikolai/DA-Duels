| License | Javadoc |
| ------- | ------- |
| [![License](https://img.shields.io/github/license/sirNikolai/DA-Duels)](LICENSE.md)| [![Javadocs](https://img.shields.io/badge/Javadocs-1.0.0-orange.svg)](https://sirnikolai.github.io/DA-Duels/index.html)

# DA Duels
Organized dueling plugin, created for [Dumbledore's Army](http://http://dumbledoresarmy.enjin.com/).

# Installation
1. Compile JAR through maven.
2. Place in `plugins` directory of server.
3. Wait for `config.yml` to generate in `plugins/DA-Duels` folder.
4. Change values as needed.
5. Rerun server.

# Dependencies
| Name               | Type                  |
| ------------------ | --------------------- |
| DASpells           | Spigot Plugin         |
| MySQL              | Database              |
| https://sirnik.dev/dastatsfrontend/#/duelers | Data aggregation site |

# Commands
_NOTE_: all commands are prefaced with `/daduels` e.g `/daduels help`.

Commands with `<>` are mandatory arguments, `[]` are optional.

| Command   | Arguments        | Alternative Names              | Permission                 | Description                                        | Permission Level | Notes |
| --------  | ---------------- | ------------------------------ | -------------------------- | -------------------------------------------------- | ---------------- | ----- |
| help      |                  |                                | `daduels.cmds`             | Lists all available commands                       | Player           |       |
| list      |                  | l, op, guis                    | `daduels.opengui`          | Opens GUI to choose arena                          | Player           |       |
| exit      | `[Player Name] ` | remove                         | `daduels.exitArena`        | Leave arena (will count as loss if in game)        | Player           | Staff can supply player name to remove others. Requires `daduels.exitArena.others` |
| toggle    | `<Arena Name>`   |                                | `daduels.toggleArena`      | Toggles status of Arena (open/closed)              | Staff            |       |
| save      |                  | finish                         | `daduels.saveArena`        | Save arena currently selected in creation mode     | Staff            | Only to be done after `new` or `edit` command  |
| create    | `<Arena Name>`   | new                            | `daduels.createArena`      | Creates a new arena (if name isn't taken)          | Staff            | Need to `save` after picking spawn points      |
| edit      | `<Arena Name>`   |                                | `daduels.editArena`        | Edit spawn points for arena                        | Staff            | Need to `save` after picking spawn points      |
| delete    | `<Arena Name>`   | del                            | `daduels.deleteArena`      | Deletes an arena with specified Name               | Staff            |       |
| blacklist | `<add/remove> <Arena Name> [Spell1,Spell2,..]` |  | `daduels.blacklistSpells` | Add/remove spells from arena blacklist             | Staff            |       |

# Support
If bugs are discovered please submit a bug report [here](https://github.com/sirNikolai/DA-Duels/issues/new).
Features may also be requested in the link above.
Pull requests associated with bugs will be addressed quicker than those without.