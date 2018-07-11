package org.kneelawk.learningopengl

import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL30._
import org.lwjgl.opengl.GL31._
import org.lwjgl.opengl.ARBInvalidateSubdata._
import java.nio.ByteBuffer
import org.lwjgl.system.MemoryUtil
import java.nio.ShortBuffer
import java.nio.IntBuffer
import java.nio.LongBuffer
import java.nio.DoubleBuffer
import java.nio.FloatBuffer

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
  glBufferData(GL_ARRAY_BUFFER, maxSize, GL_DYNAMIC_DRAW)

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
   * ByteBuffer version.
   */
  def set(offset: Long, buf: ByteBuffer) {
    setNative(offset, buf.remaining(), MemoryUtil.memAddress(buf))
  }

  /**
   * Set data within this buffer.
   * This can extend the length of this buffer if offset + buf.remaining() > getSize.
   * ShortBuffer version.
   */
  def set(offset: Long, buf: ShortBuffer) {
    setNative(offset, buf.remaining() << 1, MemoryUtil.memAddress(buf))
  }

  /**
   * Set data within this buffer.
   * This can extend the length of this buffer if offset + buf.remaining() > getSize.
   * IntBuffer version.
   */
  def set(offset: Long, buf: IntBuffer) {
    setNative(offset, buf.remaining() << 2, MemoryUtil.memAddress(buf))
  }

  /**
   * Set data within this buffer.
   * This can extend the length of this buffer if offset + buf.remaining() > getSize.
   * LongBuffer version.
   */
  def set(offset: Long, buf: LongBuffer) {
    setNative(offset, buf.remaining() << 3, MemoryUtil.memAddress(buf))
  }

  /**
   * Set data within this buffer.
   * This can extend the length of this buffer if offset + buf.remaining() > getSize.
   * FloatBuffer version.
   */
  def set(offset: Long, buf: FloatBuffer) {
    setNative(offset, buf.remaining() << 2, MemoryUtil.memAddress(buf))
  }

  /**
   * Set data within this buffer.
   * This can extend the length of this buffer if offset + buf.remaining() > getSize.
   * DoubleBuffer version.
   */
  def set(offset: Long, buf: DoubleBuffer) {
    setNative(offset, buf.remaining() << 3, MemoryUtil.memAddress(buf))
  }

  def setNative(offset: Long, len: Long, data: Long) {
    extend(offset, len)
    glBindBuffer(GL_ARRAY_BUFFER, defaultBuf)
    nglBufferSubData(GL_ARRAY_BUFFER, offset, len, data)
  }

  /**
   * Append data to the end of this buffer.
   * ByteBuffer version.
   */
  def append(buf: ByteBuffer) {
    appendNative(buf.remaining(), MemoryUtil.memAddress(buf))
  }

  /**
   * Append data to the end of this buffer.
   * ShortBuffer version.
   */
  def append(buf: ShortBuffer) {
    appendNative(buf.remaining() << 1, MemoryUtil.memAddress(buf))
  }

  /**
   * Append data to the end of this buffer.
   * IntBuffer version.
   */
  def append(buf: IntBuffer) {
    appendNative(buf.remaining() << 2, MemoryUtil.memAddress(buf))
  }

  /**
   * Append data to the end of this buffer.
   * LongBuffer version.
   */
  def append(buf: LongBuffer) {
    appendNative(buf.remaining() << 3, MemoryUtil.memAddress(buf))
  }

  /**
   * Append data to the end of this buffer.
   * FloatBuffer version.
   */
  def append(buf: FloatBuffer) {
    appendNative(buf.remaining() << 2, MemoryUtil.memAddress(buf))
  }

  /**
   * Append data to the end of this buffer.
   * DoubleBuffer version.
   */
  def append(buf: DoubleBuffer) {
    appendNative(buf.remaining() << 3, MemoryUtil.memAddress(buf))
  }

  def appendNative(len: Long, data: Long) {
    val offset = size
    extend(len)
    glBindBuffer(GL_ARRAY_BUFFER, defaultBuf)
    nglBufferSubData(GL_ARRAY_BUFFER, offset, len, data)
  }

  /**
   * Inserts a chunk of data into this buffer at offset, moving the data currently after
   * offset to the end of where this chunk is inserted.
   * ByteBuffer version.
   */
  def insert(offset: Long, buf: ByteBuffer) {
    insertNative(offset, buf.remaining(), MemoryUtil.memAddress(buf))
  }

  /**
   * Inserts a chunk of data into this buffer at offset, moving the data currently after
   * offset to the end of where this chunk is inserted.
   * ShortBuffer version.
   */
  def insert(offset: Long, buf: ShortBuffer) {
    insertNative(offset, buf.remaining() << 1, MemoryUtil.memAddress(buf))
  }

  /**
   * Inserts a chunk of data into this buffer at offset, moving the data currently after
   * offset to the end of where this chunk is inserted.
   * IntBuffer version.
   */
  def insert(offset: Long, buf: IntBuffer) {
    insertNative(offset, buf.remaining() << 2, MemoryUtil.memAddress(buf))
  }

  /**
   * Inserts a chunk of data into this buffer at offset, moving the data currently after
   * offset to the end of where this chunk is inserted.
   * LongBuffer version.
   */
  def insert(offset: Long, buf: LongBuffer) {
    insertNative(offset, buf.remaining() << 3, MemoryUtil.memAddress(buf))
  }

  /**
   * Inserts a chunk of data into this buffer at offset, moving the data currently after
   * offset to the end of where this chunk is inserted.
   * FloatBuffer version.
   */
  def insert(offset: Long, buf: FloatBuffer) {
    insertNative(offset, buf.remaining() << 2, MemoryUtil.memAddress(buf))
  }

  /**
   * Inserts a chunk of data into this buffer at offset, moving the data currently after
   * offset to the end of where this chunk is inserted.
   * DoubleBuffer version.
   */
  def insert(offset: Long, buf: DoubleBuffer) {
    insertNative(offset, buf.remaining() << 3, MemoryUtil.memAddress(buf))
  }

  def insertNative(offset: Long, len: Long, data: Long) {
    if (offset > size) {
      throw new IndexOutOfBoundsException(s"Cannot insert a buffer at $offset (beyond size $size)")
    } else if (offset == size) {
      appendNative(len, data)
    } else {
      val bufLen = len
      copyChunk(offset, offset + bufLen, size - offset)
      glBindBuffer(GL_ARRAY_BUFFER, defaultBuf)
      nglBufferSubData(GL_ARRAY_BUFFER, offset, len, data)
    }
  }

  /**
   * Replaces the chunk at offset of size chunkLen with the data in buf.
   * ByteBuffer version.
   */
  def replace(offset: Long, chunkLen: Long, buf: ByteBuffer) {
    replaceNative(offset, chunkLen, buf.remaining(), MemoryUtil.memAddress(buf))
  }

  /**
   * Replaces the chunk at offset of size chunkLen with the data in buf.
   * ShortBuffer version.
   */
  def replace(offset: Long, chunkLen: Long, buf: ShortBuffer) {
    replaceNative(offset, chunkLen, buf.remaining() << 1, MemoryUtil.memAddress(buf))
  }

  /**
   * Replaces the chunk at offset of size chunkLen with the data in buf.
   * IntBuffer version.
   */
  def replace(offset: Long, chunkLen: Long, buf: IntBuffer) {
    replaceNative(offset, chunkLen, buf.remaining() << 2, MemoryUtil.memAddress(buf))
  }

  /**
   * Replaces the chunk at offset of size chunkLen with the data in buf.
   * LongBuffer version.
   */
  def replace(offset: Long, chunkLen: Long, buf: LongBuffer) {
    replaceNative(offset, chunkLen, buf.remaining() << 3, MemoryUtil.memAddress(buf))
  }

  /**
   * Replaces the chunk at offset of size chunkLen with the data in buf.
   * FloatBuffer version.
   */
  def replace(offset: Long, chunkLen: Long, buf: FloatBuffer) {
    replaceNative(offset, chunkLen, buf.remaining() << 2, MemoryUtil.memAddress(buf))
  }

  /**
   * Replaces the chunk at offset of size chunkLen with the data in buf.
   * DoubleBuffer version.
   */
  def replace(offset: Long, chunkLen: Long, buf: DoubleBuffer) {
    replaceNative(offset, chunkLen, buf.remaining() << 3, MemoryUtil.memAddress(buf))
  }

  def replaceNative(offset: Long, chunkLen: Long, len: Long, data: Long) {
    if (offset > size)
      throw new IndexOutOfBoundsException(s"Cannot replace a chunk at $offset (beyond size $size)")

    val lenDif = len - chunkLen

    if (offset + chunkLen < size) {
      if (lenDif != 0) {
        copyChunk(offset + chunkLen, offset + len, size - (offset + chunkLen))
      }

      glBindBuffer(GL_ARRAY_BUFFER, defaultBuf)
      nglBufferSubData(GL_ARRAY_BUFFER, offset, len, data)

      size += lenDif
    } else {
      extend(offset, len)
      glBindBuffer(GL_ARRAY_BUFFER, defaultBuf)
      nglBufferSubData(GL_ARRAY_BUFFER, offset, len, data)

      size = offset + len
    }
  }

  /**
   * Replaces everything at and after offset with the data in buf.
   * ByteBuffer version.
   */
  def replaceAfter(offset: Long, buf: ByteBuffer) {
    replaceAfterNative(offset, buf.remaining(), MemoryUtil.memAddress(buf))
  }

  /**
   * Replaces everything at and after offset with the data in buf.
   * ShortBuffer version.
   */
  def replaceAfter(offset: Long, buf: ShortBuffer) {
    replaceAfterNative(offset, buf.remaining() << 1, MemoryUtil.memAddress(buf))
  }

  /**
   * Replaces everything at and after offset with the data in buf.
   * IntBuffer version.
   */
  def replaceAfter(offset: Long, buf: IntBuffer) {
    replaceAfterNative(offset, buf.remaining() << 2, MemoryUtil.memAddress(buf))
  }

  /**
   * Replaces everything at and after offset with the data in buf.
   * LongBuffer version.
   */
  def replaceAfter(offset: Long, buf: LongBuffer) {
    replaceAfterNative(offset, buf.remaining() << 3, MemoryUtil.memAddress(buf))
  }

  /**
   * Replaces everything at and after offset with the data in buf.
   * FloatBuffer version.
   */
  def replaceAfter(offset: Long, buf: FloatBuffer) {
    replaceAfterNative(offset, buf.remaining() << 2, MemoryUtil.memAddress(buf))
  }

  /**
   * Replaces everything at and after offset with the data in buf.
   * DoubleBuffer version.
   */
  def replaceAfter(offset: Long, buf: DoubleBuffer) {
    replaceAfterNative(offset, buf.remaining() << 3, MemoryUtil.memAddress(buf))
  }

  def replaceAfterNative(offset: Long, len: Long, data: Long) {
    if (offset > size)
      throw new IndexOutOfBoundsException(s"Cannot replace a chunk at $offset (beyond size $size)")

    extend(offset, len)
    glBindBuffer(GL_ARRAY_BUFFER, defaultBuf)
    nglBufferSubData(GL_ARRAY_BUFFER, offset, len, data)

    size = offset + len
  }

  /**
   * Replaces everything before cutoff with the data in buf.
   * ByteBuffer version.
   */
  def replaceBefore(cutoff: Long, buf: ByteBuffer) {
    replaceBeforeNative(cutoff, buf.remaining(), MemoryUtil.memAddress(buf))
  }

  /**
   * Replaces everything before cutoff with the data in buf.
   * ShortBuffer version.
   */
  def replaceBefore(cutoff: Long, buf: ShortBuffer) {
    replaceBeforeNative(cutoff, buf.remaining() << 1, MemoryUtil.memAddress(buf))
  }

  /**
   * Replaces everything before cutoff with the data in buf.
   * IntBuffer version.
   */
  def replaceBefore(cutoff: Long, buf: IntBuffer) {
    replaceBeforeNative(cutoff, buf.remaining() << 2, MemoryUtil.memAddress(buf))
  }

  /**
   * Replaces everything before cutoff with the data in buf.
   * LongBuffer version.
   */
  def replaceBefore(cutoff: Long, buf: LongBuffer) {
    replaceBeforeNative(cutoff, buf.remaining() << 3, MemoryUtil.memAddress(buf))
  }

  /**
   * Replaces everything before cutoff with the data in buf.
   * FloatBuffer version.
   */
  def replaceBefore(cutoff: Long, buf: FloatBuffer) {
    replaceBeforeNative(cutoff, buf.remaining() << 2, MemoryUtil.memAddress(buf))
  }

  /**
   * Replaces everything before cutoff with the data in buf.
   * DoubleBuffer version.
   */
  def replaceBefore(cutoff: Long, buf: DoubleBuffer) {
    replaceBeforeNative(cutoff, buf.remaining() << 3, MemoryUtil.memAddress(buf))
  }

  def replaceBeforeNative(cutoff: Long, len: Long, data: Long) {
    if (cutoff > size)
      throw new IndexOutOfBoundsException(s"Cannot replace everything before $cutoff (beyond size $size)")

    val lenDif = len - cutoff

    if (lenDif != 0) {
      copyChunk(cutoff, len, size - cutoff)
    }

    glBindBuffer(GL_ARRAY_BUFFER, defaultBuf)
    nglBufferSubData(GL_ARRAY_BUFFER, 0, len, data)

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
    glBufferData(GL_COPY_READ_BUFFER, size, GL_STATIC_COPY)

    // copy the data from the default buffer to the temp buffer
    glCopyBufferSubData(GL_COPY_WRITE_BUFFER, GL_COPY_READ_BUFFER, 0, 0, size)

    // if a resize is needed then resize and copy all the data back from the temp buffer
    if (chunkEnd > maxSize) {
      maxSize *= 2

      // allocate new space for the default buffer
      glBufferData(GL_COPY_WRITE_BUFFER, maxSize, GL_DYNAMIC_DRAW)
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
    glBufferData(GL_COPY_READ_BUFFER, size, GL_STATIC_COPY)

    // copy the data from the default buffer to the temp buffer
    glCopyBufferSubData(GL_COPY_WRITE_BUFFER, GL_COPY_READ_BUFFER, 0, 0, size)

    maxSize *= 2

    // allocate new space for the default buffer with the new maxSize
    glBufferData(GL_COPY_WRITE_BUFFER, maxSize, GL_DYNAMIC_DRAW)
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
