package org.kneelawk.learningopengl.util

class DeltaHelper {
  private var lastTime = System.currentTimeMillis()
  private var delta = 1f / 60f

  def update(): Float = {
    val now = System.currentTimeMillis()
    delta = (now - lastTime) / 1000f
    lastTime = now
    delta
  }

  def getDelta: Float = delta
}
