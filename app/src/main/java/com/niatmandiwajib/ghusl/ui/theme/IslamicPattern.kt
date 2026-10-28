package com.niatmandiwajib.ghusl.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * High-performance GPU-rendered Islamic Geometric Octagram Pattern (Khatam lattice).
 * Provides an authentic, tranquil background motif without heavy bitmap memory overhead.
 */
@Composable
fun IslamicPatternBackground(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF2E7D5F),
    alpha: Float = 0.035f,
    cellSize: Float = 160f
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val strokeWidth = 1.dp.toPx()
        val drawColor = color.copy(alpha = alpha)

        val cols = (size.width / cellSize).toInt() + 2
        val rows = (size.height / cellSize).toInt() + 2

        for (i in -1..cols) {
            for (j in -1..rows) {
                val cx = i * cellSize + (if (j % 2 == 0) 0f else cellSize / 2f)
                val cy = j * (cellSize * 0.866f)

                val r1 = cellSize * 0.26f
                val r2 = cellSize * 0.15f

                // Draw 8-pointed star (octagram)
                val path = Path()
                for (k in 0 until 16) {
                    val angle = Math.toRadians((k * 22.5).toDouble())
                    val r = if (k % 2 == 0) r1 else r2
                    val x = (cx + r * Math.cos(angle)).toFloat()
                    val y = (cy + r * Math.sin(angle)).toFloat()
                    if (k == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()

                drawPath(path, color = drawColor, style = Stroke(width = strokeWidth))

                // Interconnecting subtle diagonal lines between centers
                drawLine(
                    color = drawColor,
                    start = Offset(cx - r1, cy),
                    end = Offset(cx + r1, cy),
                    strokeWidth = strokeWidth * 0.6f
                )
                drawLine(
                    color = drawColor,
                    start = Offset(cx, cy - r1),
                    end = Offset(cx, cy + r1),
                    strokeWidth = strokeWidth * 0.6f
                )
            }
        }
    }
}
