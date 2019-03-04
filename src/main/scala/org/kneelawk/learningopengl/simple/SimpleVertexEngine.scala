package org.kneelawk.learningopengl.simple

import org.kneelawk.learningopengl.buffers.GLArrayBuffer
import org.kneelawk.learningopengl.shaders.{ShaderComponentSource, ShaderType, UnlinkedShaderProgram}
import org.kneelawk.learningopengl.util.ResourceUtils.{tryWith, tryWithDestroyer}
import org.kneelawk.learningopengl.{AbstractRenderEngine, Camera, GraphicsInterface, ResourceUtil, Window}
import org.lwjgl.opengl.GL30._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL11._
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
  private val matrices = new GLArrayBuffer
  private val indices = new mutable.HashMap[SimpleVertexModel, SimpleVertexModelIndex]
  private val indexList = new ListBuffer[SimpleVertexModelIndex]

  private val vertexShader = new ShaderComponentSource(getClass.getResourceAsStream("vertex.glsl"), "vertex.glsl", ShaderType.Vertex).compile()
  private val fragmentShader = new ShaderComponentSource(getClass.getResourceAsStream("fragment.glsl"), "fragment.glsl", ShaderType.Fragment).compile()
  private val shaderProgram = (new UnlinkedShaderProgram("shader") += vertexShader += fragmentShader).link()
  vertexShader.delete()
  fragmentShader.delete()

  private val mvpBuffer = MemoryUtil.memAllocFloat(16)
  private val mvpLocation = shaderProgram.getUniformLocation("MVP")

  def render() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)

    glUseProgram(shaderProgram.id)

    // bind vertex array for rendering
    glBindVertexArray(vertexArrayId)

    glUniformMatrix4fv(mvpLocation, false, camera.getMatrix.get(mvpBuffer))

    glEnableVertexAttribArray(0)
    glBindBuffer(GL_ARRAY_BUFFER, vertices.getId)
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)

    glDrawArrays(GL_TRIANGLES, 0, vertices.getSize.toInt / 3)

    glDisableVertexAttribArray(vertexArrayId)
  }

  def addModel(model: SimpleVertexModel) {
    if (indices.contains(model)) {
      return
    }

    val index = SimpleVertexModelIndex(vertices.getSize, matrices.getSize)

    tryWithDestroyer(MemoryUtil.memAllocFloat(model.vertexData.length)) { vertBuf =>
      vertBuf.put(model.vertexData)
      vertBuf.flip()
      vertices.append(vertBuf)
    }(MemoryUtil.memFree(_))

    tryWith(MemoryStack.stackPush()) { stack =>
      val matBuf = stack.mallocFloat(16)
      model.model.get(matBuf)
      matBuf.flip()
      matrices.append(matBuf)
    }

    indices += ((model, index))
    indexList += index
  }

  def removeModel(model: SimpleVertexModel) {
    if (!indices.contains(model)) {
      return
    }

    val index = indices(model)
    val vertLen = model.vertexData.length << 2
    val matLen = 16 << 2

    vertices.remove(index.vertex, vertLen)
    matrices.remove(index.matrix, matLen)

    indexList.slice(indexList.indexOf(index) + 1, indexList.size).foreach({ f =>
      f.vertex -= vertLen
      f.matrix -= matLen
    })

    indexList -= index
  }

  def clearModels() {
    vertices.clear()
    matrices.clear()
    indices.clear()
    indexList.clear()
  }

  def destroy() {
    glDeleteVertexArrays(vertexArrayId)
    vertices.destroy()
    matrices.destroy()
    shaderProgram.delete()
  }
}
