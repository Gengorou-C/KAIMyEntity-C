# Forge バージョン

## 1.はじめに

僕はプロのプログラマーではありません。  
OpenGLなんかチンプンカンプンで、なんならJavaのコードに触れたのもほぼ初めてです。  
ですので、バクが無い保証はできませんし、バグが発生してもサポートできません。  
というか、Forgeに移植とはいえ、そんなに大したことやってないです。  
あと、OptiFineと競合する可能性があります。

## 2.使い方

### 必要なもの

* このMODのJarファイル
* 使いたい3Dモデル(pmxもしくはpmd)
* KAIMyEntitySaba.dll
* MMDShader.fsh, MMDShader.vsh
* VMDファイル

以上のファイルを下記のように配置します。

```bash
.
├── config
├── KAIMyEntity
│   ├── DefaultAnim
│   │   └── (vmdファイル達)
│   ├── EntityPlayer
│   │   ├── (テクスチャのファイルとかフォルダとか)
│   │   ├── (あるのであればモデル専用のvmdファイル)
│   │   └── model.pmx(または model.pmd)
│   ├── EntityPlayer_(Your Name)
│   │   ├── (テクスチャのファイルとかフォルダとか)
│   │   ├── (あるのであればモデル専用のvmdファイル)
│   │   └── model.pmx(または model.pmd)
│   ├── (":"を"."に変換したエンティティのID)
│   │   ├── (テクスチャのファイルとかフォルダとか)
│   │   ├── (4つのvmdファイル)
│   │   └── model.pmx(または model.pmd)
│   └── Shader
│       ├── MMDShader.fsh
│       └── MMDShader.vsh
├── logs
├── mods
│   └──KAIMyEntity.jar
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

### other

* 棒立ち(idle.vmd)
* 歩行(walk.vmd)
* 水泳(swim.vmd)
* 乗せる(ridden.vmd)

## その他

* アイテムを持った時、使った時の角度を少し弄れるようにしました。model.pmxと同じフォルダにitemRotation.propertiesを置いてください。書き方はReleaseに置いてあるファイルとソースコードを参考にしてください。
* MMDShader.vshとMMDShader.fshに少し変更を加えています。
