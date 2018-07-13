package org.kneelawk.learningopengl.buffers

import java.nio.ByteBuffer
import java.nio.DoubleBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.LongBuffer
import java.nio.ShortBuffer

import org.lwjgl.system.MemoryUtil

/**
 * Object representing an insert operation.
 */
class GLArrayBufferInsertOperation(offset: Long, bufLen: Long, bufData: Long) {

  /**
   * ByteBuffer version of the constructor.
   */
  def this(offset: Long, buf: ByteBuffer) =
    this(offset, buf.remaining(), MemoryUtil.memAddress(buf))

  /**
   * ShortBuffer version of the constructor.
   */
  def this(offset: Long, buf: ShortBuffer) =
    this(offset, buf.remaining() << 1, MemoryUtil.memAddress(buf))

  /**
   * IntBuffer version of the constructor.
   */
  def this(offset: Long, buf: IntBuffer) =
    this(offset, buf.remaining() << 2, MemoryUtil.memAddress(buf))

  /**
   * LongBuffer version of the constructor.
   */
  def this(offset: Long, buf: LongBuffer) =
    this(offset, buf.remaining() << 3, MemoryUtil.memAddress(buf))

  /**
   * FloatBuffer version of the constructor.
   */
  def this(offset: Long, buf: FloatBuffer) =
    this(offset, buf.remaining() << 2, MemoryUtil.memAddress(buf))

  /**
   * DoubleBuffer version of the constructor.
   */
  def this(offset: Long, buf: DoubleBuffer) =
    this(offset, buf.remaining() << 3, MemoryUtil.memAddress(buf))
}