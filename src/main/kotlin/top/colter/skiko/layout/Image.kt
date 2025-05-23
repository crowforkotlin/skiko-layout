package top.colter.skiko.layout

import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Image
import top.colter.skiko.*
import top.colter.skiko.data.LayoutAlignment
import top.colter.skiko.data.place
import top.colter.skiko.data.Ratio


/**
 * ## 图片元素
 *
 * [ratio] 宽高比 例: 16f/10f。为 0 时表示保持图片原比例。可使用 [Ratio] 对象内置的比例
 *
 * @param image 图片
 * @param ratio 宽高比
 * @param modifier 样式
 * @param alignment 对齐
 */
public fun Layout.Image(
    image: Image,
    ratio: Float = 0f,
    modifier: Modifier = Modifier(),
    alignment: LayoutAlignment = LayoutAlignment.DEFAULT
) {
    Layout(
        layout = ImageLayout(
            image = image,
            ratio = ratio,
            alignment = alignment,
            modifier = modifier,
            parentLayout = this
        ),
        content = {},
    )
}

public class ImageLayout(
    public val image: Image,
    public val ratio: Float,
    public val alignment: LayoutAlignment,
    modifier: Modifier,
    parentLayout: Layout
) : Layout(modifier, parentLayout) {

    override fun measure(deep: Boolean) {
        // 第一遍计算宽高
        preMeasure()

        // 计算图片宽高
        val w = if (width.isNotNull()) width
        else if (!modifier.fillWidth && !modifier.fillMaxWidth) parentLayout!!.modifier.contentWidth
        else 0.dp

        if (w.isNotNull() && height.isNull()) {
            height = if (ratio != 0f) w / ratio else (image.height * w.px / image.width).toDp()
            if (modifier.maxHeight.isNotNull() && height > modifier.maxHeight) height = modifier.maxHeight
            if (width.isNull()) width = w
        }

        val h = if (height.isNotNull()) height
        else if (!modifier.fillHeight && !modifier.fillMaxHeight) parentLayout!!.modifier.contentHeight
        else 0.dp

        if (h.isNotNull() && width.isNull()) {
            width = if (ratio != 0f) h * ratio else (image.width * h.px / image.height).toDp()
            if (modifier.maxWidth.isNotNull() && width > modifier.maxWidth) width = modifier.maxWidth
            if (height.isNull()) height = h
        }

    }

    override fun place(bounds: LayoutBounds) {
        // 确定当前元素位置
        position = alignment.place(width, height, modifier, bounds)
    }

    override fun draw(canvas: Canvas) {
        // 绘制当前元素
        drawBgBox(canvas) {
            // 绘制图片
            drawImageClip(image, it)
        }
    }

}
