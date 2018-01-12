package org.kneelawk.learningopengl

trait ModelRenderer[Model] {
  /**
   * Called to render a batch of models.
   */
  def render(models: Iterable[AnyRef], camera: Camera)
  
  /**
   * Release OpenGL stuff (vertex arrays, etc.)
   */
  def destroy()
}