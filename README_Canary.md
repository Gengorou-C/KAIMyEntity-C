# for Canary

### ほぼメモのようなもの。

- default (RendertypeEntityTranslucentShader or RendertypeEntityCutoutNoCullShader)
![default](image/default.png)

- using shader (SEUS-Renewed, KUDA-Shaders v6.1 Legacy)
![shader](image/SEUS.png)
![shader](image/KUDA.png)

工夫次第で影が描画可能です。ただし、かなり不安定なので環境によってはできないかもしれません。  

OptifineではなくRubidiumとOculusを使う。  
使いたいシェーダーを解凍、編集する必要あり。  
MMDShaderを使用している場合はおそらく正常に描画されない。  
シェーダーを使用中にモデルを再読み込みすると表示が崩れることがよくある。  
その際は使用するプログラムの変更をするキー(初期設定ではテンキーの3)を押すことでMMDShaderに切り替えてから戻してみる。  
プレイヤー以外のエンティティが停止中、歩行中などに表示がおかしいときは、表示がおかしくなっている時にMMDShaderに切り替えてから素早く戻すと治ることがある。  
シェーダーの中で編集する必要のあるファイルはgbuffers_entitiesまたはshadowという名前がついたファイルを見ると分かる。  

シェーダーの編集例がWhipped-Creamというフォルダに入っています。(MITライセンスなのでセーフのはず……)  
使うときは[本家](https://github.com/Zi7ar21/Whipped-Cream)からダウンロード、解凍、上書きをしてください。  
有名なシェーダーを編集してそのまま配布したかったのですが、ほぼ全て原則再配布禁止だったので、代わりに編集方法の一例を以下に書きます。  
明確な根拠とともに「これは再配布にあたる」という指摘が来た時には消します。

- SEUS-Renewed-v1.0.1
  - gbuffers_entities.vsh  
    main関数の直上に追加

      ```glsl
      attribute vec3 K_Position;
      attribute vec2 K_UV0;
      attribute ivec2 K_UV2;
      uniform mat4 K_ModelViewMat;
      uniform mat4 K_ProjMat;
      uniform sampler2D K_Sampler2;
      uniform int KAIMyEntityV;
      varying vec4 lightMapColor;
      ```

    最初のgl_Positionが出てきたらその直下に追加

    ```glsl
    if(KAIMyEntityV == 1){
      gl_Position = K_ProjMat * K_ModelViewMat * vec4(K_Position, 1.0);
      texcoord.st = K_UV0;
      blockLight.x = 1.0 * K_UV2.x/256.0;
      blockLight.y = 1.0 * K_UV2.y/256.0;
    }
    ```

  - gbuffers_entities.fsh  
    main関数の直上に追加

    ```glsl
    uniform sampler2D K_Sampler0;
    uniform int KAIMyEntityF;
    in vec4 lightMapColor;
    ```

    main関数の末尾に追加

    ```glsl
    if(KAIMyEntityF == 1){
      gl_FragData[0] = texture2D(K_Sampler0, texcoord.st);
      gl_FragData[1] = vec4(blockLight, 1.0, 1.0);
    }
    ```

  - shadow.vsh  
    main関数の直上に追加

    ```glsl
    attribute vec3 K_Position;
    uniform mat4 K_ModelViewMat;
    uniform mat4 K_ProjMat;
    uniform int KAIMyEntityV;
    ```

    最初のgl_Positionの次の行に追加

    ```glsl
    if(KAIMyEntityV == 1){
      gl_Position = K_ProjMat * K_ModelViewMat * vec4(K_Position, 1.0);
    }
    ```

- KUDA-Shaders v6.1 Legacy
  - gbuffers_entities.vsh  
    main関数の直上に追加

    ```glsl
    attribute vec3 K_Position;
    attribute vec2 K_UV0;
    attribute ivec2 K_UV2;
    uniform mat4 K_ModelViewMat;
    uniform mat4 K_ProjMat;
    uniform sampler2D K_Sampler2;
    uniform int KAIMyEntityV;
    varying vec4 lightMapColor;
    ```

    最初のgl_Positionが出てきたらその直下に追加

    ```glsl
    if(KAIMyEntityV == 1){
      texcoord = K_UV0;
      lmcoord = K_UV2/256.0;
      gl_Position = K_ProjMat * K_ModelViewMat * vec4(K_Position, 1.0);
    }
    ```

  - gbuffers_entities.fsh  
    main関数の直上に追加

    ```glsl
    uniform sampler2D K_Sampler0;
    uniform int KAIMyEntityF;
    in vec4 lightMapColor;
    ```

    main関数の末尾に追加

    ```glsl
    if(KAIMyEntityF == 1){
      gl_FragData[0] = texture2D(K_Sampler0, texcoord.st);
    }
    ```

  - shadow.vsh  
    main関数の直上に追加

    ```glsl
    attribute vec3 K_Position;
    uniform mat4 K_ModelViewMat;
    uniform mat4 K_ProjMat;
    uniform int KAIMyEntityV;
    ```

    最初のgl_Positionの次の行に追加

    ```glsl
    if(KAIMyEntityV == 1){
      gl_Position = K_ProjMat * K_ModelViewMat * vec4(K_Position, 1.0);
    }
    ```
