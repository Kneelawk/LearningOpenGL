package org.kneelawk.learningopengl.simple

import java.util

import org.kneelawk.learningopengl.buffers.GLArrayBuffer
import org.kneelawk.learningopengl.shaders.{ShaderComponentSource, ShaderType, UnlinkedShaderProgram}
import org.kneelawk.learningopengl.textures.GLTextureSource
import org.kneelawk.learningopengl.util.TryUtil.{tryWith, tryWithDestroyer}
import org.kneelawk.learningopengl.{AbstractRenderEngine, Camera, Window}
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._
import org.lwjgl.system.{MemoryStack, MemoryUtil}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class SimpleVertexEngine(window: Window, camera: Camera, update: Float => Unit) extends AbstractRenderEngine[SimpleVertexModel](window, camera, update) {
  // setup clear color
  glClearColor(0.2f, 0.2f, 0.2f, 1.0f)

  // setup depth testing
  glEnable(GL_DEPTH_TEST)
  glDepthFunc(GL_LESS)

  // generate the vertex arrays
  private val vertexArrayId = glGenVertexArrays()
  // bind vertex array for setup
  glBindVertexArray(vertexArrayId)

  private val vertices = new GLArrayBuffer
  private val uvs = new GLArrayBuffer
  private val matrices = new GLArrayBuffer
  private val indices = new mutable.HashMap[SimpleVertexModel, SimpleVertexModelIndex]
  private val indexList = new ListBuffer[SimpleVertexModelIndex]

  private val vertexShader = new ShaderComponentSource(getClass.getResourceAsStream("vertex.glsl"), "vertex.glsl", ShaderType.Vertex).compile()
  private val fragmentShader = new ShaderComponentSource(getClass.getResourceAsStream("fragment.glsl"), "fragment.glsl", ShaderType.Fragment).compile()
  private val shaderProgram = (new UnlinkedShaderProgram("shader") += vertexShader += fragmentShader).link()
  vertexShader.delete()
  fragmentShader.delete()

  private val mvpBuffer = MemoryUtil.memAllocFloat(16)
  private val mvpLocation = shaderProgram.getUniformLocation("vpMatrix")

  private val texture = new GLTextureSource(getClass.getResource("texture.png")).build()

  def render() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)

    glUseProgram(shaderProgram.id)

    // bind vertex array for rendering
    glBindVertexArray(vertexArrayId)

    glBindTexture(GL_TEXTURE_2D, texture.id)

    glUniformMatrix4fv(mvpLocation, false, camera.getMatrix.get(mvpBuffer))

    glEnableVertexAttribArray(0)
    glBindBuffer(GL_ARRAY_BUFFER, vertices.getId)
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)

    glEnableVertexAttribArray(1)
    glBindBuffer(GL_ARRAY_BUFFER, uvs.getId)
    glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0)

    glEnableVertexAttribArray(2)
    glEnableVertexAttribArray(3)
    glEnableVertexAttribArray(4)
    glEnableVertexAttribArray(5)
    glBindBuffer(GL_ARRAY_BUFFER, matrices.getId)
    glVertexAttribPointer(2, 4, GL_FLOAT, false, 16 << 2, 0)
    glVertexAttribPointer(3, 4, GL_FLOAT, false, 16 << 2, 4 << 2)
    glVertexAttribPointer(4, 4, GL_FLOAT, false, 16 << 2, 8 << 2)
    glVertexAttribPointer(5, 4, GL_FLOAT, false, 16 << 2, 12 << 2)

    glDrawArrays(GL_TRIANGLES, 0, vertices.getSize.toInt / 3)

    glDisableVertexAttribArray(vertexArrayId)
  }

  def addModel(model: SimpleVertexModel) {
    if (indices.contains(model)) {
      return
    }

    val index = SimpleVertexModelIndex(vertices.getSize, uvs.getSize, matrices.getSize)

    tryWithDestroyer(MemoryUtil.memAllocFloat(model.vertexData.length)) { vertBuf =>
      vertBuf.put(model.vertexData)
      vertBuf.flip()
      vertices.append(vertBuf)
    }(MemoryUtil.memFree)

    tryWithDestroyer(MemoryUtil.memAllocFloat(model.uvData.length)) { uvBuf =>
      uvBuf.put(model.uvData)
      uvBuf.flip()
      uvs.append(uvBuf)
    }(MemoryUtil.memFree)

    tryWithDestroyer(MemoryUtil.memAllocFloat(model.vertexData.length / 3 * 16)) { matBuf =>
      for (i <- 0 until model.vertexData.length / 3) {
        model.model.get(i * 16, matBuf)
      }
      matrices.append(matBuf)
    }(MemoryUtil.memFree)

    indices += ((model, index))
    indexList += index
  }

  def removeModel(model: SimpleVertexModel) {
    if (!indices.contains(model)) {
      return
    }

    val index = indices(model)
    val vertLen = model.vertexData.length << 2
    val uvLen = model.uvData.length << 2
    val matLen = 16 << 2

    vertices.remove(index.vertex, vertLen)
    uvs.remove(index.uv, uvLen)
    matrices.remove(index.matrix, matLen)

    indexList.slice(indexList.indexOf(index) + 1, indexList.size).foreach({ f =>
      f.vertex -= vertLen
      f.uv -= uvLen
      f.matrix -= matLen
    })

    indexList -= index
  }

  def clearModels() {
    vertices.clear()
    uvs.clear()
    matrices.clear()
    indices.clear()
    indexList.clear()
  }

  def destroy() {
    glDeleteVertexArrays(vertexArrayId)
    vertices.destroy()
    uvs.destroy()
    matrices.destroy()
    shaderProgram.delete()
    texture.delete()
  }
}
