package org.kneelawk.learningopengl.textures

import java.awt.image.BufferedImage
import java.io.{File, InputStream}
import java.net.URL

import javax.imageio.ImageIO
import org.kneelawk.learningopengl.util.TryUtil
import org.kneelawk.learningopengl.util.TryUtil.tryWithDestroyer
import org.lwjgl.system.MemoryUtil
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL12.GL_BGRA
import org.lwjgl.opengl.GL30.glGenerateMipmap

class GLTextureSource(image: BufferedImage) {
  def this(url: URL) = this(ImageIO.read(url))

  def this(file: File) = this(ImageIO.read(file))

  def this(is: InputStream) = this(ImageIO.read(is))

  def build(): GLTexture = {
    val width = image.getWidth()
    val height = image.getHeight()
    val dataLen = height * width

    // image data is width * height pixels with each pixel being 32 bits or an Int (8 bits per color for RGBA)
    val data = new Array[Int](dataLen)

    // load image data into the array
    image.getRGB(0, 0, width, height, data, 0, width)

    // generate the texture
    val id = glGenTextures()
    glBindTexture(GL_TEXTURE_2D, id)

    tryWithDestroyer(MemoryUtil.memAllocInt(dataLen)) { buf =>
      for (y <- (0 until height).reverse) {
        buf.put(data, y * width, width)
      }
      buf.flip()

      glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_BGRA, GL_UNSIGNED_BYTE, buf)
    }(MemoryUtil.memFree)

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
    glGenerateMipmap(GL_TEXTURE_2D)

    GLTexture(id)
  }
}

case class GLTexture(id: Int) {
  private var deleted = false

  def delete(): Unit = if (!deleted) {
    glDeleteTextures(id)
    deleted = true
  }

  override protected def finalize(): Unit = delete()
}
