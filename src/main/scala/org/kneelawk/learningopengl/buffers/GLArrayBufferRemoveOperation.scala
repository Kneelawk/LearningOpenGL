package org.kneelawk.learningopengl.buffers

/**
 * Object representing a remove operation.
 */
case class GLArrayBufferRemoveOperation(offset: Long, chunkLen: Long) {

  if (offset < 0)
    throw new IllegalArgumentException("The offset cannot be negative")

  if (chunkLen < 0)
    throw new IllegalArgumentException("The length of the chunk to be removed cannot be negative")
}