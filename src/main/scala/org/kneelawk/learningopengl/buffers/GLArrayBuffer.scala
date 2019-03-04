package org.kneelawk.learningopengl.buffers

import java.nio._

import org.kneelawk.learningopengl.util.TryUtil._
import org.lwjgl.opengl.ARBInvalidateSubdata._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL31._
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil._

/**
 * Object and utilities for managing buffer objects.
 */
class GLArrayBuffer(initialAllocation: Long) {

  /**
   * Array of both buffers for safe keeping.
   */
  private val bufArray = new Array[Int](2)
  // Create both buffer names
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

  // bind default buffer to GL_ARRAY_BUFFER
  glBindBuffer(GL_ARRAY_BUFFER, defaultBuf)
  // initialize the space within the default buffer
  glBufferData(GL_ARRAY_BUFFER, maxSize, GL_DYNAMIC_DRAW)

  /**
   * This buffer's id.
   */
  def getId: Int = defaultBuf

  /**
   * The current length of this buffer.
   */
  def getSize: Long = size

  /**
   * Set data within this buffer.
   * This can extend the length of this buffer if offset + buf.remaining() > getSize.
   * ByteBuffer version.
   */
  def set(offset: Long, buf: ByteBuffer) {
    setNative(offset, buf.remaining(), memAddress(buf))
  }

  /**
   * Set a single byte within this buffer.
   *
   * @param offset the offset of the byte to set.
   * @param value  the new value of the byte to set.
   */
  def set(offset: Long, value: Byte): Unit = {
    tryWith(stackPush()) { stack =>
      set(offset, stack.bytes(value))
    }
  }

  /**
   * Set data within this buffer.
   * This can extend the length of this buffer if offset + buf.remaining() > getSize.
   * ShortBuffer version.
   */
  def set(offset: Long, buf: ShortBuffer) {
    setNative(offset, buf.remaining() << 1, memAddress(buf))
  }

  /**
   * Set a single short within this buffer.
   *
   * @param offset the offset in bytes of the short to set.
   * @param value  the new value of the short to set.
   */
  def set(offset: Long, value: Short): Unit = {
    tryWith(stackPush()) { stack =>
      set(offset, stack.shorts(value))
    }
  }

  /**
   * Set data within this buffer.
   * This can extend the length of this buffer if offset + buf.remaining() > getSize.
   * IntBuffer version.
   */
  def set(offset: Long, buf: IntBuffer) {
    setNative(offset, buf.remaining() << 2, memAddress(buf))
  }

  /**
   * Set a single int within this buffer.
   *
   * @param offset the offset in bytes of the int to set.
   * @param value  the new value of the int to set.
   */
  def set(offset: Long, value: Int): Unit = {
    tryWith(stackPush()) { stack =>
      set(offset, stack.ints(value))
    }
  }

  /**
   * Set data within this buffer.
   * This can extend the length of this buffer if offset + buf.remaining() > getSize.
   * LongBuffer version.
   */
  def set(offset: Long, buf: LongBuffer) {
    setNative(offset, buf.remaining() << 3, memAddress(buf))
  }

  /**
   * Set a single long within this buffer.
   *
   * @param offset the offset in bytes of the long to set.
   * @param value  the new value of the long to set.
   */
  def set(offset: Long, value: Long): Unit = {
    tryWith(stackPush()) { stack =>
      set(offset, stack.longs(value))
    }
  }

  /**
   * Set data within this buffer.
   * This can extend the length of this buffer if offset + buf.remaining() > getSize.
   * FloatBuffer version.
   */
  def set(offset: Long, buf: FloatBuffer) {
    setNative(offset, buf.remaining() << 2, memAddress(buf))
  }

  /**
   * Set a single float within this buffer.
   *
   * @param offset the offset in bytes of the float to set.
   * @param value  the new value of the float to set.
   */
  def set(offset: Long, value: Float): Unit = {
    tryWith(stackPush()) { stack =>
      set(offset, stack.floats(value))
    }
  }

  /**
   * Set data within this buffer.
   * This can extend the length of this buffer if offset + buf.remaining() > getSize.
   * DoubleBuffer version.
   */
  def set(offset: Long, buf: DoubleBuffer) {
    setNative(offset, buf.remaining() << 3, memAddress(buf))
  }

