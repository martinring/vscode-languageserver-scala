package net.flatmap.vscode.languageserver

/**
  * A range represents an ordered pair of two positions.
  * It is guaranteed that [start](#Range.start).isBeforeOrEqual([end](#Range.end))
  *
  * Range objects are __immutable__. Use the [with](#Range.with),
  * [intersection](#Range.intersection), or [union](#Range.union) methods
  * to derive new ranges from an existing range.
  *
  * @param start The start position. It is before or equal to [end](#Range.end).
  * @param end   The end position. It is after or equal to [start](#Range.start).
  */
class Range private [languageserver] (val start: Position, val end: Position) {
  /**
    * `true` iff `start` and `end` are equal.
    */
  def isEmpty = start == end

  /**
    * `true` iff `start.line` and `end.line` are equal.
    */
  def isSingleLine = start.line == end.line

  /**
    * Check if a position is contained in this range.
    *
    * @param position A position.
    * @return `true` iff the position is inside or equal
    * to this range.
    */
  def contains(position: Position): Boolean =
  start <= position && position <= end

  /**
    * Check if a position or a range is contained in this range.
    *
    * @param range A range.
    * @return `true` iff the range is inside or equal
    * to this range.
    */
  def contains(range: Range): Boolean =
  start <= range.start && range.end <= end

  /**
    * Intersect `range` with this range and returns a new range or None
    * if the ranges have no overlap.
    *
    * @param other A range.
    * @return A range of the greater start and smaller end positions. Will
    * return None when there is no overlap.
    *
    * xxxxxx______
    * _______xxxxx
    */
  def intersection(other: Range): Option[Range] = {
    val start = Position.ordering.max(this.start,other.start)
    val end   = Position.ordering.min(this.start,other.start)
    if (start <= end) Some(Range(start,end)) else None
  }

  /**
    * Compute the union of `other` with this range.
    *
    * @param other A range.
    * @return A range of smaller start position and the greater end position.
    */
  def union(other: Range): Range = {
    val start = Position.ordering.min(this.start,other.start)
    val end   = Position.ordering.max(this.start,other.start)
    Range(start,end)
  }

  override def equals(other: Any): Boolean = other match {
    case Range(start,end) => start == this.start && end == this.end
  }

  /**
    * Derived a new range from this range.
    *
    * @param start A position that should be used as start. The default value is the [current start](#Range.start).
    * @param end A position that should be used as end. The default value is the [current end](#Range.end).
    * @return A range derived from this range with the given start and end position.
    * If start and end are not different `this` range will be returned.
    */
  def copy(start: Position = this.start, end: Position = this.end) =
  Range(start,end)
}

object Range {
  /**
    * Create a new range from two positions. If `start` is not
    * before or equal to `end`, the values will be swapped.
    *
    * @param start A position.
    * @param end A position.
    */
  def apply(start: Position, end: Position): Range =
    if (start <= end) new Range(start,end)
    else new Range(end,start)

  def unapply(range: Range): Option[(Position,Position)] =
    Some((range.start,range.end))

  /**
    * Create a new range from number coordinates. It is a shorter equivalent of
    * using `new Range(new Position(startLine, startCharacter), new Position(endLine, endCharacter))`
    *
    * @param startLine A zero-based line value.
    * @param startCharacter A zero-based character value.
    * @param endLine A zero-based line value.
    * @param endCharacter A zero-based character value.
    */
  def apply(startLine: Int, startCharacter: Int, endLine: Int, endCharacter: Int): Range =
  Range(Position(startLine,startCharacter),Position(endLine,endCharacter))
}