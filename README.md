# KAIMyEntity-C

## 1.初めに

私はプロのプログラマーではありません。  
OpenGLなんかチンプンカンプンで、その上Javaのコードに触れたのもほぼ初めてです。  
ですので、バクが無い保証はできませんし、バグが発生してもサポートできません。

## 2.使い方

### 使用するもの

#### 必須

* このMODのJarファイル
* 使いたい3Dモデル(pmxもしくはpmd)
* KAIMyEntitySaba.dll
* MMDShader.fsh, MMDShader.vsh

#### ほぼ必須

* デフォルトのVMDファイル
* lightMap.png

#### 推奨

* モデルごとの専用VMDファイル
* model.properties

以上のファイルを下記のように配置します。

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
* はしごなどの上り下り(onClimbable.vmd)
* 睡眠(sleep.vmd)
* 騎乗(ride.vmd)
* 死亡(die.vmd)
* 任意のタイミングで再生機能なモーション4つ(custom_[1-4].vmd)
* 特定のアイテムを特定の腕で使ったときのモーション(itemActive_[itemName]_[left or right].vmd)
* 乗馬中の移動(onHorse.vmd)

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

#### やりたいこと(達成時期どころか、可能かどうかすら不明)

* プレイヤー以外もmodel.propertiesを読み込む
* エリトラを描画
