package org.kneelawk.learningopengl.buffers

import java.nio._

import org.lwjgl.system.MemoryUtil

/**
 * Object representing an insert operation.
 */
case class GLArrayBufferInsertOperation(offset: Int, bufLen: Int, bufData: Long) {

  if (offset < 0)
    throw new IllegalArgumentException("The offset cannot be negative")

  if (bufLen < 0)
    throw new IllegalArgumentException("The length of the buffer cannot be negative")

  /**
   * ByteBuffer version of the constructor.
   */
  def this(offset: Int, buf: ByteBuffer) =
    this(offset, buf.remaining(), MemoryUtil.memAddress(buf))

  /**
   * ShortBuffer version of the constructor.
   */
  def this(offset: Int, buf: ShortBuffer) =
    this(offset, buf.remaining() << 1, MemoryUtil.memAddress(buf))

  /**
   * IntBuffer version of the constructor.
   */
  def this(offset: Int, buf: IntBuffer) =
    this(offset, buf.remaining() << 2, MemoryUtil.memAddress(buf))

  /**
   * LongBuffer version of the constructor.
   */
  def this(offset: Int, buf: LongBuffer) =
    this(offset, buf.remaining() << 3, MemoryUtil.memAddress(buf))

  /**
   * FloatBuffer version of the constructor.
   */
  def this(offset: Int, buf: FloatBuffer) =
    this(offset, buf.remaining() << 2, MemoryUtil.memAddress(buf))

  /**
   * DoubleBuffer version of the constructor.
   */
  def this(offset: Int, buf: DoubleBuffer) =
    this(offset, buf.remaining() << 3, MemoryUtil.memAddress(buf))
}