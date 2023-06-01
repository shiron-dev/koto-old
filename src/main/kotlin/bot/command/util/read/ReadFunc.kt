package bot.command.util.read

import bot.Bot
import com.sedmelluq.discord.lavaplayer.container.wav.WavAudioTrack
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo
import net.dv8tion.jda.api.entities.Message
import java.io.ByteArrayInputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream


fun vcRead(message: Message) {
    val audioManager = message.guild.audioManager
    if (audioManager.isConnected) {
        val speakString = messageToSpeak(message)
        val audioBytes = makeAudio(speakString)

        val audioFormat = AudioFormat(24000f, 16, 1, true, false)
        val byteArrayInputStream = ByteArrayInputStream(audioBytes)
        val audioInputStream =
            AudioInputStream(byteArrayInputStream, audioFormat, (audioBytes.size / audioFormat.frameSize).toLong())

        val playerManager: AudioPlayerManager = DefaultAudioPlayerManager()
        AudioSourceManagers.registerRemoteSources(playerManager)
        AudioSourceManagers.registerLocalSource(playerManager)
        val player: AudioPlayer = playerManager.createPlayer()
        val track = WavAudioTrack(
            AudioTrackInfo(
                "Title",
                "Author",
                (audioBytes.size / audioFormat.frameSize).toLong(),
                "identifier",
                true,
                ""
            ), InputStreamSeekableInputStream(audioInputStream)
        )
        player.playTrack(track)

        audioManager.sendingHandler = VCReadAudioHandler(player)
    }
}

fun messageToSpeak(message: Message): String {
    val urlReg = Regex("""(https?|ftp)://[^\s/$.?#].\S*""")
    val fileStr = if (message.attachments.size <= 5)
        "${if (message.attachments.isNotEmpty()) "。添付されたファイル。" else ""}${
            message.attachments.joinToString("。") {
                it.contentType?.let { it1 ->
                    convertMediaTypeToJapaneseName(
                        it1
                    )
                } ?: "不明なファイル"
            }
        }" else "。添付されたファイルがあります。"
    println(fileStr)
    return "${urlReg.replace(message.contentRaw, "(URL)")}$fileStr"
}

fun convertMediaTypeToJapaneseName(mediaType: String): String? {
    val (type, subtype) = mediaType.split("/")
    return when (type) {
        "text" -> "テキスト"
        "image" -> "画像"
        "audio" -> "音声"
        "video" -> "動画"
        "model" -> "3D"
        "application" -> {
            when (subtype) {
                "msword", "vnd.openxmlformats-officedocument.wordprocessingml.document" -> "ワード"
                "vnd.ms-excel", "vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> "エクセル"
                "vnd.ms-powerpoint", "vnd.openxmlformats-officedocument.presentationml.presentation" -> "パワーポイント"
                "vnd.oasis.opendocument.text" -> "オープンドキュメント"
                else -> subtype
            }
        }

        else -> null
    }
}

fun makeAudio(text: String, speakerId: Int = 1): ByteArray {
    val audioQueryUrl =
        URL("http://${Bot.audioEngineHost}/audio_query?speaker=${speakerId}&text=${URLEncoder.encode(text, "UTF-8")}")
    val audioQueryConnection = audioQueryUrl.openConnection() as HttpURLConnection
    audioQueryConnection.requestMethod = "POST"
    audioQueryConnection.doInput = true
    audioQueryConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
    audioQueryConnection.connect()

    val queryJson = audioQueryConnection.inputStream.bufferedReader().use { it.readText() }
        .replace(""""speedScale":[+-]?\d+(?:\.\d+)?""".toRegex(), """"speedScale": 1.4""")

    val synthesisUrl = URL("http://${Bot.audioEngineHost}/synthesis?speaker=${speakerId}")
    val synthesisConnection = synthesisUrl.openConnection() as HttpURLConnection
    synthesisConnection.requestMethod = "POST"
    synthesisConnection.doOutput = true
    synthesisConnection.doInput = true
    synthesisConnection.setRequestProperty("Content-Type", "application/json")
    synthesisConnection.setChunkedStreamingMode(0)

    synthesisConnection.outputStream.bufferedWriter().use { it.write(queryJson) }
    return synthesisConnection.inputStream.readBytes()
}
