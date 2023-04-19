package bot.command.util.read

import bot.Bot
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.sound.sampled.AudioSystem

fun makeAudio(text: String, speakerId: Int) {
    val audioQueryUrl = URL("${Bot.audioEngineHost}/audio_query?speaker=${speakerId}")
    val audioQueryConnection = audioQueryUrl.openConnection() as HttpURLConnection
    audioQueryConnection.requestMethod = "POST"
    audioQueryConnection.doOutput = true
    audioQueryConnection.doInput = true
    audioQueryConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

    val textData = URLEncoder.encode(text, "UTF-8")
    val postData = "text=$textData".toByteArray(Charsets.UTF_8)

    audioQueryConnection.outputStream.write(postData)

    val queryJson = audioQueryConnection.inputStream.bufferedReader().use { it.readText() }

    val synthesisUrl = URL("http://127.0.0.1:50021/synthesis?speaker=${speakerId}")
    val synthesisConnection = synthesisUrl.openConnection() as HttpURLConnection
    synthesisConnection.requestMethod = "POST"
    synthesisConnection.doOutput = true
    synthesisConnection.doInput = true
    synthesisConnection.setRequestProperty("Content-Type", "application/json")

    synthesisConnection.outputStream.bufferedWriter().use { it.write(queryJson) }

    val audioFile = File("audio.wav")
    audioFile.writeBytes(synthesisConnection.inputStream.readBytes())
    
}

