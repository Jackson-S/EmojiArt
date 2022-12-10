package image

import types.Pixel
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import java.nio.file.Path
import javax.imageio.ImageIO

class ImageInput(path: Path, width: Int = 32) {

    private val image: BufferedImage
    private val pixelData: List<Pixel>

    init {
        val inputImage: BufferedImage = ImageIO.read(path.toFile())
        val heightScale = 1 / (inputImage.width.toDouble() / inputImage.height.toDouble())
        val after = BufferedImage(width, (heightScale * width).toInt(), BufferedImage.TYPE_INT_ARGB)
        val at = AffineTransform()
        at.scale(1 / (inputImage.width / width.toDouble()), 1 / (inputImage.width / width.toDouble()))
        val scaleOp = AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC)
        image = scaleOp.filter(inputImage, after)
        val pixels = (image.raster.dataBuffer as DataBufferInt).data

        pixelData = pixels.map {
            Pixel(
                a = ((it shr 24) and 0xff).toDouble(),
                r = ((it shr 16) and 0xff).toDouble(),
                g = ((it shr 8) and 0xff).toDouble(),
                b = (it and 0xff).toDouble()
            )
        }
    }

    fun getPixel(x: Int, y: Int) = pixelData[y * image.width + x]

    fun getWidth() = image.width

    fun getHeight() = image.height
}
