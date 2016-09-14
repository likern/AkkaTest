/**
  * Created by victor on 11.09.16.
  */
case class MergeVector[T](image: Vector[T], data: Vector[T],
                  video: Vector[T], page: Vector[T])
  extends Iterable[T] {
  override def iterator: Iterator[T] = new MergeIterator()

  class MergeIterator extends Iterator[T] {

    var counter = 0
    val imageIterator = image.iterator
    val dataIterator = data.iterator
    val videoIterator = video.iterator
    val pageIterator = page.iterator

    override def hasNext: Boolean = {
      imageIterator.hasNext || dataIterator.hasNext ||
        videoIterator.hasNext || pageIterator.hasNext
    }

    override def next(): T = {
      val elem = counter % 4 match {
        case 0 =>
          if (imageIterator.hasNext) imageIterator.next()
          else if (dataIterator.hasNext) {
            counter += 1
            dataIterator.next()
          }
          else if (videoIterator.hasNext) {
            counter += 1
            videoIterator.next()
          }
          else if (pageIterator.hasNext) {
            counter += 1
            pageIterator.next()
          }
          else {
            throw new NoSuchElementException("No more elements")
          }
        case 1 =>
          if (dataIterator.hasNext) dataIterator.next()
          else if (videoIterator.hasNext) {
            counter += 1
            videoIterator.next()
          }
          else if (pageIterator.hasNext) {
            counter += 1
            pageIterator.next()
          }
          else if (imageIterator.hasNext) {
            counter += 1
            imageIterator.next()
          }
          else {
            throw new NoSuchElementException("No more elements")
          }
        case 2 =>
          if (videoIterator.hasNext) videoIterator.next()
          else if (pageIterator.hasNext) {
            counter += 1
            pageIterator.next()
          }
          else if (imageIterator.hasNext) {
            counter += 1
            imageIterator.next()
          }
          else if (dataIterator.hasNext) {
            counter += 1
            dataIterator.next()
          }
          else {
            throw new NoSuchElementException("No more elements")
          }
        case 3 =>
          if (pageIterator.hasNext) pageIterator.next()
          else if (imageIterator.hasNext) {
            counter += 1
            imageIterator.next()
          }
          else if (dataIterator.hasNext) {
            counter += 1
            dataIterator.next()
          }
          else if (videoIterator.hasNext) {
            counter += 1
            videoIterator.next()
          }
          else {
            throw new NoSuchElementException("No more elements")
          }
      }
      counter += 1
      elem
    }
  }
}