  /**
   * Set a single double within this buffer.
   *
   * @param offset the offset in bytes of the double to set.
   * @param value  the new value of the double to set.
   */
  def set(offset: Long, value: Double): Unit = {
    tryWith(stackPush()) { stack =>
      set(offset, stack.doubles(value))
    }
  }

  /**
   * Set data within this buffer.
   * This can extend the length of this buffer if offset + len > getSize.
   * Memory length and address version.
   */
  def setNative(offset: Long, len: Long, data: Long) {
    if (len < 0)
      throw new IllegalArgumentException("The length of the buffer cannot be negative")

    val chunkEnd = offset + len

    extendToPoint(chunkEnd)
    glBindBuffer(GL_ARRAY_BUFFER, defaultBuf)
    nglBufferSubData(GL_ARRAY_BUFFER, offset, len, data)

    if (chunkEnd > size) {
      size = chunkEnd
    }
  }

  /**
   * Bulk set chunks of data within this buffer.
   * This can extend the length of this buffer if any of the operations' offset + their bufLen > getSize.
   */
  def setChunks(tasks: Seq[GLArrayBufferSetOperation]) {
    // calculate the end of the last chunk
    val lastChunk = tasks.maxBy(t => t.offset + t.bufLen)
    val lastChunkEnd = lastChunk.offset + lastChunk.bufLen

    // make sure the buffer is large enough
    extendToPoint(lastChunkEnd)

    // bind the buffer
    glBindBuffer(GL_ARRAY_BUFFER, defaultBuf)

    // allocate a client side buffer to operate on
    val defaultBufData = nmemAllocChecked(lastChunkEnd)
    // copy the graphics side buffer to the client side buffer
    nglGetBufferSubData(GL_ARRAY_BUFFER, 0, lastChunkEnd, defaultBufData)

    // perform every operation on the client side buffer
    for (chunk <- tasks) {
      memCopy(defaultBufData + chunk.offset, chunk.bufData, chunk.bufLen)
    }

    // copy the client side buffer back to the graphics side buffer
    nglBufferSubData(GL_ARRAY_BUFFER, 0, lastChunkEnd, defaultBufData)

    // free the client side buffer
    nmemFree(defaultBufData)

    // extend the size
    if (lastChunkEnd > size) {
      size = lastChunkEnd
    }
  }

  /**
   * Append data to the end of this buffer.
   * ByteBuffer version.
   */
  def append(buf: ByteBuffer) {
    appendNative(buf.remaining(), memAddress(buf))
  }

  /**
   * Append a single byte to the end of this buffer.
   *
   * @param value the value of the byte to append.
   */
  def append(value: Byte): Unit = {
    tryWith(stackPush()) { stack =>
      append(stack.bytes(value))
    }
  }

  /**
   * Append data to the end of this buffer.
   * ShortBuffer version.
   */
  def append(buf: ShortBuffer) {
    appendNative(buf.remaining() << 1, memAddress(buf))
  }

  /**
   * Append a single short to the end of this buffer.
   *
   * @param value the value of the short to append.
   */
  def append(value: Short): Unit = {
    tryWith(stackPush()) { stack =>
      append(stack.shorts(value))
    }
  }

  /**
   * Append data to the end of this buffer.
   * IntBuffer version.
   */
  def append(buf: IntBuffer) {
    appendNative(buf.remaining() << 2, memAddress(buf))
  }

  /**
   * Append a single int to the end of this buffer.
   *
   * @param value the value of the int to append.
   */
  def append(value: Int): Unit = {
    tryWith(stackPush()) { stack =>
      append(stack.ints(value))
    }
  }

  /**
   * Append data to the end of this buffer.
   * LongBuffer version.
   */
  def append(buf: LongBuffer) {
    appendNative(buf.remaining() << 3, memAddress(buf))
  }

  /**
   * Append a single long to the end of this buffer.
   *
   * @param value the value of the long to append.
   */
  def append(value: Long): Unit = {
    tryWith(stackPush()) { stack =>
      append(stack.longs(value))
    }
  }

