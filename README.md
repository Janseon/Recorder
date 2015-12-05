# Recorder
实时录音，speex编码，实时通过udp转发到另一端； 另一端实时接收，实时通过speex解码，然后实时进行播放

服务器的代码路径：Recorder\app\src\server

<p></p>
android实时语音demo

1. 支持两个客户端互相对话；
2. 实现的功能
  * 客户端录制并且编码；
  * 再通过udp传到服务端；
  * 服务端通过udp转发到另外的客户端；
  * 客户端通过udp接收数据，并进行解码和播放。
3. 在客户端上暂时只支持1、和2两个id，所以在编辑框中只能填入1或2这两个id；

<p></p>
![img](/screenshots/Screenshot_20151205-143503.png)
