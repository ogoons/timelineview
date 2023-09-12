package com.ogoons.timelineview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

/**
 * Created by ogoons on 2017. 11. 20..
 */
class TimelineView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var marker: Drawable? = null
    private var markerSize: Int = 0
    private var markerMargin: Int = 0
    private var markerInCenter: Boolean = false
    private lateinit var markerBounds: Rect

    private var lineType: LineType
    private var lineOrientation: LineOrientation
    private var lineStyle: LineStyle
    private var lineWidth: Int = 0
    private var lineColor: Int = 0

    private var dashWidth: Int = 0
    private var dashGap: Int = 0

    private val linePaint = Paint()

    enum class LineType {
        BEGIN,
        MIDDLE,
        END,
        ONLY
    }

    enum class LineOrientation {
        HORIZONTAL,
        VERTICAL
    }

    enum class LineStyle {
        SOLID,
        DASHED,
    }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TimelineView)
        marker = typedArray.getDrawable(R.styleable.TimelineView_marker)
        markerSize = typedArray.getDimensionPixelSize(R.styleable.TimelineView_markerSize, Utils.dpToPx(context, DEFAULT_MARKER_SIZE_DP))
        markerMargin = typedArray.getDimensionPixelSize(R.styleable.TimelineView_markerMargin, Utils.dpToPx(context, DEFAULT_MARKER_MARGIN_DP))
        lineType = when (typedArray.getInt(R.styleable.TimelineView_lineType, DEFAULT_LINE_TYPE)) {
            0 -> LineType.BEGIN
            1 -> LineType.MIDDLE
            2 -> LineType.END
            3 -> LineType.ONLY
            else -> LineType.ONLY
        }
        lineOrientation = if (typedArray.getInt(R.styleable.TimelineView_lineOrientation, DEFAULT_LINE_ORIENTATION) == 0) LineOrientation.HORIZONTAL else LineOrientation.VERTICAL
        lineStyle = if (typedArray.getInt(R.styleable.TimelineView_lineStyle, DEFAULT_LINE_STYLE) == 0) LineStyle.SOLID else LineStyle.DASHED
        lineWidth = typedArray.getDimensionPixelSize(R.styleable.TimelineView_lineWidth, Utils.dpToPx(context, DEFAULT_LINE_HEIGHT_DP))
        lineColor = typedArray.getColor(R.styleable.TimelineView_lineColor, ContextCompat.getColor(context, android.R.color.darker_gray))
        dashWidth = typedArray.getDimensionPixelSize(R.styleable.TimelineView_dashWidth, Utils.dpToPx(context, DEFAULT_DASH_WIDTH_DP))
        dashGap = typedArray.getDimensionPixelSize(R.styleable.TimelineView_dashGap, Utils.dpToPx(context, DEFAULT_DASH_GAP_DP))
        markerInCenter = typedArray.getBoolean(R.styleable.TimelineView_markerInCenter, true)
        typedArray.recycle()

        marker = marker ?: ContextCompat.getDrawable(context, R.drawable.marker)
    }

    private fun setDrawable() {
        val circleWidth = width - (paddingLeft - paddingRight)
        val circleHeight = height - (paddingTop - paddingBottom)

        val markerSize = Math.min(markerSize, Math.min(circleWidth, circleHeight))

        /**
         * Set the marker
         */
        if (markerInCenter) {
            marker?.let {
                it.setBounds(width / 2 - markerSize / 2, height / 2 - markerSize / 2, width / 2 + markerSize / 2, height / 2 + markerSize / 2)
                markerBounds = it.bounds // for calculation
            }
        } else {
            marker?.let {
                it.setBounds(paddingLeft, paddingTop, paddingLeft + markerSize, paddingTop + markerSize)
                markerBounds = it.bounds // for calculation
            }
        }

        /**
         * Set the dashed line
         */

        if (lineStyle == LineStyle.DASHED) {
            linePaint.reset()
            linePaint.style = Paint.Style.FILL
            val dashedLinePath = Path()
            dashedLinePath.addCircle(0f, 0f, dashWidth / 2f, Path.Direction.CW)
            linePaint.pathEffect = PathDashPathEffect(dashedLinePath, dashWidth + dashGap.toFloat(), 0f, PathDashPathEffect.Style.ROTATE)
        }
        linePaint.isAntiAlias = true
        linePaint.color = lineColor

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }

        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        setDrawable()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = markerSize + paddingLeft + paddingRight
        val height = markerSize + paddingTop + paddingBottom

        val widthSize = View.resolveSizeAndState(width, widthMeasureSpec, 0)
        val heightSize = View.resolveSizeAndState(height, heightMeasureSpec, 0)

        setMeasuredDimension(widthSize, heightSize)

        setDrawable()
    }

    private fun drawMarker(canvas: Canvas) {
        marker?.draw(canvas)
    }

    private fun drawLine(canvas: Canvas) {
        when (lineOrientation) {
            LineOrientation.HORIZONTAL -> {
                val markerCenterY = markerBounds.exactCenterY()
                val lineTop = markerCenterY - (lineWidth shr 1)
                when (lineStyle) {
                    LineStyle.SOLID -> {
                        /**
                         * Draw rectangle
                         */
                        when (lineType) {
                            LineType.BEGIN -> {
                                // End line
                                canvas.drawRect(width.toFloat(), lineTop, markerBounds.right + markerMargin.toFloat(), lineWidth + lineTop, linePaint) // from right
                            }
                            LineType.MIDDLE -> {
                                // Start line
                                canvas.drawRect(0f, lineTop, markerBounds.left - markerMargin.toFloat(), lineWidth + lineTop, linePaint) // from left

                                // End line
                                canvas.drawRect(width.toFloat(), lineTop, markerBounds.right + markerMargin.toFloat(), lineWidth + lineTop, linePaint) // from right
                            }
                            LineType.END -> {
                                // Start line
                                canvas.drawRect(0f, lineTop, markerBounds.left - markerMargin.toFloat(), lineWidth + lineTop, linePaint) // from left
                            }
                            LineType.ONLY -> {}
                        }
                    }
                    LineStyle.DASHED -> {
                        /**
                         * Draw line
                         */
                        when (lineType) {
                            LineType.BEGIN -> {
                                // End line
                                canvas.drawLine(width.toFloat() - ((dashGap / 2) + (dashWidth / 2)), markerCenterY, markerBounds.right + markerMargin.toFloat(), markerCenterY, linePaint) // from right
                            }
                            LineType.MIDDLE -> {
                                // Start line
                                canvas.drawLine((dashGap / 2f) + (dashWidth / 2), markerCenterY, markerBounds.left - markerMargin.toFloat(), markerCenterY, linePaint) // from left

                                // End line
                                canvas.drawLine(width.toFloat() - ((dashGap / 2) + (dashWidth / 2)), markerCenterY, markerBounds.right + markerMargin.toFloat(), markerCenterY, linePaint) // from right
                            }
                            LineType.END -> {
                                // Start line
                                canvas.drawLine((dashGap / 2f) + (dashWidth / 2f), markerCenterY, markerBounds.left - markerMargin.toFloat(), markerCenterY, linePaint) // from left
                            }
                            LineType.ONLY -> {}
                        }
                    }
                }
            }
            LineOrientation.VERTICAL -> {
                val markerCenterX = markerBounds.exactCenterX()
                val lineLeft = markerCenterX - (lineWidth shr 1)
                when (lineStyle) {
                    LineStyle.SOLID -> {
                        /**
                         * Draw rectangle
                         */
                        when (lineType) {
                            LineType.BEGIN -> {
                                // End line
                                canvas.drawRect(lineLeft, height.toFloat(), lineWidth + lineLeft, markerBounds.bottom + markerMargin.toFloat(), linePaint) // from bottom
                            }
                            LineType.MIDDLE -> {
                                // Start line
                                canvas.drawRect(lineLeft, 0f, lineWidth + lineLeft, markerBounds.top - markerMargin.toFloat(), linePaint) // from top

                                // End linePaint
                                canvas.drawRect(lineLeft, height.toFloat(), lineWidth + lineLeft, markerBounds.bottom + markerMargin.toFloat(), linePaint) // from bottom
                            }
                            LineType.END -> {
                                // Start line
                                canvas.drawRect(lineLeft, 0f, lineWidth + lineLeft, markerBounds.top - markerMargin.toFloat(), linePaint) // from top
                            }
                            LineType.ONLY -> {}
                        }
                    }
                    LineStyle.DASHED -> {
                        /**
                         * Draw line
                         */
                        when (lineType) {
                            LineType.BEGIN -> {
                                // End line
                                canvas.drawLine(markerCenterX, height + ((dashGap / 2f) + (dashWidth / 2f)), markerCenterX, markerBounds.bottom + markerMargin.toFloat(), linePaint) // from bottom
                            }
                            LineType.MIDDLE -> {
                                // Start line
                                canvas.drawLine(markerCenterX, (dashGap / 2f) + (dashWidth / 2f), markerCenterX, markerBounds.top - markerMargin.toFloat(), linePaint) // from top

                                // End line
                                canvas.drawLine(markerCenterX, height + ((dashGap / 2f) + (dashWidth / 2f)), markerCenterX, markerBounds.bottom + markerMargin.toFloat(), linePaint) // from bottom
                            }
                            LineType.END -> {
                                // Start line
                                canvas.drawLine(markerCenterX, (dashGap / 2f) + (dashWidth / 2f), markerCenterX, markerBounds.top - markerMargin.toFloat(), linePaint) // from top
                            }
                            LineType.ONLY -> {}
                        }
                    }
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawMarker(canvas)
        drawLine(canvas)
    }

    /**
     * Get the marker.
     */
    fun getMarker() = this.marker

    /**
     * Set the marker.
     */
    fun setMarker(marker: Drawable) {
        this.marker = marker
        setDrawable()
    }

    /**
     * Set the marker with color filter.
     */
    fun setMarker(marker: Drawable, color: Int) {
        marker.setColorFilter(color, PorterDuff.Mode.SRC)
        this.marker = marker
        setDrawable()
    }

    /**
     * Set the marker color.
     */
    fun setMarkerColor(color: Int) {
        marker?.setColorFilter(color, PorterDuff.Mode.SRC)
        setDrawable()
    }

    /**
     * Set the marker size.
     */
    fun setMarkerSize(markerSize: Int) {
        this.markerSize = markerSize
        setDrawable()
    }

    /**
     * Set the marker margin.
     */
    fun setMarkerMargin(markerMargin: Int) {
        this.markerMargin = markerMargin
        setDrawable()
    }

    /**
     * Set the line type.
     */
    fun setLineType(lineType: LineType) {
        this.lineType = lineType
        invalidate()
    }

    /**
     * Set the line orientation.
     */
    fun setLineOrientation(lineOrientation: LineOrientation) {
        this.lineOrientation = lineOrientation
        setDrawable()
    }


    /**
     * Set the line style.
     */
    fun setLineStyle(lineStyle: LineStyle) {
        this.lineStyle = lineStyle
        setDrawable()
    }

    /**
     * Set the line size.
     */
    fun setLineWidth(lineWidth: Int) {
        this.lineWidth = lineWidth
        setDrawable()
    }

    /**
     * Set the dash Width
     */
    fun setDashWidth(dashWidth: Int) {
        this.dashWidth = dashWidth
        setDrawable()
    }

    /**
     * Set the dash gap
     */
    fun setDashGap(dashGap: Int) {
        this.dashGap = dashGap
        setDrawable()
    }

    companion object {
        // Default
        private const val DEFAULT_MARKER_SIZE_DP = 20
        private const val DEFAULT_MARKER_MARGIN_DP = 4
        private const val DEFAULT_LINE_HEIGHT_DP = 2
        private const val DEFAULT_LINE_TYPE = 3 // ONLY
        private const val DEFAULT_LINE_ORIENTATION = 0 // Horizontal
        private const val DEFAULT_LINE_STYLE = 0 // Line

        private const val DEFAULT_DASH_WIDTH_DP = 4
        private const val DEFAULT_DASH_GAP_DP = 4
    }
}