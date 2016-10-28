package net.flatmap.vscode.languageserver

/**
  * Created by martin on 26.10.16.
  */
object Util {
  val lineBreak = "\\r?\\n".r

  def unfold[A,B](seed: B)(f: B => Option[(A,B)]): Iterable[A] = new Iterable[A] {
    def iterator: Iterator[A] = new Iterator[A] {
      private var done = false
      private var state = seed
      private var nextComputed = Option.empty[(A, B)]

      def hasNext: Boolean = !done && (nextComputed.isDefined || {
        nextComputed = f(state)
        nextComputed.foreach(x => state = x._2)
        done = !nextComputed.isDefined
        !done
      })

      def next(): A = if (hasNext) {
        val res = nextComputed.get._1
        nextComputed = None
        res
      } else
        throw new IndexOutOfBoundsException()
    }
  }
}