  /**
   * Append data to the end of this buffer.
   * FloatBuffer version.
   */
  def append(buf: FloatBuffer) {
    appendNative(buf.remaining() << 2, memAddress(buf))
  }

  /**
   * Append a single float to the end of this buffer.
   *
   * @param value the value of the float to append.
   */
  def append(value: Float): Unit = {
    tryWith(stackPush()) { stack =>
      append(stack.floats(value))
    }
  }

  /**
   * Append data to the end of this buffer.
   * DoubleBuffer version.
   */
  def append(buf: DoubleBuffer) {
    appendNative(buf.remaining() << 3, memAddress(buf))
  }

  /**
   * Append a single double to the end of this buffer.
   *
   * @param value the value of the double to append.
   */
  def append(value: Double): Unit = {
    tryWith(stackPush()) { stack =>
      append(stack.doubles(value))
    }
  }

  /**
   * Append data to the end of this buffer.
   * Memory length and address version.
   */
  def appendNative(len: Long, data: Long) {
    if (len < 0)
      throw new IllegalArgumentException("The length of the buffer cannot be negative")

    extendFromEnd(len)
    glBindBuffer(GL_ARRAY_BUFFER, defaultBuf)
    nglBufferSubData(GL_ARRAY_BUFFER, size, len, data)

    size += len
  }

  /**
   * Inserts a chunk of data into this buffer at offset, moving the data currently after
   * offset to the end of where this chunk is inserted.
   * ByteBuffer version.
   */
  def insert(offset: Long, buf: ByteBuffer) {
    insertNative(offset, buf.remaining(), memAddress(buf))
  }

  /**
   * Inserts a single byte into this buffer at offset, moving the data currently after
   * offset to the end of where this byte is inserted.
   *
   * @param offset the offset of the byte to be inserted.
   * @param value  the value of the byte to be inserted.
   */
  def insert(offset: Long, value: Byte): Unit = {
    tryWith(stackPush()) { stack =>
      insert(offset, stack.bytes(value))
    }
  }

  /**
   * Inserts a chunk of data into this buffer at offset, moving the data currently after
   * offset to the end of where this chunk is inserted.
   * ShortBuffer version.
   */
  def insert(offset: Long, buf: ShortBuffer) {
    insertNative(offset, buf.remaining() << 1, memAddress(buf))
  }

  /**
   * Inserts a single short into this buffer at offset, moving the data currently after
   * offset to the end of where this short is inserted.
   *
   * @param offset the offset in byte of the short to be inserted.
   * @param value  the value of the short to be inserted.
   */
  def insert(offset: Long, value: Short): Unit = {
    tryWith(stackPush()) { stack =>
      insert(offset, stack.shorts(value))
    }
  }

  /**
   * Inserts a chunk of data into this buffer at offset, moving the data currently after
   * offset to the end of where this chunk is inserted.
   * IntBuffer version.
   */
  def insert(offset: Long, buf: IntBuffer) {
    insertNative(offset, buf.remaining() << 2, memAddress(buf))
  }

  /**
   * Inserts a single int into this buffer at offset, moving the data currently after
   * offset to the end of where this int is inserted.
   *
   * @param offset the offset in bytes of the int to be inserted.
   * @param value  the value of the int to be inserted.
   */
  def insert(offset: Long, value: Int): Unit = {
    tryWith(stackPush()) { stack =>
      insert(offset, stack.ints(value))
    }
  }

  /**
   * Inserts a chunk of data into this buffer at offset, moving the data currently after
   * offset to the end of where this chunk is inserted.
   * LongBuffer version.
   */
  def insert(offset: Long, buf: LongBuffer) {
    insertNative(offset, buf.remaining() << 3, memAddress(buf))
  }

  /**
   * Inserts a single long into this buffer at offset, moving the data currently after
   * offset to the end of where this long is inserted.
   *
   * @param offset the offset in bytes of the long to be inserted.
   * @param value  the value of the long to be inserted.
   */
  def insert(offset: Long, value: Long): Unit = {
    tryWith(stackPush()) { stack =>
      insert(offset, stack.longs(value))
    }
  }

  /**
   * Inserts a chunk of data into this buffer at offset, moving the data currently after
   * offset to the end of where this chunk is inserted.
   * FloatBuffer version.
   */
  def insert(offset: Long, buf: FloatBuffer) {
    insertNative(offset, buf.remaining() << 2, memAddress(buf))
  }

