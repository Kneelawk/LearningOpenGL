package org.kneelawk.learningopengl

import org.joml.{Matrix4f, Vector3f}

class Camera {
  private val location = new Vector3f(0, 0, 1)
  private val target = new Vector3f(0, 0, 0)
  private val up = new Vector3f(0, 1, 0)
  private var fovy = 45f
  private var aspect = 1f
  private var zNear = 0.01f
  private var zFar = 100f

  private val view = new Matrix4f().setLookAt(location, target, up)
  private val projection = new Matrix4f().setPerspective(fovy, aspect, zNear, zFar)

  private val pv = projection.mul(view, new Matrix4f())

  /**
   * Returns the current view-projection matrix.
   */
  def getMatrix: Matrix4f = pv

  def getLocation: Vector3f = location
  def getTarget: Vector3f = target
  def getUp: Vector3f = up

  def getFovy: Float = fovy
  def getAspect: Float = aspect
  def getZNear: Float = zNear
  def getZFar: Float = zFar

  def getView: Matrix4f = view
  def getProjection: Matrix4f = projection

  /**
   * Recalculates the camera's matrices.
   */
  def update() {
    view.setLookAt(location, target, up)
    projection.setPerspective(fovy, aspect, zNear, zFar)
    projection.mul(view, pv)
  }

  def setLocation(location: Vector3f): Unit = {
    this.location.set(location)
  }

  def setLocation(locationX: Float, locationY: Float, locationZ: Float): Unit = {
    location.x = locationX
    location.y = locationY
    location.z = locationZ
  }

  def setTarget(target: Vector3f): Unit = {
    this.target.set(target)
  }

  def setTarget(targetX: Float, targetY: Float, targetZ: Float): Unit = {
    target.x = targetX
    target.y = targetY
    target.z = targetZ
  }

  def setUp(up: Vector3f): Unit = {
    this.up.set(up)
  }

  def setUp(upX: Float, upY: Float, upZ: Float): Unit = {
    up.x = upX
    up.y = upY
    up.z = upZ
  }

  def setFovy(fovy: Float): Unit = {
    this.fovy = fovy
  }

  def setAspect(aspect: Float): Unit = {
    this.aspect = aspect
  }

  def setZNear(zNear: Float): Unit = {
    this.zNear = zNear
  }

  def setZFar(zFar: Float): Unit = {
    this.zFar = zFar
  }

  /**
   * Sets the camera's location, target sight, and up direction.
   */
  def setView(location: Vector3f, target: Vector3f, up: Vector3f) {
    this.location.set(location)
    this.target.set(target)
    this.up.set(up)
  }

  /**
   * Sets the camera's location, target sight, and up direction.
   */
  def setView(locationX: Float, locationY: Float, locationZ: Float, targetX: Float, targetY: Float, targetZ: Float, upX: Float, upY: Float, upZ: Float) {
    location.x = locationX
    location.y = locationY
    location.z = locationZ
    target.x = targetX
    target.y = targetY
    target.z = targetZ
    up.x = upX
    up.y = upY
    up.z = upZ
  }

  /**
   * Sets the camera's projection fovy, aspect ratio, z-near, and z-far.
   */
  //noinspection SpellCheckingInspection,SpellCheckingInspection
  def setProjection(fovy: Float, aspect: Float, zNear: Float, zFar: Float) {
    this.fovy = fovy
    this.aspect = aspect
    this.zNear = zNear
    this.zFar = zFar
  }
}