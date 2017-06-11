package io.connorwyatt.flashcards.views.graphs

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Cap.ROUND
import android.graphics.Paint.Join
import android.graphics.Paint.Style.FILL
import android.graphics.Paint.Style.STROKE
import android.graphics.Path
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import io.connorwyatt.flashcards.R

class LineGraph : View {

  var coordinates: List<Coordinate>? = null
    set(coordinates) {
      field = coordinates?.let { sortCoordinates(it) }
      invalidate()
    }

  var lineColor: Int? = null
    set(color) {
      field = color
      invalidate()
    }

  private val paint by lazy { Paint() }

  constructor(context: Context) : super(context) {
    init(null)
  }

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    init(attrs)
  }

  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    super(context, attrs, defStyleAttr) {
    init(attrs)
  }

  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
    super(context, attrs, defStyleAttr, defStyleRes) {
    init(attrs)
  }

  override fun onDraw(canvas: Canvas) {
    val coordinates = this.coordinates ?: return
    val lineColor = this.lineColor ?: return

    val linePath = getLinePath(coordinates, canvas.width, canvas.height)

    paint.color = lineColor
    paint.strokeCap = ROUND
    paint.strokeJoin = Join.ROUND
    paint.strokeWidth = 6f

    paint.alpha = 100
    paint.style = FILL

    canvas.drawPath(linePath, paint)

    paint.style = STROKE
    paint.alpha = 255

    canvas.drawPath(linePath, paint)
  }

  private fun init(attrs: AttributeSet?) {
    val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.LineGraph, 0, 0)

    try {
      val primaryColor = ContextCompat.getColor(context, R.color.colorPrimary)

      lineColor = typedArray.getColor(R.styleable.LineGraph_lineColor, primaryColor)
    } finally {
      typedArray.recycle()
    }
  }

  private fun sortCoordinates(coordinates: List<Coordinate>)
    = coordinates.sortedBy { it.x }

  /**
   * A method for getting the path of the line in the graph.
   *
   * It currently draws outside of the canvas after it draws the line so that it can close the path
   * properly for the fill. This is because if it is at the edge of the graph the stroke would be
   * partly visible.
   *
   * @param[coordinates] The list of coordinates to generate the line from. (It is assumed that they
   * are sorted correctly).
   * @param[width] The width of the area to generate the path relative to.
   * @param[height] The height of the area to generate the path relative to.
   *
   * @return The path representing the coordinates passed in.
   */
  private fun getLinePath(coordinates: List<Coordinate>, width: Int, height: Int): Path {
    val path = Path()

    val mappedCoordinates = coordinates
      .map { Coordinate(it.x, 1 - it.y) }
      .map { mapCoordinateToArea(it, width, height) }

    val firstCoordinate = mappedCoordinates.firstOrNull() ?: return path
    val remainingCoordinates = mappedCoordinates.drop(1)

    if (remainingCoordinates.isEmpty()) return path

    path.moveTo(firstCoordinate.x, firstCoordinate.y)

    remainingCoordinates
      .forEach { path.lineTo(it.x, it.y) }

    val lastCoordinate = remainingCoordinates.last()

    path.lineTo(lastCoordinate.x + 20f, lastCoordinate.y)
    path.lineTo(lastCoordinate.x + 20f, height + 20f)
    path.lineTo(firstCoordinate.x - 20f, height + 20f)
    path.lineTo(firstCoordinate.x - 20f, firstCoordinate.y)

    path.close()

    return path
  }

  private fun mapCoordinateToArea(coordinate: Coordinate, width: Int, height: Int)
    = Coordinate(x = coordinate.x * width, y = coordinate.y * height)

}