  /**
   * Inserts a single float into this buffer at offset, moving the data currently after
   * offset to the end of where this float is inserted.
   *
   * @param offset the offset in bytes of the float to be inserted.
   * @param value  the value of the float to be inserted.
   */
  def insert(offset: Long, value: Float): Unit = {
    tryWith(stackPush()) { stack =>
      insert(offset, stack.floats(value))
    }
  }

  /**
   * Inserts a chunk of data into this buffer at offset, moving the data currently after
   * offset to the end of where this chunk is inserted.
   * DoubleBuffer version.
   */
  def insert(offset: Long, buf: DoubleBuffer) {
    insertNative(offset, buf.remaining() << 3, memAddress(buf))
  }

  /**
   * Inserts a single double into this buffer at offset, moving the data currently after
   * offset to the end of where this double is inserted.
   *
   * @param offset the offset in bytes of the double to be inserted.
   * @param value  the value of the double to be inserted.
   */
  def insert(offset: Long, value: Double): Unit = {
    tryWith(stackPush()) { stack =>
      insert(offset, stack.doubles(value))
    }
  }

  /**
   * Inserts a chunk of data into this buffer at offset, moving the data currently after
   * offset to the end of where this chunk is inserted.
   * Memory length and address version.
   */
  def insertNative(offset: Long, len: Long, data: Long) {
    if (len < 0)
      throw new IllegalArgumentException("The length of the buffer cannot be negative")

    if (offset >= size) {
      setNative(offset, len, data)
    } else {
      copyChunk(offset, offset + len, size - offset)
      glBindBuffer(GL_ARRAY_BUFFER, defaultBuf)
      nglBufferSubData(GL_ARRAY_BUFFER, offset, len, data)

      size += len
    }
  }

  /**
   * Bulk insert chunks of data into this buffer. This will increase the size of this buffer.
   *
   * @param tasks The sequence of insert operations to be performed
   */
  def insertChunks(tasks: Seq[GLArrayBufferInsertOperation]) {
    // calculate the size of the new buffer
    val newSize = tasks.foldLeft(size)((a, b) => a + b.bufLen)

    // make sure this buffer is large enough
    extendToPoint(newSize)

    // bind the default buffer
    glBindBuffer(GL_ARRAY_BUFFER, defaultBuf)

    // allocate a cpu side buffer for the data
    val defaultBufData = nmemAllocChecked(newSize)
    // allocate a temp cpu side buffer for the data
    val tempHolder = nmemAllocChecked(newSize)
    // copy the data from the graphics side buffer to the cpu side buffer
    nglGetBufferSubData(GL_ARRAY_BUFFER, 0, newSize, defaultBufData)

    // this keeps track of the current size as we grow it
    var currentSize = size

    // apply every operation in sequence
    for (chunk <- tasks) {
      // read the data after the insert into the temp cpu side buffer
      memCopy(defaultBufData + chunk.offset, tempHolder, currentSize - chunk.offset)
      // write the inserted data to the default cpu side buffer
      memCopy(chunk.bufData, defaultBufData + chunk.offset, chunk.bufLen)
      // write the temp buffer's content after the inserted data
      memCopy(tempHolder, defaultBufData + chunk.offset + chunk.bufLen, currentSize - chunk.offset)
      // keep track of buffer size increases
      currentSize += chunk.bufLen
    }

    // copy the the data from the cpu side buffer back to the graphics side buffer
    nglBufferSubData(GL_ARRAY_BUFFER, 0, newSize, defaultBufData)

    // free our buffers now that we're done with them
    nmemFree(defaultBufData)
    nmemFree(tempHolder)

    // set the buffer size field
    size = newSize
  }

  /**
   * Replaces the chunk at offset of size chunkLen with the data in buf.
   * ByteBuffer version.
   */
  def replace(offset: Long, chunkLen: Long, buf: ByteBuffer) {
    replaceNative(offset, chunkLen, buf.remaining(), memAddress(buf))
  }

