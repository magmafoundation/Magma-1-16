<div align="center">
<img src="https://i.imgur.com/zTCTCWG.png" alt="Magma logo" align="middle"></img>

![](https://img.shields.io/badge/Minecraft%20Forge-1.16.5%20--%2036.1.32%20--%2029f1757-orange.svg?style=for-the-badge) ![](https://img.shields.io/badge/Bukkit-1.16%20r5-blue?style=for-the-badge) ![](https://img.shields.io/badge/CraftBukkit-Build%2029dd68e-orange?style=for-the-badge) ![](https://img.shields.io/badge/Spigot-Build%20988b411-yellow?style=for-the-badge) [![](https://img.shields.io/github/workflow/status/magmafoundation/Magma-1.16.x/Dev-Builds?style=for-the-badge)](https://github.com/magmafoundation/Magma-1.16.x/actions/workflows/dev-builds.yaml)
</div>

## About

Magma is the next generation of hybrid minecraft server softwares.

Magma is based on Forge and Paper, meaning it can run both Craftbukkit/Spigot/Paper plugins and Forge mods.

We hope to eliminate all issues with craftbukkit forge servers. In the end, we envision a seamless, low lag Magma experience with support for newer 1.12+ versions of Minecraft.

## Deployment

### Installation

1. Download the recommended builds from the [**Releases** section](https://github.com/magmafoundation/Magma-1.16.x/releases) (**Download** the one that ends in installer) 
   1. Or Download the latest jar from [Magma Site](https://magmafoundation.org/)
2. Make a new directory(folder) for the server
3. Move the jar that you downloaded into the new directory
4. Run the jar with your command prompt or terminal, going to your directory and entering `java -jar Magma-[version]-installer.jar --installServer`. Change [version] to your Magma version number.
5. This will generate another jar `forge-[version].jar` run this as normal `java -jar forge-[version].jar`

### Building the sources

- Clone the Project
    - You can use Git GUI (like GitHub Desktop/GitKraken) or clone using the terminal using:
        - `git clone https://github.com/magmafoundation/Magma-1.16.x/`
- Building
    - First you want to run the build command
        - `./gradlew setup installerJar`
    - Now go and get a drink this may take some time
    - Navigate to `projects/magma/build/libs` directory of the compiled source code
    - Copy the Jar to a new server directory (see Installation)
    
## Contribute to Magma

If you wish to inspect Magma, submit PRs, or otherwise work with Magma itself, you're in the right place!.

Please read the [CONTRIBUTING.md](https://github.com/magmafoundation/Magma-1.16.x/blob/master/CONTRIBUTING.md) to see how to contribute, setup, and run.

## Chat

You are welcome to visit Magma Discord server [here](https://discord.gg/6rkqngA).

You could also go to Magma's subreddit [here](https://www.reddit.com/r/Magma).

## Partners

<a href="https://aternos.org/en/"><img src="https://company.aternos.org/img/logotype-blue.svg" width="200"></a>
<a href="https://serverjars.com/"><img src="https://serverjars.com/assets/img/logo_white.svg" width="200"></a>

![YourKit-Logo](https://www.yourkit.com/images/yklogo.png)

[YourKit](http://www.yourkit.com/), makers of the outstanding java profiler, support open source projects of all kinds with their full featured [Java](https://www.yourkit.com/java/profiler/index.jsp) and [.NET](https://www.yourkit.com/.net/profiler/index.jsp) application profilers.
