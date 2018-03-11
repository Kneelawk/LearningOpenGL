package org.kneelawk.learningopengl

import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL30._
import org.lwjgl.opengl.GL31._
import org.lwjgl.opengl.ARBInvalidateSubdata._
import org.lwjgl.BufferUtils
import java.nio.ByteBuffer

class GLArrayBuffer(initialAllocation: Long) {
  /**
   * Array of buffer names.
   * Setup: [ <default-buffer>, <temp-copy-buffer> ]
   */
  private val buffers = Array[Int](2)

  /**
   * Current length of the buffer and offset for new data.
   */
  private var size: Long = 0

  /**
   * Current length of allocated space for the buffer.
   */
  private var maxSize: Long = initialAllocation

  /**
   * Constructs a GLArrayBuffer with an initial allocation of 1024 bytes.
   */
  def this() = this(1024)

  glGenBuffers(buffers)
  glBindBuffer(GL_ARRAY_BUFFER, buffers(0))
  glBufferData(GL_ARRAY_BUFFER, maxSize, GL_STATIC_DRAW)

  /**
   * This buffer's id.
   */
  def getId = buffers(0)

  /**
   * The current length of this buffer.
   */
  def getSize = size

  /**
   * Set data within this buffer.
   * This can extend the length of this buffer if offset + buf.remaining() > getSize.
   */
  def set(offset: Long, buf: ByteBuffer) {
    extend(offset, buf.remaining())
    glBindBuffer(GL_ARRAY_BUFFER, buffers(0))
    glBufferSubData(GL_ARRAY_BUFFER, offset, buf)
  }

  /**
   * Append data to the end of this buffer.
   */
  def append(buf: ByteBuffer) {
    extend(buf.remaining())
    glBindBuffer(GL_ARRAY_BUFFER, buffers(0))
    glBufferSubData(GL_ARRAY_BUFFER, size, buf)
  }

  /**
   * Doubles the length of allocated space for this buffer
   */
  private def resize() {
    // bind default buffer to GL_COPY_WRITE_BUFFER
    glBindBuffer(GL_COPY_WRITE_BUFFER, buffers(0))
    // bind temp buffer to GL_COPY_READ_BUFFER
    glBindBuffer(GL_COPY_READ_BUFFER, buffers(1))
    // allocate data for the temp buffer but only as much
    // as there is data in the default buffer
    glBufferData(GL_COPY_READ_BUFFER, size, GL_STATIC_DRAW)

    // copy the data from the default buffer to the temp buffer
    glCopyBufferSubData(GL_COPY_WRITE_BUFFER, GL_COPY_READ_BUFFER, 0, 0, size)

    maxSize *= 2

    // allocate new space for the default buffer with the new maxSize
    glBufferData(GL_COPY_WRITE_BUFFER, maxSize, GL_STATIC_DRAW)
    // copy the old data back from the temp buffer
    glCopyBufferSubData(GL_COPY_READ_BUFFER, GL_COPY_WRITE_BUFFER, 0, 0, size)

    // free the temp buffer's allocated space
    glInvalidateBufferData(buffers(1))
  }

  /**
   * Extends the length of this buffer and resizes if needed.
   */
  private def extend(extraSize: Long) {
    if (extraSize + size > maxSize) {
      resize()
    }

    size += extraSize
  }

  /**
   * Extends the length of this buffer if needed and resizes if needed.
   */
  private def extend(offset: Long, extraSize: Long) {
    if (extraSize + offset > maxSize) {
      resize()
    }

    if (extraSize + offset > size) {
      size = extraSize + offset
    }
  }
}