  /**
   * Replaces the chunk at offset of size chunkLen with the data in buf.
   * ShortBuffer version.
   */
  def replace(offset: Long, chunkLen: Long, buf: ShortBuffer) {
    replaceNative(offset, chunkLen, buf.remaining() << 1, memAddress(buf))
  }

  /**
   * Replaces the chunk at offset of size chunkLen with the data in buf.
   * IntBuffer version.
   */
  def replace(offset: Long, chunkLen: Long, buf: IntBuffer) {
    replaceNative(offset, chunkLen, buf.remaining() << 2, memAddress(buf))
  }

  /**
   * Replaces the chunk at offset of size chunkLen with the data in buf.
   * LongBuffer version.
   */
  def replace(offset: Long, chunkLen: Long, buf: LongBuffer) {
    replaceNative(offset, chunkLen, buf.remaining() << 3, memAddress(buf))
  }

  /**
   * Replaces the chunk at offset of size chunkLen with the data in buf.
   * FloatBuffer version.
   */
  def replace(offset: Long, chunkLen: Long, buf: FloatBuffer) {
    replaceNative(offset, chunkLen, buf.remaining() << 2, memAddress(buf))
  }

  /**
   * Replaces the chunk at offset of size chunkLen with the data in buf.
   * DoubleBuffer version.
   */
  def replace(offset: Long, chunkLen: Long, buf: DoubleBuffer) {
    replaceNative(offset, chunkLen, buf.remaining() << 3, memAddress(buf))
  }

  /**
   * Replaces the chunk at offset of size chunkLen with the data represented by len and data.
   * Memory length and address version.
   */
  def replaceNative(offset: Long, chunkLen: Long, len: Long, data: Long) {
    if (chunkLen < 0)
      throw new IllegalArgumentException("The length of the chunk to be replaced cannot be negative")

    if (len < 0)
      throw new IllegalArgumentException("The length of the buffer cannot be negative")

    val lenDif = len - chunkLen

    if (offset + chunkLen < size) {
      if (lenDif != 0) {
        copyChunk(offset + chunkLen, offset + len, size - (offset + chunkLen))
      }

      glBindBuffer(GL_ARRAY_BUFFER, defaultBuf)
      nglBufferSubData(GL_ARRAY_BUFFER, offset, len, data)

      size += lenDif
    } else {
      extendToPoint(offset + len)
      glBindBuffer(GL_ARRAY_BUFFER, defaultBuf)
      nglBufferSubData(GL_ARRAY_BUFFER, offset, len, data)

      size = offset + len
    }
  }

  /**
   * Bulk replacement of chunks of data within the buffer.
   *
   * @param tasks The sequence of every operation to perform on the buffer
   */
  def replaceChunks(tasks: Seq[GLArrayBufferReplaceOperation]) {
    // calculate the new size of the buffer after every operation has been performed
    var maxSize = size
    var newSize = size
    tasks.foreach { chunk =>
      newSize +=
        // make sure to properly manage the replacement of chunks extending beyond the end of the buffer
        (if (chunk.offset + chunk.chunkLen > newSize) {
          chunk.bufLen - (newSize - chunk.offset)
        } else {
          chunk.bufLen - chunk.chunkLen
        })

      // keep track of the largest the buffer will need to get
      if (newSize > maxSize) {
        maxSize = newSize
      }
    }

    if (newSize <= 0) {
      // if performing every operation would result in a buffer with length 0, then simply clear the buffer
      clear()
    } else {
      // make sure the graphics side buffer is large enough
      extendToPoint(newSize)

      // bind the default buffer
      glBindBuffer(GL_ARRAY_BUFFER, defaultBuf)

      // allocate a cpu side buffer to perform each operation on
      val defaultBufData = nmemAllocChecked(maxSize)
      // allocate a temp cpu side buffer to help with the operations
      val tempHolder = nmemAllocChecked(maxSize)
      // copy all the data from the graphics side buffer to the cpu side buffer
      nglGetBufferSubData(GL_ARRAY_BUFFER, 0, size, defaultBufData)

      // keep track of the current size of the cpu side buffer
      var currentSize = size

      // perform every operation in order
      for (chunk <- tasks) {
        if (chunk.offset + chunk.chunkLen < currentSize) {
          // if the end of the region to be replaced does not extend beyond the end of the current state of the cpu side
          // buffer, then simply make room for the new data and copy it in
          if (chunk.bufLen - chunk.chunkLen != 0) {
            // if there is a difference between the size of the chunk to be replaced and that of the data to replace it
            // with, then move the data after the end of the chunk to be replaced to the end of where the new data will
            // be inserted
            memCopy(defaultBufData + chunk.offset + chunk.chunkLen, tempHolder, currentSize - (chunk.offset + chunk.chunkLen))
            memCopy(tempHolder, defaultBufData + chunk.offset + chunk.bufLen, currentSize - (chunk.offset + chunk.chunkLen))
          }
          // copy the new data into the cpu side buffer
          memCopy(chunk.bufData, defaultBufData + chunk.offset, chunk.bufLen)
          // adjust the current size of the cpu buffer
          currentSize += chunk.bufLen - chunk.chunkLen
        } else {
          // if the end of the region to be replaced does extend beyond the end of the current state of the cpu side
          // buffer, then there is no need to make room for the new data

          // copy the new data into the cpu side buffer
          memCopy(chunk.bufData, defaultBufData + chunk.offset, chunk.bufLen)
          // set the current size to the end of the inserted data because the region to be replaced extended beyond the
          // end of the old current size so the end of new current size is simply the end of the region the new data was
          // copied to
          currentSize = chunk.offset + chunk.bufLen
        }
      }

      // copy the cpu side buffer data back into the graphics side buffer
      nglBufferSubData(GL_ARRAY_BUFFER, 0, newSize, defaultBufData)

      // free the cpu side buffers
      nmemFree(defaultBufData)
      nmemFree(tempHolder)

      // update the current size of the buffer accounting for all the operations performed
      size = newSize
    }
  }

