package club.sk1er.elementa.components.image

import java.awt.image.BufferedImage
import java.lang.IllegalArgumentException
import java.util.concurrent.TimeUnit
import javax.imageio.metadata.IIOMetadataNode
import com.sun.imageio.plugins.png.PNGMetadata
import org.apache.commons.io.FileUtils
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.*
import javax.imageio.ImageIO
import javax.imageio.IIOImage
import javax.imageio.ImageTypeSpecifier
import java.io.ByteArrayInputStream
import java.net.URL
import java.nio.channels.FileChannel


class FileImageCache(
    private val directory: File,
    private val cacheTime: Long,
    private val timeUnit: TimeUnit,
    private val deleteOnCacheMiss: Boolean = true
) : ImageCache {

    init {
        if (!directory.exists()) {
            directory.mkdirs()
        }
        if (!directory.isDirectory) {
            throw IllegalArgumentException("Directory ${directory.absolutePath} is not a valid directory for a FileImageCache")
        }
    }

    override operator fun get(url: URL): BufferedImage? {
        val hashCode = url.hashCode()
        var index = 0
        FileChannel.open(File(directory, "$hashCode-$index.png").toPath()).use { open ->
            val lock = open.lock();
            while (true) {
                val file = File(directory, "$hashCode-$index.png")
                if (!file.exists()) {
                    lock.release()
                    return null
                }
                val cacheItem = readEntry(file)
                if (cacheItem.second == url.toString()) {
                    if (System.currentTimeMillis() - cacheItem.third < timeUnit.toMillis(cacheTime)) {
                        lock.release()
                        return cacheItem.first
                    } else if (deleteOnCacheMiss) {
                        file.delete()
                        moveDown(hashCode, index)
                    }
                }
                index++
            }
        }
        return null
    }

    override operator fun set(url: URL, image: BufferedImage) {
        val hashCode = url.hashCode()
        var index = 0
        while (true) {
            val file = File(directory, "$hashCode-$index.png")
            if (!file.exists()) {
                writeEntry(file, image, url)
                return
            }
            index++
        }

    }

    private fun moveDown(hashCode: Int, index: Int) {
        var tmp = index + 1
        while (true) {
            val src = File(directory, "$hashCode-$tmp.png")
            if (src.exists()) {
                src.renameTo(File(directory, "$hashCode-${tmp - 1}.png"))
            } else return
            tmp++
        }
    }


    private fun writeEntry(file: File, img: BufferedImage, url: URL) {
        val writer = ImageIO.getImageWritersByFormatName("png").next()

        val writeParam = writer.defaultWriteParam
        val typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB)
        val metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam)

        val urlEntry = IIOMetadataNode("tEXtEntry")
        urlEntry.setAttribute("keyword", "image_url")
        urlEntry.setAttribute("value", url.toString())

        val cacheTimeEntry = IIOMetadataNode("tEXtEntry")
        cacheTimeEntry.setAttribute("keyword", "cache_time")
        cacheTimeEntry.setAttribute("value", System.currentTimeMillis().toString())

        val text = IIOMetadataNode("tEXt")
        text.appendChild(urlEntry)
        text.appendChild(cacheTimeEntry)

        val root = IIOMetadataNode("javax_imageio_png_1.0")
        root.appendChild(text)
        metadata.mergeTree("javax_imageio_png_1.0", root)

        val fos = FileOutputStream(file)
        val stream = ImageIO.createImageOutputStream(fos)
        writer.output = stream
        writer.write(metadata, IIOImage(img, null, metadata), writeParam)
        stream.close()
        fos.close()
    }

    private fun readEntry(file: File): Triple<BufferedImage, String, Long> {
        val imageReader = ImageIO.getImageReadersByFormatName("png").next()
        val imageData = FileUtils.readFileToByteArray(file)
        imageReader.setInput(ImageIO.createImageInputStream(ByteArrayInputStream(imageData)), true)


        val metadata = imageReader.getImageMetadata(0) as PNGMetadata
        val childNodes: NodeList = metadata.standardTextNode.childNodes
        var cacheUrl = ""
        var timeCached = 0L
        for (i in 0 until childNodes.length) {
            val node: Node = childNodes.item(i)
            val keyword: String = node.attributes.getNamedItem("keyword").nodeValue
            val value: String = node.attributes.getNamedItem("value").nodeValue
            if ("image_url" == keyword) {
                cacheUrl = value
            } else if ("cache_time" == keyword) {
                timeCached = value.toLong()
            }
        }
        return Triple(ImageIO.read(ByteArrayInputStream(imageData)), cacheUrl, timeCached)
    }

}

