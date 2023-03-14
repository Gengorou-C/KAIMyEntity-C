# KAIMyEntity-C

## 1.初めに

私はプロのプログラマーではありません。  
OpenGLなんかチンプンカンプンで、その上Javaのコードに触れたのもほぼ初めてです。  
ですので、バクが無い保証はできませんし、バグが発生してもサポートできません。

## 2.使い方

### 使用するもの

#### 必須

* [このMODのJarファイル](https://github.com/Gengorou-C/KAIMyEntity-C/releases)
* 使いたい3Dモデル(pmxもしくはpmd)
* [KAIMyEntitySaba.dll](https://github.com/Gengorou-C/KAIMyEntitySaba/releases/tag/20221215)
* [MMDShader.fsh, MMDShader.vsh](https://github.com/Gengorou-C/KAIMyEntity-C/releases/tag/requiredFiles)

#### ほぼ必須

* [デフォルトのVMDファイル](https://github.com/Gengorou-C/KAIMyEntity-C/releases/tag/requiredFiles)
* [lightMap.png](https://github.com/Gengorou-C/KAIMyEntity-C/releases/tag/requiredFiles)

#### 推奨

* モデルごとの専用VMDファイル
* [model.properties](https://github.com/Gengorou-C/KAIMyEntity-C/releases/tag/requiredFiles)

### 導入手順

(1)遊ぶマイクラのバージョンと前提Modを確認して、jarファイルをダウンロード、modsフォルダに配置。  
(2)マイクラを起動。  
(3)ゲームディレクトリにKAIMyEntityフォルダが無い場合、自動的にZipファイルをDL後に解凍します。  
(4)ゲームディレクトリにKAIMyEntitySaba.dllが無い場合、自動的にDLします。  
(5)KAIMyEntityフォルダ内のEntityPlayerフォルダをその場でコピー&ペースト。  
(6)コピーしたEntityPlayerフォルダを「EntityPlayer_(プレイヤー名)」に改名。  
(7)EntityPlayer_(プレイヤー名)フォルダにモデルのファイルたちを配置。  
(8)モデルのファイル名をmodel.pmx(またはmodel.pmd)に変更。  
(9)ワールドを選択してゲームを開始。

### ファイルの配置例

```bash
.
├── config
├── KAIMyEntity
│   ├── DefaultAnim
│   │   └── vmdファイル達
│   ├── EntityPlayer
│   │   ├── テクスチャのファイルとかフォルダとか
│   │   ├── モデル専用のvmdファイル
│   │   ├── lightMap.png
│   │   ├── model.properties
│   │   └── model.pmx(または model.pmd)
│   ├── EntityPlayer_(Player Name)
│   │   ├── テクスチャのファイルとかフォルダとか
│   │   ├── モデル専用のvmdファイル
│   │   ├── lightMap.png
│   │   ├── model.properties
│   │   └── model.pmx(または model.pmd)
│   ├── (":"を"."に変換したエンティティのID)(例：minecraft.horse)
│   │   ├── テクスチャのファイルとかフォルダとか
│   │   ├── モデル専用のvmdファイル
│   │   ├── lightMap.png
│   │   └── model.pmx(または model.pmd)
│   └── Shader
│       ├── MMDShader.fsh
│       └── MMDShader.vsh
├── logs
├── mods
│   └──KAIMyEntityC.jar
├── saves
├── shaderpacks
├── KAIMyEntitySaba.dll
└── (その他のファイル)
```

## 3.機能

* エンティティのモデルを変更できます。

### 対応モーション

### Player

* 棒立ち(idle.vmd)
* 歩行(walk.vmd)
* スプリント(sprint.vmd)
* スニーク(sneak.vmd)
* 右腕でアイテム使用(swingRight.vmd)
* 左腕でアイテム使用(swingLeft.vmd)
* エリトラでの飛行(elytraFly.vmd)
* 水泳(swim.vmd)
* はしごなどで停止(onClimbable.vmd)
* はしごなどの上り(onClimbableUp.vmd)
* はしごなどの下り(onClimbableDown.vmd)
* 睡眠(sleep.vmd)
* 騎乗(ride.vmd)
* 死亡(die.vmd)
* 任意のタイミングで再生機能なモーション4つ(custom_[1-4].vmd)
* 特定のアイテムを特定の腕で使ったときのモーション  
(itemActive_[itemName]\_[Left or Right]_[using or swinging].vmd)
* 乗馬中の移動(onHorse.vmd)
* 匍匐前進(crawl.vmd)
* 伏せ(lieDown.vmd)

### other

* 棒立ち(idle.vmd)
* 歩行(walk.vmd)
* 水泳(swim.vmd)
* 乗せる(ridden.vmd)
* 何かを乗せて移動(driven.vmd)

## その他

* model.propertiesでモデルのサイズと持っているアイテムの角度を変更できます。  
書き方はReleaseに置いてあるファイルやソースコードを参考にしてください。  
* lightMap.pngを編集するとモデルに当たる環境光やアイテム由来の光の色が変更できます。  
(シェーダー使用中はシェーダーの方が優先されます。)
* [requiredFiles](https://github.com/Gengorou-C/KAIMyEntity-C/releases/tag/requiredFiles)にあるKAIMyEntity.zipに入っているファイルに関してはご自由にお使いください。  
(クレジット表記不要、改変可、再配布可)

### やりたいこと(達成時期どころか、可能かどうかすら不明のものを含む)

* エリトラを描画