  /**
   * Replaces everything at and after offset with the data in buf.
   * ByteBuffer version.
   */
  def replaceAfter(offset: Long, buf: ByteBuffer) {
    replaceAfterNative(offset, buf.remaining(), memAddress(buf))
  }

  /**
   * Replaces everything at and after offset with the data in buf.
   * ShortBuffer version.
   */
  def replaceAfter(offset: Long, buf: ShortBuffer) {
    replaceAfterNative(offset, buf.remaining() << 1, memAddress(buf))
  }

  /**
   * Replaces everything at and after offset with the data in buf.
   * IntBuffer version.
   */
  def replaceAfter(offset: Long, buf: IntBuffer) {
    replaceAfterNative(offset, buf.remaining() << 2, memAddress(buf))
  }

  /**
   * Replaces everything at and after offset with the data in buf.
   * LongBuffer version.
   */
  def replaceAfter(offset: Long, buf: LongBuffer) {
    replaceAfterNative(offset, buf.remaining() << 3, memAddress(buf))
  }

  /**
   * Replaces everything at and after offset with the data in buf.
   * FloatBuffer version.
   */
  def replaceAfter(offset: Long, buf: FloatBuffer) {
    replaceAfterNative(offset, buf.remaining() << 2, memAddress(buf))
  }

  /**
   * Replaces everything at and after offset with the data in buf.
   * DoubleBuffer version.
   */
  def replaceAfter(offset: Long, buf: DoubleBuffer) {
    replaceAfterNative(offset, buf.remaining() << 3, memAddress(buf))
  }

  /**
   * Replaces everything at and after offset with the data represented by len and data.
   * Memory length and address version.
   */
  def replaceAfterNative(offset: Long, len: Long, data: Long) {
    if (len < 0)
      throw new IllegalArgumentException("The length of the buffer cannot be negative")

    if (offset > size)
      throw new IndexOutOfBoundsException(s"Cannot replace a chunk at $offset (beyond size $size)")

    extendToPoint(offset + len)
    glBindBuffer(GL_ARRAY_BUFFER, defaultBuf)
    nglBufferSubData(GL_ARRAY_BUFFER, offset, len, data)

    size = offset + len
  }

  /**
   * Replaces everything before cutoff with the data in buf.
   * ByteBuffer version.
   */
  def replaceBefore(cutoff: Long, buf: ByteBuffer) {
    replaceBeforeNative(cutoff, buf.remaining(), memAddress(buf))
  }

