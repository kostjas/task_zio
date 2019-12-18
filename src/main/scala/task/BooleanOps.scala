package task

final class BooleanOps(self: Boolean) {

  final class Conditional[+A](t: => A) {

    def ||[B >: A](s: Boolean)(f: => B): Conditional[B] = if (s) new Conditional(f) else this

    def |[B >: A](f: => B): B = if (self) t else f
  }

  def ?[T](t: => T): Conditional[T] = new Conditional(t)
}

object ToBooleanOps {
  implicit def ToBooleanOpsFromBoolean(a: Boolean): BooleanOps = new BooleanOps(a)
}
