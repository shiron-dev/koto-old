package bot.command.util.read

import com.sedmelluq.discord.lavaplayer.source.local.LocalSeekableInputStream
import com.sedmelluq.discord.lavaplayer.tools.io.ExtendedBufferedInputStream
import com.sedmelluq.discord.lavaplayer.tools.io.SeekableInputStream
import com.sedmelluq.discord.lavaplayer.track.info.AudioTrackInfoProvider
import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.InputStream
import java.util.*

class InputStreamSeekableInputStream(private val inputStream: InputStream) :
    SeekableInputStream(inputStream.available().toLong(), 0) {
    companion object {
        private val log = LoggerFactory.getLogger(LocalSeekableInputStream::class.java)
    }

    private val bufferedStream = ExtendedBufferedInputStream(inputStream)

    private var position: Long = 0

    override fun read(): Int {
        val result = bufferedStream.read()
        if (result >= 0) {
            position++
        }

        return result
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        val read = bufferedStream.read(b, off, len)
        position += read
        return read
    }

    override fun skip(n: Long): Long {
        val skipped = bufferedStream.skip(n)
        position += skipped
        return skipped
    }

    override fun available(): Int {
        return bufferedStream.available()
    }

    @Throws
    override fun reset() {
        throw IOException("mark/reset not supported")
    }

    override fun markSupported(): Boolean {
        return false
    }

    @Throws(IOException::class)
    override fun close() {
        log.debug("Failed to close channel")
    }

    override fun getPosition(): Long {
        return position
    }

    override fun canSeekHard(): Boolean {
        return true
    }

    override fun getTrackInfoProviders(): MutableList<AudioTrackInfoProvider> {
        return Collections.emptyList()
    }

    @kotlin.jvm.Throws
    override fun seekHard(position: Long) {
        inputStream.mark(position.toInt())
        this.position = position
        bufferedStream.discardBuffer()
    }
}