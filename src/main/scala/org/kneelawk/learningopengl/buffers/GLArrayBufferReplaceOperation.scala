package org.kneelawk.learningopengl.buffers

import java.nio.ByteBuffer
import java.nio.DoubleBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.LongBuffer
import java.nio.ShortBuffer

import org.lwjgl.system.MemoryUtil

/**
 * Object representing a replace operation.
 */
class GLArrayBufferReplaceOperation(offset: Long, chunkLen: Long, bufLen: Long, bufData: Long) {

  if (offset < 0)
    throw new IllegalArgumentException("The offset cannot be negative")

  if (chunkLen < 0)
    throw new IllegalArgumentException("The length of the chunk to be replaced cannot be negative")

  if (bufLen < 0)
    throw new IllegalArgumentException("The length of the buffer cannot be negative")

  /**
   * ByteBuffer version of the constructor.
   */
  def this(offset: Long, chunkLen: Long, buf: ByteBuffer) =
    this(offset, chunkLen, buf.remaining(), MemoryUtil.memAddress(buf))

  /**
   * ShortBuffer version of the constructor.
   */
  def this(offset: Long, chunkLen: Long, buf: ShortBuffer) =
    this(offset, chunkLen, buf.remaining() << 1, MemoryUtil.memAddress(buf))

  /**
   * IntBuffer version of the constructor.
   */
  def this(offset: Long, chunkLen: Long, buf: IntBuffer) =
    this(offset, chunkLen, buf.remaining() << 2, MemoryUtil.memAddress(buf))

  /**
   * LongBuffer version of the constructor.
   */
  def this(offset: Long, chunkLen: Long, buf: LongBuffer) =
    this(offset, chunkLen, buf.remaining() << 3, MemoryUtil.memAddress(buf))

  /**
   * FloatBuffer version of the constructor.
   */
  def this(offset: Long, chunkLen: Long, buf: FloatBuffer) =
    this(offset, chunkLen, buf.remaining() << 2, MemoryUtil.memAddress(buf))

  /**
   * DoubleBuffer version of the constructor.
   */
  def this(offset: Long, chunkLen: Long, buf: DoubleBuffer) =
    this(offset, chunkLen, buf.remaining() << 3, MemoryUtil.memAddress(buf))
}