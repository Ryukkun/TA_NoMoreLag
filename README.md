# TA_NoMoreLag

## ナニコレ
Lagによるタイムの上振れを出来るだけなくすことを目標に作った<br>
ちなみに ping高いと 全然下振れするが、上振れすることはほとんどないと思う。<br>
逆に、pingが安定していれば、正しいタイムが取得できる確率が上がる　多分ね。<br><br>

精度を上振れを撲滅しようと奮闘しすぎたあまり、リソースをまあまあ使うPluginになってしまったかもしれない　それはまあ許して


## よういするもの
- ProtocolLib


## ToDo
- [x] 失敗 >> 多分 Positionとかのパケット取得しなくても PlayerMoveEvent だけでいけそう<br>こっちの方が軽そうだし やってみる。
- [x] protocollibのasyncイベントなんとかしなきゃあかん
- [x] ずっとTA情報 保存しておくのもあれだし、10分くらいオフラインの子は 進行中のTA停止させちゃう。
- [ ] 時間計測方式も導入する。ゴールまでパケロスが頻発し安定しなかった場合の対策。
- [ ] modいれてクライアント重くさせるの 試す
- [ ] タイムをskでも取得できる形で保存する。 scoreboardになるかな オブジェクトの名前制限あるけど まあええか


## タイム計測方式
### 時間計算方式（従来）
- 計測方法
    - 計測終了時の時刻と 計測開始時の時刻の差から算出。

- タイムズレ要因
    1. Serer tickのずれ
        - 1tickの処理を50ms以内に処理できなかった時 ずれるんじゃねって思ってる。サーバーとクライアント非同期だし、全然ずれそう
        - TAタイムの"**50ms以下**"のずれに関与する
    2. サーバー側のmspt（憶測）
        - TAの処理をTick処理が終わった後に行うとすれば、Tick処理にかかった時間によってタイムがズレるよねってこと。
        - 対策は簡単 Tick処理の前にTA処理を行うか、Tick開始時の時間をもとに計算するか
        - TAタイムの"**50ms以下**"のずれに関与する
    3. Playerとのping
        - タイム計測開始時のpingと計測終了時のpingのずれ
        - TAタイムの"**tick単位(50msの正の倍数)**"のずれに関与する

### Packetカウント方式（自作）
- 計測方法（パケロス対策）
    1. パケットが送られてくる間隔が安定するまで パケットをカウントする
    2. 安定したら 時間と現在のpingを記録する。
    3. ゴールしたら ゴールした時刻とpingを記録する。
    4. Time = (FinishTime - FPing) - (StartTime - SPing)
       
- カウント方法
    1. (終了時のパケットの取得時間 - ping) - (開始時のパケットの取得時間 - ping)
        - スタート時にpingを高くされたら 正しい計測ができない
    2. パケットが送られた数をカウント
        - パケロス、pingの変動 など かなり対策がむずい

- タイムズレ要因
    1. パケロス
    2. pingの変動

## Packet
- Keep Alive ([ToServer](https://wiki.vg/index.php?title=Protocol&oldid=14204#Keep_Alive_.28serverbound.29)) ([ToClient](https://wiki.vg/index.php?title=Protocol&oldid=14204#Keep_Alive_.28clientbound.29))
    - 説明 : サーバーは、ランダムなIDを含むキープアライブを頻繁に送信する。クライアントは同じパケットで応答しなければならない。クライアントが30秒以上応答しない場合、サーバーはクライアントをキックする。逆に、サーバーが20秒間キープアライブを送信しなかった場合、クライアントは切断され、"Timed out "例外が発生する。<br>NotchianサーバーはキープアライブIDの値を生成するのに、ミリ秒単位のシステム依存の時間を使用します。
    - 実験結果
        - 頻繁に送信するとあるが、詳しくは15秒おき。
        - 自分でkeep aliveのパケットを作って送信すると、記録にないIDだと言われ、蹴られる。
        - ping取得に使用 ↑は対策できた 自前でping取得システム作れたわ
        - これからclientのtickカウントするの無理だわ　ごめん
        

- Player ([ToServer](https://wiki.vg/index.php?title=Protocol&oldid=14204#Player))
    - 説明 : このパケットと、Player Position、Player Look、Player Position And Lookは、「サーバーバウンド移動パケット」と呼ばれます。バニラクライアントは、プレイヤーが静止している場合でも、20ティックに1回Player Positionを送信します。<br>このパケットは、プレイヤーが地上にいるか（歩いているか/泳いでいるか）、空中にいるか（ジャンプしているか/落下しているか）を示すために使用されます。<br>このステートを含む移動関連のパケットがいくつかあることに注意。
    - 実験結果
        - 移動中は 毎tick パケット送ってくれる いい子
        - 止まっている時は止まってから1秒おきに送信される
        - 動いていても移送距離が非常に短い時はパケットを送信しない -> まあ、TAには関係ないか

- Player Position And Look ([ToServer](https://wiki.vg/index.php?title=Protocol&oldid=14204#Player_Position_And_Look_.28serverbound.29)) ([ToClient](https://wiki.vg/index.php?title=Protocol&oldid=14204#Player_Position_And_Look_.28clientbound.29))
    - 説明 (ToServer) : サーバー上のプレイヤーのXYZ位置を更新します。
    - 説明 (ToClient) : サーバー上のプレイヤーの位置を更新します。このパケットは、参加/再ポーン時の「地形のダウンロード」画面も閉じます。<br>このパケットによって設定された新しい位置と、サーバー上のプレイヤーの最後の位置との間の距離が100mを超えると、「You moved too quickly :( (Hacking?) 」としてクライアントがキックされます。
 

## Event
- PlayerMoveEvent
    - 説明 : BukkitのEvent 同期処理 
    - 実験結果
        - 実行タイミングがtickと同期してるから 正確なpacket取得時間 取得できなかった
        - 1tickに一回しか実行されるわけではなく、1tick以内に受け取ったpackeが複数あれば 複数回実行される
        - 止まっている状態でpacketを受け取っても 実行されない
