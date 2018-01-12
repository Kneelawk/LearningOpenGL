package org.kneelawk.learningopengl

trait ModelRenderer[Model] {
  /**
   * Setup OpenGL stuff (vertex arrays, etc.)
   */
  def init()
  
  /**
   * Is this renderer initialized?
   */
  def initialized: Boolean
  
  /**
   * Called to render a batch of models.
   */
  def render(models: Iterable[AnyRef], camera: Camera)
}