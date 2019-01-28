package org.kneelawk.learningopengl

import org.joml.{Matrix4f, Vector3f}

class Camera {
  private val view = new Matrix4f()
  private val projection = new Matrix4f()

  private val pv = projection.mul(view, new Matrix4f())

  /**
   * Returns the current view-projection matrix.
   */
  def getMatrix: Matrix4f = pv

  /**
   * Recalculates the camera's view-projection matrix.
   */
  def update() {
    projection.mul(view, pv)
  }

  /**
   * Sets the camera's location, target sight, and up direction.
   */
  def setView(location: Vector3f, target: Vector3f, up: Vector3f) {
    view.setLookAt(location, target, up)
  }

  /**
   * Sets the camera's location, target sight, and up direction.
   */
  def setView(locationX: Float, locationY: Float, locationZ: Float, targetX: Float, targetY: Float, targetZ: Float, upX: Float, upY: Float, upZ: Float) {
    view.setLookAt(locationX, locationY, locationZ, targetX, targetY, targetZ, upX, upY, upZ)
  }

  /**
   * Sets the camera's projection fovy, aspect ratio, z-near, and z-far.
   */
  //noinspection SpellCheckingInspection,SpellCheckingInspection
  def setProjection(fovy: Float, aspect: Float, zNear: Float, zFar: Float) {
    projection.setPerspective(fovy, aspect, zNear, zFar)
  }
}