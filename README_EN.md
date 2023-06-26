# KAIMyEntity-C

KAIMyEntity allows you to render 3D models of MikuMikuDance instead of default entities.  
  
I'm not professional programer.  
I can't support and guarantee that this mod work without any bugs.

## How to use

### What you need to prepare

#### indispensable

* [this mod](https://github.com/Gengorou-C/KAIMyEntity-C/releases)
* 3D model (PMX or PMD)
* [KAIMyEntitySaba.dll](https://github.com/Gengorou-C/KAIMyEntitySaba/releases/tag/20221215)
* [MMDShader.fsh, MMDShader.vsh](https://github.com/Gengorou-C/KAIMyEntity-C/releases/tag/requiredFiles)

#### almost indispensable

* [default VMD files](https://github.com/Gengorou-C/KAIMyEntity-C/releases/tag/requiredFiles)
* [lightMap.png](https://github.com/Gengorou-C/KAIMyEntity-C/releases/tag/requiredFiles)

#### recommended

* dedicated VMD files for each 3D models
* [model.properties](https://github.com/Gengorou-C/KAIMyEntity-C/releases/tag/requiredFiles)

### Installation

(1) Download appropriate jar file, and put it in mods folder.  
(2) Run minecraft.  
(3) If KAIMyEntity folder does't exist in Game directory, this mod will download a ZIP file and extract it.  
(4) If KAIMyEntitySaba.dll does't exist in Game directory, it will be downloaded.  
(5) Open KAIMyEntity folder, and copy and paste EntityPlayer folder.  
(6) Rename the copied EntityPlayer folder "EntityPlayer_(YourName)".  
 (e.g.) "EntityPlayer_Gengorou-C"  
(7) Put 3D model files in EntityPlayer_(YourName) folder.  
(8) Rename the 3D model file "model.pmx" (or "model.pmd").  
(9) Select world, and start the game.

### Exmaple of directory tree

```bash
.
├── config
├── KAIMyEntity
│   ├── DefaultAnim
│   │   └── default VMD files
│   ├── EntityPlayer
│   │   ├── Texture files
│   │   ├── dedicated VMD files
│   │   ├── lightMap.png
│   │   ├── model.properties
│   │   └── model.pmx (or model.pmd)
│   ├── EntityPlayer_(Player Name)
│   │   ├── Texture files
│   │   ├── dedicated VMD files
│   │   ├── lightMap.png
│   │   ├── model.properties
│   │   └── model.pmx (or model.pmd)
│   ├── (entity ID) (e.g. minecraft.horse)
│   │   ├── Texture files
│   │   ├── dedicated VMD files
│   │   ├── lightMap.png
│   │   └── model.pmx (or model.pmd)
│   └── Shader
│       ├── MMDShader.fsh
│       └── MMDShader.vsh
├── logs
├── mods
│   └──KAIMyEntityC.jar
├── saves
├── shaderpacks
├── KAIMyEntitySaba.dll
└── ...
```

## Motion list

### Player

* idle.vmd
* walk.vmd
* sprint.vmd
* sneak.vmd
* swingRight.vmd
* swingLeft.vmd
* elytraFly.vmd
* swim.vmd
* onClimbable.vmd
* onClimbableUp.vmd
* onClimbableDown.vmd
* sleep.vmd
* ride.vmd
* die.vmd
* custom_[1-4].vmd
* itemActive_[itemName]\_[Left or Right]_[using or swinging].vmd  
(e.g. itemActive_minecraft.shield_Left_using.vmd)  
(dedicated motion for each items)
* onHorse.vmd
* crawl.vmd
* lieDown.vmd

### entity

* idle.vmd
* walk.vmd
* swim.vmd
* ridden.vmd
* driven.vmd

## others

* If you want to change model size, or item angle, you need to edit model.properties.  
