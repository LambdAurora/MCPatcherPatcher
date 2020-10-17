# MCPatcherPatcher

![Java 8](https://img.shields.io/badge/language-Java%208-9B599A.svg?style=flat-square)
[![GitHub license](https://img.shields.io/github/license/LambdAurora/MCPatcherPatcher?style=flat-square)](https://raw.githubusercontent.com/LambdAurora/MCPatcherPatcher/master/LICENSE)

A cursed resource pack converter from MCPatcher/OptiFine format to newer and alternative formats.

# Use

You can use this project in one of two ways:
1. Runtime conversion.  This is the easiest, and only requires the jar to be put in the mods folder.  It will not output any files.
2. Run as its own jar.  This will output the converted files, so that you can modify them.

This mod does not have the ability to display the resource packs, simply just to convert them into formats for other Fabric mods.
The following list is all the mods that MCPatcherPatcher can convert to.
1. [FabricSkyboxes](https://www.curseforge.com/minecraft/mc-mods/fabricskyboxes).  Adds Skybox support.
2. [Varied Mob Textures](https://www.curseforge.com/minecraft/mc-mods/varied-mob-textures).  Adds CET (custom entity texture) and RET (random entity texture) support.

At the time of writing, this project does not have any releases.  If you would like to use this, follow the instructions below.

# Build

1. `git clone` the repository to your computer.
2. `cd MCPatcherPatcher` to get into the MCPatcherPatcher folder where you cloned the repository to.
3. `./gradlew shadowRemapJar` builds the jar itself.
4. Inside the MCPatcherPatcher folder, the output jar will be located at `fabric/build/libs`.