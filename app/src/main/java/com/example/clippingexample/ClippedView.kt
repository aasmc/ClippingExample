package com.example.clippingexample

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View

class ClippedView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        isAntiAlias = true
        strokeWidth = resources.getDimension(R.dimen.strokeWidth)
        textSize = resources.getDimension(R.dimen.textSize)
    }

    /**
     * Path object to store locally what has been drawn
     */
    private val path = Path()

    /**
     * Variables for dimensions for clipping rectangle around the whole set of shapes
     */

    private val clipRectRight = resources.getDimension(R.dimen.clipRectRight)
    private val clipRectBottom = resources.getDimension(R.dimen.clipRectBottom)
    private val clipRectTop = resources.getDimension(R.dimen.clipRectTop)
    private val clipRectLeft = resources.getDimension(R.dimen.clipRectLeft)

    /**
     * Inset of a rectangle and offset of a small rectangle
     */
    private val rectInset = resources.getDimension(R.dimen.rectInset)
    private val smallRectOffset = resources.getDimension(R.dimen.smallRectOffset)

    /**
     * Radius of a circle
     */
    private val circleRadius = resources.getDimension(R.dimen.circleRadius)

    /**
     * Text offset and size
     */
    private val textOffset = resources.getDimension(R.dimen.textOffset)
    private val textSize = resources.getDimension(R.dimen.textSize)

    /**
     * Coordinates for the two columns
     */
    private val columnOne = rectInset
    private val columnTwo = columnOne + rectInset + clipRectRight

    /**
     * Coordinates for each row including the final row for the transformed text
     */
    private val rowOne = rectInset
    private val rowTwo = rowOne + rectInset + clipRectBottom
    private val rowThree = rowTwo + rectInset + clipRectBottom
    private val rowFour = rowThree + rectInset + clipRectBottom
    private val textRow = rowFour + (1.5f * clipRectBottom)

    private val rectF = RectF(
        rectInset,
        rectInset,
        clipRectRight - rectInset,
        clipRectBottom - rectInset
    )

    private val rejectRow = rowFour + rectInset + 2 * clipRectBottom

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackAndUnclippedRectangle(canvas)
        drawDifferenceClippingExample(canvas)
        drawCircularClippingExample(canvas)
        drawIntersectionClippingExample(canvas)
        drawCombinedClippingExample(canvas)
        drawRoundedRectangleClippingExample(canvas)
        drawOutsideClippingExample(canvas)
        drawSkewedTextExample(canvas)
        drawTranslatedTextExample(canvas)
        drawQuickRejectExample(canvas)
    }

    private fun drawClippedRectangle(canvas: Canvas) {
        // set the boundaries of a clipping rectangle fot the whole shape
        canvas.clipRect(clipRectLeft, clipRectTop, clipRectRight, clipRectBottom)
        // set the color of the canvas. Only the region inside the clipping rectangle will be filled!
        canvas.drawColor(Color.WHITE)

        // set the paint color to RED
        paint.color = Color.RED
        // draw the diagonal line from top left corner to bottom right corner
        canvas.drawLine(clipRectLeft, clipRectTop, clipRectRight, clipRectBottom, paint)

        // set the color to GREEN
        paint.color = Color.GREEN
        // draw circle inside the clipping rectangle
        canvas.drawCircle(circleRadius, clipRectBottom - circleRadius, circleRadius, paint)

        // set the color to blue
        paint.color = Color.BLUE
        // set the textSize
        paint.textSize = textSize
        // set the textAlign
        paint.textAlign = Paint.Align.RIGHT
        // draw text on the canvas
        canvas.drawText(context.getString(R.string.clipping), clipRectRight, textOffset, paint)
    }

    private fun drawQuickRejectExample(canvas: Canvas) {
        val inClipRectangle = RectF(
            clipRectRight / 2,
            clipRectBottom / 2,
            clipRectRight * 2,
            clipRectBottom * 2
        )

        val notInClippedRectangle = RectF(
            clipRectRight + 1,
            clipRectBottom + 1,
            clipRectRight * 2,
            clipRectBottom * 2
        )

        canvas.save()
        canvas.translate(columnOne, rejectRow)
        canvas.clipRect(clipRectLeft, clipRectTop, clipRectRight, clipRectBottom)
        if (canvas.quickReject(notInClippedRectangle, Canvas.EdgeType.AA)) {
            canvas.drawColor(Color.WHITE)
        }
         else {
            canvas.drawColor(Color.BLACK)
            canvas.drawRect(notInClippedRectangle, paint)
        }
        canvas.restore()
    }


    private fun drawSkewedTextExample(canvas: Canvas) {
        canvas.save()
        paint.color = Color.YELLOW
        paint.textAlign = Paint.Align.RIGHT

        canvas.translate(columnTwo, textRow)
        canvas.skew(0.4f, 0.3f)
        canvas.drawText(context.getString(R.string.skewed), clipRectLeft, clipRectTop, paint)
        canvas.restore()
    }

    private fun drawTranslatedTextExample(canvas: Canvas) {
        canvas.save()
        paint.color = Color.GREEN
        paint.textAlign = Paint.Align.LEFT

        canvas.translate(columnTwo, textRow)
        canvas.drawText(context.getString(R.string.translated), clipRectLeft, clipRectTop, paint)
        canvas.restore()
    }

    private fun drawOutsideClippingExample(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnOne, rowFour)
        canvas.clipRect(
            2 * rectInset, 2 * rectInset,
            clipRectRight - 2 * rectInset,
            clipRectBottom - 2 * rectInset
        )
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    private fun drawRoundedRectangleClippingExample(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnTwo, rowThree)
        path.rewind()
        path.addRoundRect(
            rectF, clipRectRight / 4,
            clipRectRight / 4, Path.Direction.CCW
        )
        canvas.clipPath(path)
        drawClippedRectangle(canvas)
        canvas.restore()

    }


    private fun drawCombinedClippingExample(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnOne, rowThree)
        path.rewind()
        path.addCircle(
            clipRectLeft + rectInset + circleRadius,
            clipRectTop + rectInset + circleRadius,
            circleRadius, Path.Direction.CCW
        )
        path.addRect(
            clipRectRight / 2 - circleRadius,
            clipRectTop + circleRadius + rectInset,
            clipRectRight / 2 + circleRadius,
            clipRectBottom - rectInset, Path.Direction.CCW
        )
        canvas.clipPath(path)
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    private fun drawIntersectionClippingExample(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnTwo, rowTwo)
        canvas.clipRect(
            clipRectLeft, clipRectTop,
            clipRectRight - smallRectOffset,
            clipRectBottom - smallRectOffset
        )

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            canvas.clipRect(
                clipRectLeft + smallRectOffset,
                clipRectTop + smallRectOffset,
                clipRectRight, clipRectBottom,
                Region.Op.INTERSECT
            )
        } else {
            canvas.clipRect(
                clipRectLeft + smallRectOffset,
                clipRectTop + smallRectOffset,
                clipRectRight,
                clipRectBottom
            )
        }
        drawClippedRectangle(canvas)
        canvas.restore()

    }

    private fun drawCircularClippingExample(canvas: Canvas) {
        canvas.save()

        canvas.translate(columnOne, rowTwo)
        // clears any lines and curves from the path but unlike the reset(),
        // keeps the internal data structure for faster reuse.
        path.rewind()

        path.addCircle(
            circleRadius, clipRectBottom - circleRadius,
            circleRadius, Path.Direction.CCW
        )
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            canvas.clipPath(path, Region.Op.DIFFERENCE)
        } else {
            canvas.clipOutPath(path)
        }
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    private fun drawDifferenceClippingExample(canvas: Canvas) {
        canvas.save()
        // translate the canvas to the free space
        canvas.translate(columnTwo, rowOne)
        // clip the rectangle from the canvas moving top left corner to the position of: 2* rectInset
        // bottom right corner to the position clipRectBottom/Right - 2 * rectInset
        // this enables us to draw a rectangle that is less than the origin by a fixed amount from left right top and bottom
        canvas.clipRect(
            2 * rectInset, 2 * rectInset,
            clipRectRight - 2 * rectInset,
            clipRectBottom - 2 * rectInset
        )

        // clips center from the original rectangle
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            canvas.clipRect(
                4 * rectInset, 4 * rectInset,
                clipRectRight - 4 * rectInset,
                clipRectBottom - 4 * rectInset, Region.Op.DIFFERENCE
            )
        } else {
            canvas.clipOutRect(
                4 * rectInset, 4 * rectInset,
                clipRectRight - 4 * rectInset,
                clipRectBottom - 4 * rectInset
            )
        }

        drawClippedRectangle(canvas)
        canvas.restore()

    }

    private fun drawBackAndUnclippedRectangle(canvas: Canvas) {
        canvas.drawColor(Color.GRAY)
        canvas.save()
        canvas.translate(columnOne, rowOne)
        drawClippedRectangle(canvas)
        canvas.restore()
    }
}
