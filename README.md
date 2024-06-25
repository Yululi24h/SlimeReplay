# SlimeReplay
A lightweight replay API (plugin) compatible with versions 1.8 to 1.20.4.

## Features
- Seamlessly record and replay your Minecraft gameplay across different game versions.
- Lightweight and easy to integrate into your existing projects.
- Compatible with a wide range of Minecraft versions, from 1.8 all the way up to 1.20.4.

## Compatibility
SlimeReplay uses ViaVersion and ViaBackwards to make sure you can watch replays on different versions of Minecraft. This means you can record your gameplay on one version and watch it on another, making it easy to use on different servers.

## Installation
1. Download the latest version of SlimeReplay from the [releases page](#TODO:).
2. Place the SlimeReplay.jar file into your server's `plugins` folder.
3. Restart your server to load the plugin.

## Configuration
After installation, a configuration file will be generated in the `plugins/SlimeReplay` folder. Customize the settings according to your needs.

## Usage
To start recording a replay, use the following command:
``/slimereplay start <player> <replay-file>``

To stop recording, use the following command:
``/slimereplay stop <player>``

To replay a recorded session, use the following command:
``/slimereplay play <replay-file>``

## Optional Dependency
To get the most out of SlimeReplay, consider installing these plugins:

- [ViaVersion](https://www.spigotmc.org/resources/viaversion.19254/)
- [ViaBackwards](https://www.spigotmc.org/resources/viabackwards.27448/)

## Contributing
We welcome contributions from the community. Please fork the repository and submit pull requests with your changes.

## License
This project is licensed under the GNU General Public License v3.0. See the [LICENSE](LICENSE) file for more details.