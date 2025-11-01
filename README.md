# CannonDebug

1.20 Fabric port of the CannonDebug plugin.

#### New!
 - Click to teleport to entity location, like OSMC
 - Support falling block debugging like concrete powder, anvils, dragon eggs, etc.
 - Error message improvements

*Best cannon debugger out there!*

## Supports

* WorldEdit 7.x
* Fabric 1.20.1 (Loader 0.17.3)

## Features

* Per user cannon logging
* Select which dispensers and sand blocks to log
  * Click the blocks you wish to log with "/c select"
  * WorldEdit selection support with "/c region"
  * Selections can be viewed in game with "/c view"
* Provides a unique ID per dispenser and sand block tracked
* Logs locations and velocities for TnT and sand every tick
* Caches and indexes all cannoning debug information
* Interactive and helpful listing GUI
* Sort and filter information by
  * Server tick
  * Entities' selection ID

## Download

[Obtain the latest compiled version of CannonDebug here](https://github.com/UltimateGG/CannonDebug-Fabric/releases)

## Installation

1. Download file to your computer
2. Drag and drop CannonDebug.jar to mods folder in server
3. Restart your server

## Permissions
Fabric permissions API supported.

| **Permission**           | **Description**                                     |
|--------------------------|-----------------------------------------------------|
| cannondebug.clear        | Clear either history or selections                  |
| cannondebug.help         | View the default help pages                         |
| cannondebug.history.all  | View latest history for all profiled entities       |
| cannondebug.history.help | View the history help pages                         |
| cannondebug.history.id   | View latest history for specific entity             |
| cannondebug.history.tick | View all tracker history for a specific server tick |
| cannondebug.page         | Access to use the pager system                      |
| cannondebug.preview      | Preview all selected blocks for profiling           |
| cannondebug.region       | Use the region selector tool utilizing WorldEdit    |
| cannondebug.select       | Use the hand selector tool                          |
| cannondebug.teleport     | Allow teleporting to tracked locations              |

## Contributing

* 4-space indentation
* UNIX line endings
* Braces on the same line

## License

CannonDebug is licensed under the [MIT license](https://tldrlegal.com/license/mit-license).