  /**
   * Replaces everything before cutoff with the data in buf.
   * ShortBuffer version.
   */
  def replaceBefore(cutoff: Long, buf: ShortBuffer) {
    replaceBeforeNative(cutoff, buf.remaining() << 1, memAddress(buf))
  }

  /**
   * Replaces everything before cutoff with the data in buf.
   * IntBuffer version.
   */
  def replaceBefore(cutoff: Long, buf: IntBuffer) {
    replaceBeforeNative(cutoff, buf.remaining() << 2, memAddress(buf))
  }

  /**
   * Replaces everything before cutoff with the data in buf.
   * LongBuffer version.
   */
  def replaceBefore(cutoff: Long, buf: LongBuffer) {
    replaceBeforeNative(cutoff, buf.remaining() << 3, memAddress(buf))
  }

  /**
   * Replaces everything before cutoff with the data in buf.
   * FloatBuffer version.
   */
  def replaceBefore(cutoff: Long, buf: FloatBuffer) {
    replaceBeforeNative(cutoff, buf.remaining() << 2, memAddress(buf))
  }

  /**
   * Replaces everything before cutoff with the data in buf.
   * DoubleBuffer version.
   */
  def replaceBefore(cutoff: Long, buf: DoubleBuffer) {
    replaceBeforeNative(cutoff, buf.remaining() << 3, memAddress(buf))
  }

