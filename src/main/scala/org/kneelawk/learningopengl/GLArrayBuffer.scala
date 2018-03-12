package org.kneelawk.learningopengl

import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL30._
import org.lwjgl.opengl.GL31._
import org.lwjgl.opengl.ARBInvalidateSubdata._
import java.nio.ByteBuffer

class GLArrayBuffer(initialAllocation: Long) {
  private val bufArray = new Array[Int](2)
  glGenBuffers(bufArray)

  /**
   * Default buffer in which all the data is kept.
   */
  private val defaultBuf = bufArray(0)

  /**
   * Temporary buffer used for copy operations.
   */
  private val tempBuf = bufArray(1)

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

  glBindBuffer(GL_ARRAY_BUFFER, defaultBuf)
  glBufferData(GL_ARRAY_BUFFER, maxSize, GL_STATIC_DRAW)

  /**
   * This buffer's id.
   */
  def getId = defaultBuf

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
    glBindBuffer(GL_ARRAY_BUFFER, defaultBuf)
    glBufferSubData(GL_ARRAY_BUFFER, offset, buf)
  }

  /**
   * Append data to the end of this buffer.
   */
  def append(buf: ByteBuffer) {
    extend(buf.remaining())
    glBindBuffer(GL_ARRAY_BUFFER, defaultBuf)
    glBufferSubData(GL_ARRAY_BUFFER, size, buf)
  }

  /**
   * Inserts a chunk of data into this buffer at offset, moving the data currently after
   * offset to the end of where this chunk is inserted.
   */
  def insert(offset: Long, buf: ByteBuffer) {
    if (offset > size) {
      throw new IndexOutOfBoundsException(s"Cannot insert a buffer at $offset (beyond size $size)")
    } else if (offset == size) {
      append(buf)
    } else {
      val bufLen = buf.remaining()
      copyChunk(offset, offset + bufLen, size - offset)
      glBindBuffer(GL_ARRAY_BUFFER, defaultBuf)
      glBufferSubData(GL_ARRAY_BUFFER, offset, buf)
    }
  }

  /**
   * Replaces the chunk at offset of size chunkLen with the data in buf.
   */
  def replace(offset: Long, chunkLen: Long, buf: ByteBuffer) {
    if (offset > size)
      throw new IndexOutOfBoundsException(s"Cannot replace a chunk at $offset (beyond size $size)")

    val bufLen = buf.remaining()
    val lenDif = bufLen - chunkLen

    if (offset + chunkLen < size) {
      if (lenDif != 0) {
        copyChunk(offset + chunkLen, offset + bufLen, size - (offset + chunkLen))
      }

      glBindBuffer(GL_ARRAY_BUFFER, defaultBuf)
      glBufferSubData(GL_ARRAY_BUFFER, offset, buf)

      size += lenDif
    } else {
      extend(offset, bufLen)
      glBindBuffer(GL_ARRAY_BUFFER, defaultBuf)
      glBufferSubData(GL_ARRAY_BUFFER, offset, buf)

      size = offset + bufLen
    }
  }

  /**
   * Replaces everything at and after offset with the data in buf.
   */
  def replaceAfter(offset: Long, buf: ByteBuffer) {
    if (offset > size)
      throw new IndexOutOfBoundsException(s"Cannot replace a chunk at $offset (beyond size $size)")

    val bufLen = buf.remaining()
    extend(offset, bufLen)
    glBindBuffer(GL_ARRAY_BUFFER, defaultBuf)
    glBufferSubData(GL_ARRAY_BUFFER, offset, buf)

    size = offset + bufLen
  }

  /**
   * Replaces everything before cutoff with the data in buf.
   */
  def replaceBefore(cutoff: Long, buf: ByteBuffer) {
    if (cutoff > size)
      throw new IndexOutOfBoundsException(s"Cannot replace everything before $cutoff (beyond size $size)")

    val bufLen = buf.remaining()
    val lenDif = bufLen - cutoff

    if (lenDif != 0) {
      copyChunk(cutoff, bufLen, size - cutoff)
    }

    glBindBuffer(GL_ARRAY_BUFFER, defaultBuf)
    glBufferSubData(GL_ARRAY_BUFFER, 0, buf)

    size += lenDif
  }

  /**
   * Removes the chunk at offset of size chunkLen from this buffer.
   */
  def remove(offset: Long, chunkLen: Long) {
    if (offset > size)
      throw new IndexOutOfBoundsException(s"Cannot remove a chunk at $offset (beyond size $size)")

    if (offset + chunkLen < size) {
      copyChunk(offset + chunkLen, offset, size - (offset + chunkLen))

      size -= chunkLen
    } else {
      size = offset
    }
  }

  /**
   * Removes everything at and after cutoff from this buffer.
   */
  def removeAfter(cutoff: Long) {
    if (cutoff > size)
      throw new IndexOutOfBoundsException(s"Cannot remove chunk at $cutoff (beyond size $size)")

    size = cutoff
  }

  /**
   * Removes everything before cutoff from this buffer.
   */
  def removeBefore(cutoff: Long) {
    if (cutoff > size)
      throw new IndexOutOfBoundsException(s"Cannot remove everything before $cutoff (beyond size $size)")

    copyChunk(cutoff, 0, size - cutoff)

    size -= cutoff
  }

  /**
   * Sets the buffers current size to 0, effectively clearing it.
   */
  def clear() {
    size = 0
  }

  /**
   * Copies a chunk of data around within the default buffer, resizing if necessary.
   */
  private def copyChunk(sourceOffset: Long, destOffset: Long, chunkLen: Long) {
    // where is the end of the destination chunk?
    val chunkEnd = destOffset + chunkLen

    // bind default buffer to GL_COPY_WRITE_BUFFER
    glBindBuffer(GL_COPY_WRITE_BUFFER, defaultBuf)
    // bind temp buffer to GL_COPY_READ_BUFFER
    glBindBuffer(GL_COPY_READ_BUFFER, tempBuf)
    // allocate data for the temp buffer
    glBufferData(GL_COPY_READ_BUFFER, size, GL_STATIC_DRAW)

    // copy the data from the default buffer to the temp buffer
    glCopyBufferSubData(GL_COPY_WRITE_BUFFER, GL_COPY_READ_BUFFER, 0, 0, size)

    // if a resize is needed then resize and copy all the data back from the temp buffer
    if (chunkEnd > maxSize) {
      maxSize *= 2

      // allocate new space for the default buffer
      glBufferData(GL_COPY_WRITE_BUFFER, maxSize, GL_STATIC_DRAW)
      // copy the old data back from the temp buffer
      glCopyBufferSubData(GL_COPY_READ_BUFFER, GL_COPY_WRITE_BUFFER, 0, 0, size)
    }

    // copy the chunk from the source location in the temp buffer to the dest location in the default buffer
    glCopyBufferSubData(GL_COPY_READ_BUFFER, GL_COPY_WRITE_BUFFER, sourceOffset, destOffset, chunkLen)

    // increase the current length if necessary
    if (chunkEnd > size) {
      size = chunkEnd
    }

    // free the temp buffer's allocated space
    glInvalidateBufferData(tempBuf)
  }

  /**
   * Doubles the length of allocated space for this buffer.
   */
  private def resize() {
    // bind default buffer to GL_COPY_WRITE_BUFFER
    glBindBuffer(GL_COPY_WRITE_BUFFER, defaultBuf)
    // bind temp buffer to GL_COPY_READ_BUFFER
    glBindBuffer(GL_COPY_READ_BUFFER, tempBuf)
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
    glInvalidateBufferData(tempBuf)
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
