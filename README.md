# MCPatcherPatcher

![Java 8](https://img.shields.io/badge/language-Java%208-9B599A.svg?style=flat-square)
[![GitHub license](https://img.shields.io/github/license/LambdAurora/MCPatcherPatcher?style=flat-square)](https://raw.githubusercontent.com/LambdAurora/MCPatcherPatcher/master/LICENSE)

A ~~cursed~~ resource pack converter from MCPatcher/OptiFine format to newer and alternative formats.

# Use

This project can be utilized in 2 ways:
- Fabric Mod -   This method converts the resource pack during runtime. This is the easiest, and only requires the jar to be put in the mods folder. It will not output any files.
- Standalone Jar -  This will output the converted files, so that you can modify them.

This project does not have the ability to display the resource packs, it simply just to convert them into formats for other Fabric mods.
The following list is all the mods that MCPatcherPatcher can convert to.
- [FabricSkyboxes](https://www.curseforge.com/minecraft/mc-mods/fabricskyboxes) - Adds Skybox support.
- [Varied Mob Textures](https://www.curseforge.com/minecraft/mc-mods/varied-mob-textures) - Adds CET (custom entity texture) and RET (random entity texture) support.

At the time of writing, this project does not have any releases and only works as a Fabric Mod.  If you would like to use this, follow the instructions below.

# Build

1. Clone this using `git clone` or Download and Extract the ZIP via GitHub.
2. Make sure your current folder is MCPatcherPatcher, if you cloned the repository `cd MCPatcherPatcher`.
3. Build the binary using `./gradlew shadowRemapJar` (macOS/Linux) or `gradlew shadowRemapJar` (Windows).
4. Inside the MCPatcherPatcher folder, the output jar will be located at `fabric/build/libs`.

