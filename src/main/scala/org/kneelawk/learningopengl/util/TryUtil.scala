package org.kneelawk.learningopengl.util

import scala.util.control.NonFatal

object TryUtil {
  /**
   * Creates a resource, uses it within the safety of a try block, and closes it when done.
   *
   * @param creator The call-by-name argument that creates the resource
   * @param block   The block that uses the resource
   * @tparam Resource The type of resource this try with block is handling
   * @tparam Result   The resulting type of the try with block
   *
   * @return The result of the try with block
   */
  def tryWith[Resource >: Null <: AutoCloseable, Result](creator: => Resource)(block: Resource => Result): Result = {
    var resource: Resource = null
    var exception: Throwable = null
    try {
      resource = creator
      block(resource)
    } catch {
      case NonFatal(e) =>
        exception = e
        throw e
    } finally {
      if (resource != null) {
        if (exception != null) {
          try {
            resource.close()
          } catch {
            case NonFatal(e) =>
              exception.addSuppressed(e)
          }
        } else {
          resource.close()
        }
      }
    }
  }

  def tryWithDestroyer[Resource >: Null, Result](creator: => Resource)(block: Resource => Result)(destroyer: Resource => Unit): Result = {
    var resource: Resource = null
    var exception: Throwable = null
    try {
      resource = creator
      block(resource)
    } catch {
      case NonFatal(e) =>
        exception = e
        throw e
    } finally {
      if (resource != null) {
        if (exception != null) {
          try {
            destroyer(resource)
          } catch {
            case NonFatal(e) =>
              exception.addSuppressed(e)
          }
        } else {
          destroyer(resource)
        }
      }
    }
  }

  def tryWithCheckedDestroyer[Resource >: Null, Result](creator: => Resource)(block: Resource => Result)(destroyer: (Resource, Throwable => Unit) => Unit): Result = {
    var resource: Resource = null
    var exception: Throwable = null
    try {
      resource = creator
      block(resource)
    } catch {
      case NonFatal(e) =>
        exception = e
        throw e
    } finally {
      if (resource != null) {
        if (exception != null) {
          try {
            destroyer(resource, { e =>
              exception.addSuppressed(e)
            })
          } catch {
            case NonFatal(e) =>
              exception.addSuppressed(e)
          }
        } else {
          destroyer(resource, { e =>
            if (exception != null) {
              exception.addSuppressed(e)
            } else {
              exception = e
            }
          })
        }
      }
    }
  }
}