  /**
   * Replaces everything before cutoff with the data by len and data.
   * Memory length and address version.
   */
  def replaceBeforeNative(cutoff: Long, len: Long, data: Long) {
    if (len < 0)
      throw new IllegalArgumentException("The length of the buffer cannot be negative")

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
   * Bulk remove chunks from this buffer.
   *
   * @param tasks The sequence of remove operations to be performed.
   */
  def removeChunks(tasks: Seq[GLArrayBufferRemoveOperation]) {
    // calculate the new size of the buffer after every operations has been completed
    val newSize = tasks.foldLeft(size)((a, op) => a - op.chunkLen)

    if (newSize <= 0) {
      // if the sequence of operations would result in a length of 0, then simply clear the buffer
      clear()
    } else {
      // bind the buffer
      glBindBuffer(GL_ARRAY_BUFFER, defaultBuf)

      // allocate a cpu side buffer to perform operations on
      val defaultBufData = nmemAlloc(size)
      // allocate a temp buffer to help with the operations
      val tempHolder = nmemAlloc(size)
      // copy all data from the graphic side buffer to the cpu side buffer
      nglGetBufferSubData(GL_ARRAY_BUFFER, 0, size, defaultBufData)

      // keep track of the current size of the buffer
      var curSize = size

      // perform every operation on the cpu side buffer
      for (op <- tasks) {
        // copy the section after the chunk to be removed into the temp cpu buffer
        memCopy(defaultBufData + op.offset + op.chunkLen, tempHolder, curSize - (op.offset + op.chunkLen))
        // copy the section after the chunk to be removed back from the temp buffer to the start position of the chunk
        // to be removed
        memCopy(tempHolder, defaultBufData + op.offset, curSize - (op.offset + op.chunkLen))
        // update the current size accounting for the chunk removal
        curSize -= op.chunkLen
      }

      // copy the completed cpu side buffer data back to the graphics buffer
      nglBufferSubData(GL_ARRAY_BUFFER, 0, newSize, defaultBufData)

      // free the cpu side buffers
      nmemFree(defaultBufData)
      nmemFree(tempHolder)

      // update the buffer's size accounting for all the removals
      size = newSize
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
   * Deletes the buffers held by this object, invalidating it.
   */
  def destroy() {
    glDeleteBuffers(bufArray)
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

      // keep growing until we're big enough
      while (chunkEnd > maxSize)
        maxSize *= 2

      // allocate new space for the default buffer
      glBufferData(GL_COPY_WRITE_BUFFER, maxSize, GL_DYNAMIC_DRAW)
      // copy the old data back from the temp buffer
      glCopyBufferSubData(GL_COPY_READ_BUFFER, GL_COPY_WRITE_BUFFER, 0, 0, size)
    }

    // copy the chunk from the source location in the temp buffer to the dest location in the default buffer
    glCopyBufferSubData(GL_COPY_READ_BUFFER, GL_COPY_WRITE_BUFFER, sourceOffset, destOffset, chunkLen)

    // free the temp buffer's allocated space
    glInvalidateBufferData(tempBuf)
  }

  /**
   * Designed for bulk-copying different chunks around within the default buffer, resizing if necessary.
   */
  private def copyChunks(tasks: Seq[ChunkCopyTask]) {
    // find the chunk that ends last
    val lastChunk = tasks.maxBy(t => t.destOffset + t.chunkLen)
    // where does that last chunk end?
    val lastChunkEnd = lastChunk.destOffset + lastChunk.chunkLen

    // bind default buffer to GL_COPY_WRITE_BUFFER
    glBindBuffer(GL_COPY_WRITE_BUFFER, defaultBuf)
    // bind temp buffer to GL_COPY_READ_BUFFER
    glBindBuffer(GL_COPY_READ_BUFFER, tempBuf)
    // allocate space for the temp buffer
    glBufferData(GL_COPY_READ_BUFFER, size, GL_STATIC_COPY)

    // copy the data from the default buffer to the temp buffer
    glCopyBufferSubData(GL_COPY_WRITE_BUFFER, GL_COPY_READ_BUFFER, 0, 0, size)

    // if a resize is needed then resize and copy all the data back from the temp buffer
    if (lastChunkEnd > maxSize) {

      // keep growing until we're big enough
      while (lastChunkEnd > maxSize)
        maxSize *= 2

      // allocate new space for the default buffer
      glBufferData(GL_COPY_WRITE_BUFFER, maxSize, GL_DYNAMIC_DRAW)
      // copy the old data back from the temp buffer
      glCopyBufferSubData(GL_COPY_READ_BUFFER, GL_COPY_WRITE_BUFFER, 0, 0, size)
    }

    // for each copy task:
    for (chunk <- tasks) {
      // copy the chunk from the source location in the temp buffer to the dest location in the default buffer
      glCopyBufferSubData(GL_COPY_READ_BUFFER, GL_COPY_WRITE_BUFFER, chunk.sourceOffset, chunk.destOffset, chunk.chunkLen)
    }

    // free the temp buffer's allocated space
    glInvalidateBufferData(tempBuf)
  }

  /**
   * Doubles the length of allocated space for this buffer.
   */
  private def resize(newSize: Long) {
    // bind default buffer to GL_COPY_WRITE_BUFFER
    glBindBuffer(GL_COPY_WRITE_BUFFER, defaultBuf)
    // bind temp buffer to GL_COPY_READ_BUFFER
    glBindBuffer(GL_COPY_READ_BUFFER, tempBuf)
    // allocate data for the temp buffer but only as much
    // as there is data in the default buffer
    glBufferData(GL_COPY_READ_BUFFER, size, GL_STATIC_COPY)

    // copy the data from the default buffer to the temp buffer
    glCopyBufferSubData(GL_COPY_WRITE_BUFFER, GL_COPY_READ_BUFFER, 0, 0, size)

    while (maxSize < newSize)
      maxSize *= 2

    // allocate new space for the default buffer with the new maxSize
    glBufferData(GL_COPY_WRITE_BUFFER, maxSize, GL_DYNAMIC_DRAW)
    // copy the old data back from the temp buffer
    glCopyBufferSubData(GL_COPY_READ_BUFFER, GL_COPY_WRITE_BUFFER, 0, 0, size)

    // free the temp buffer's allocated space
    glInvalidateBufferData(tempBuf)
  }

  /**
   * Resizes if needed.
   */
  private def extendFromEnd(extraSize: Long) {
    if (extraSize + size > maxSize) {
      resize(extraSize + size)
    }
  }

  /**
   * Resizes if needed.
   */
  private def extendToPoint(point: Long) {
    if (point > maxSize) {
      resize(point)
    }
  }

  /**
   * Case class designed to be used in lists for bulk copy operations.
   */
  private case class ChunkCopyTask(var sourceOffset: Long, var destOffset: Long, var chunkLen: Long)

  private case class ChunkSetTask(var bufOffset: Long, var bufLen: Long, var bufData: Long, var destOffset: Long)

}
