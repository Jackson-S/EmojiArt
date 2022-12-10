import emoji.EmojiColour
import image.ImageInput
import java.io.File
import kotlin.io.path.Path
import kotlin.system.exitProcess

const val IMAGE_WIDTH: Int = 100

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("./gradlew run in.png [out.txt]")
        exitProcess(1)
    }

    val emojiList = EmojiColour()
    val inputImage = ImageInput(Path(args[0]), IMAGE_WIDTH)

    val result = (0 until inputImage.getHeight()).map { y ->
        (0 until inputImage.getWidth()).map { x ->
            emojiList.getNearestEmoji(inputImage.getPixel(x, y))
        }.joinToString(separator = "") { it }
    }.joinToString(separator = "\n") { it }

    if (args.size > 1) {
        File(args[1]).writeText(result)
    } else {
        println(result)
    }
}
