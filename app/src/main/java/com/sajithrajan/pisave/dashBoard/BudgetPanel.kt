package com.sajithrajan.pisave.dashBoard

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun BudgetPanel(
    modifier: Modifier = Modifier,
    todaySpending: Double = 0.0,
    dailyBudget: Double = 0.0,
    currency: String = "₹",
    monthlySpending: Double = 0.0,
    monthBudget: Double = 0.0,
) {
    ElevatedCard() {
        Row {
            BudgetCard(
                modifier = Modifier.weight(1f), used = todaySpending, total = dailyBudget
            )
            Spacer(modifier = Modifier.width(10.dp))
            BudgetCard(
                modifier = Modifier.weight(1f), used = monthlySpending, total = monthBudget
            )
        }
    }

}

@Composable
fun BudgetCard(
    modifier: Modifier = Modifier,
    used: Double,
    total: Double,
) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Text(
                text = "Today",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 20.sp)
            )


            CustomCircularProgressIndicator(

                modifier = Modifier.size(100.dp), used = used, total = total


            )
            Text(
                text = String.format("%d",total.roundToInt()),
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 30.sp),
                modifier = Modifier.align(Alignment.End)
            )

        }
    }
}


@Composable
fun CustomCircularProgressIndicator(
    modifier: Modifier = Modifier,
    used: Double,
    primaryColor: Color = MaterialTheme.colorScheme.onPrimary,
    secondaryColor: Color = MaterialTheme.colorScheme.inverseSurface,
    minValue: Int = 0,
    total: Double = 100.0,
    circleRadius: Float = 80f,

    ) {
    var circleCenter by remember {
        mutableStateOf(Offset.Zero)
    }

    var positionValue by remember {
        mutableStateOf(used.roundToInt())
    }



    Box(
        modifier = modifier
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val width = size.width
            val height = size.height
            val circleThickness = width / 25f
            circleCenter = Offset(x = width / 2f, y = height / 2f)


            drawCircle(
                brush = Brush.radialGradient(
                    listOf(
                        primaryColor.copy(0.45f), secondaryColor.copy(0.15f)
                    )
                ), radius = circleRadius, center = circleCenter
            )


            drawCircle(
                style = Stroke(
                    width = circleThickness
                ), color = secondaryColor, radius = circleRadius, center = circleCenter
            )

            drawArc(
                color = primaryColor,
                startAngle = 90f,
                sweepAngle = (360f / total.toFloat()) * positionValue.toFloat(),
                style = Stroke(
                    width = circleThickness, cap = StrokeCap.Round
                ),
                useCenter = false,
                size = Size(
                    width = circleRadius * 2f, height = circleRadius * 2f
                ),
                topLeft = Offset(
                    (width - circleRadius * 2f) / 2f, (height - circleRadius * 2f) / 2f
                )

            )

            val outerRadius = circleRadius + circleThickness / 2f
            val gap = 5f
            for (i in 0..(total.toInt() - minValue)) {
                val color =
                    if (i < positionValue - minValue) primaryColor else primaryColor.copy(alpha = 0.3f)
                val angleInDegrees = i * 360f / (50 - minValue).toFloat()
                val angleInRad = angleInDegrees * PI / 180f + PI / 2f

                val yGapAdjustment = cos(angleInDegrees * PI / 180f) * gap
                val xGapAdjustment = -sin(angleInDegrees * PI / 180f) * gap

                val start = Offset(
                    x = (outerRadius * cos(angleInRad) + circleCenter.x + xGapAdjustment).toFloat(),
                    y = (outerRadius * sin(angleInRad) + circleCenter.y + yGapAdjustment).toFloat()
                )

                val end = Offset(
                    x = (outerRadius * cos(angleInRad) + circleCenter.x + xGapAdjustment).toFloat(),
                    y = (outerRadius * sin(angleInRad) + circleThickness + circleCenter.y + yGapAdjustment).toFloat()
                )

                rotate(
                    angleInDegrees, pivot = start
                ) {
                    drawLine(
                        color = color, start = start, end = end, strokeWidth = 1.dp.toPx()
                    )
                }

            }

            drawContext.canvas.nativeCanvas.apply {
                drawIntoCanvas {
                    drawText("$positionValue",
                        circleCenter.x,
                        circleCenter.y + 30.dp.toPx() / 3f,
                        Paint().apply {
                            textSize = 30.sp.toPx()
                            textAlign = Paint.Align.CENTER
                            color = 0xFFFFFFFF.toInt()
                            isFakeBoldText = true
                        })
                }
            }

        }
    }
}





@Composable
fun BudgetDialog(
    dailyBudgetSliderValue: Float,
    monthlyBudgetSliderValue: Float,
    onDailyBudgetChange: (Float) -> Unit,
    onMonthlyBudgetChange: (Float) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onSave) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Set Monthly Budget") },
        text = {
            Column {
                Text(text = "Daily Budget: ₹${dailyBudgetSliderValue.toInt()}", modifier = Modifier.padding(bottom = 8.dp))
                Slider(
                    value = dailyBudgetSliderValue,
                    onValueChange = onDailyBudgetChange,
                    valueRange = 100f..5000f,
                    steps = 49,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Monthly Budget: ₹${monthlyBudgetSliderValue.toInt()}", modifier = Modifier.padding(bottom = 8.dp))
                Slider(
                    value = monthlyBudgetSliderValue,
                    onValueChange = onMonthlyBudgetChange,
                    valueRange = 1000f..100000f,
                    steps = 99,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}





