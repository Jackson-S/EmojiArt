package emoji

import types.Pixel
import java.awt.Font
import java.awt.image.BufferedImage
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.system.exitProcess

class EmojiColour {
    val osName = System.getProperty("os.name").lowercase()
    val fontName = when {
        osName.contains("win") -> "Segoe UI Emoji"
        osName.contains("mac") -> "Apple Color Emoji"
        else -> {
            println("Unknown OS or Linux. Sorry, you're on your own. ($osName)")
            exitProcess(1)
        }
    }

    private val emojiInfo: List<EmojiColorData> = emojiList.map { emoji ->
        val bufferedImage = BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB)
        val graphics = bufferedImage.graphics
        graphics.font = Font(fontName, Font.PLAIN, 32)
        graphics.drawString(emoji, 0, 28)
        val array = DoubleArray(32 * 32 * 4)
        bufferedImage.flush()
        bufferedImage.data.getPixels(0, 0, 32, 32, array)
        var transparency = 0.0
        var red = 0.0
        var green = 0.0
        var blue = 0.0
        array.forEachIndexed { index, d ->
            when (index % 4) {
                0 -> red += d
                1 -> green += d
                2 -> blue += d
                3 -> transparency += d
            }
        }

        val divisor = bufferedImage.width * bufferedImage.height
        EmojiColorData(emoji, Triple(red / divisor, green / divisor, blue / divisor), transparency / divisor)
    }

    private data class EmojiColorData(
        val emoji: String,
        val color: Triple<Double, Double, Double>,
        val transparency: Double
    )

    fun getNearestEmoji(pixel: Pixel): String {
        val map = emojiInfo.map {
            val alphaDelta = (it.transparency - pixel.a).pow(2)
            val redDelta = (it.color.first - pixel.r).pow(2)
            val greenDelta = (it.color.second - pixel.g).pow(2)
            val blueDelta = (it.color.third - pixel.b).pow(2)
            Pair(sqrt(redDelta + greenDelta + blueDelta + alphaDelta), it)
        }

        return map.minBy {
            it.first
        }.second.emoji
    }
}
