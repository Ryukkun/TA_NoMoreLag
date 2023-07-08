# TA_NoMoreLag


## Packet
- Keep Alive ([ToServer](https://wiki.vg/index.php?title=Protocol&oldid=14204#Keep_Alive_.28serverbound.29)) ([ToClient](https://wiki.vg/index.php?title=Protocol&oldid=14204#Keep_Alive_.28clientbound.29))
    - 説明 : サーバーは、ランダムなIDを含むキープアライブを頻繁に送信する。クライアントは同じパケットで応答しなければならない。クライアントが30秒以上応答しない場合、サーバーはクライアントをキックする。逆に、サーバーが20秒間キープアライブを送信しなかった場合、クライアントは切断され、"Timed out "例外が発生する。<br>NotchianサーバーはキープアライブIDの値を生成するのに、ミリ秒単位のシステム依存の時間を使用します。
    - 疑問
      - [-] 頻繁に送信するとあるが どの程度なのかがわからない。 A. 15秒おき
    - だけどこれからclientのtickカウントするの無理だわ　ごめん

- Player ([ToServer](https://wiki.vg/index.php?title=Protocol&oldid=14204#Player))
  - 説明 : このパケットと、Player Position、Player Look、Player Position And Lookは、「サーバーバウンド移動パケット」と呼ばれます。バニラクライアントは、プレイヤーが静止している場合でも、20ティックに1回Player Positionを送信します。<br>このパケットは、プレイヤーが地上にいるか（歩いているか/泳いでいるか）、空中にいるか（ジャンプしているか/落下しているか）を示すために使用されます。<br>このステートを含む移動関連のパケットがいくつかあることに注意。
  - 実験結果
      - 移動中は 毎tick パケット送ってくれる いい子
      - 止まっている時は止まってから1秒おきに送信される
      - 動いていても移送距離が非常に短い時はパケットを送信しない -> まあ、TAには関係ないか

- Player Position And Look ([ToServer](https://wiki.vg/index.php?title=Protocol&oldid=14204#Player_Position_And_Look_.28serverbound.29)) ([ToClient](https://wiki.vg/index.php?title=Protocol&oldid=14204#Player_Position_And_Look_.28clientbound.29))
  - 説明 (ToServer) : サーバー上のプレイヤーのXYZ位置を更新します。
  - 説明 (ToClient) : サーバー上のプレイヤーの位置を更新します。このパケットは、参加/再ポーン時の「地形のダウンロード」画面も閉じます。<br>このパケットによって設定された新しい位置と、サーバー上のプレイヤーの最後の位置との間の距離が100mを超えると、「You moved too quickly :( (Hacking?) 」としてクライアントがキックされます。
