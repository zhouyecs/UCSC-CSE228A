package hw4


object MatMulModel {
  type Matrix = Seq[Seq[Int]]

  def apply(p: MatMulParams, a: Matrix, b: Matrix): Matrix = {
    assert(a.size == p.aRows)
    assert(a.head.size == p.aCols)
    assert(b.size == p.bRows)
    assert(b.head.size == p.bCols)

    // BEGIN SOLUTION
    // Seq is immutable, so convert from mutable Array
    val c = Array.fill(p.cRows, p.cCols)(0)
    for (i <- 0 until p.cRows) {
      for (j <- 0 until p.cCols) {
        for (k <- 0 until p.aCols) {
          c(i)(j) += a(i)(k) * b(k)(j)
        }
      }
    }
    // Convert array to Matrix and return
    c.map(_.toSeq).toSeq
  }
}
