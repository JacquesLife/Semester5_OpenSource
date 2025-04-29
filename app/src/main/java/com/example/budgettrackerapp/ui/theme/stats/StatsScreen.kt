package com.example.budgettrackerapp.ui.theme.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import androidx.navigation.NavController



@Composable
fun StatsScreen(navController: NavController) {
    val categories = listOf(
        "Food", "Transportation", "Entertainment", "Rent", "Other",
        "Shopping", "Healthcare", "Phone and Internet", "Utilities", "Saving", "Investment"
    )

    val values = listOf(
        200f, 150f, 100f, 600f, 50f,
        300f, 250f, 100f, 150f, 400f, 350f
    )

    val total = values.sum()
    val colors = ColorTemplate.MATERIAL_COLORS.toList()

    val upcomingPayments = listOf(
        "Rent - R 1200 - Due: 01 May 2024",
        "Electricity - R 350 - Due: 05 May 2024",
        "Internet - R 500 - Due: 10 May 2024",
        "Insurance - R 800 - Due: 15 May 2024"
    )

    val scrollState = rememberScrollState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text("Stats Screen", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(20.dp))

        // Pie Chart Composable
        AndroidView(
            factory = { context ->
                PieChart(context).apply {
                    val entries = categories.zip(values).map {
                        PieEntry(it.second, it.first)
                    }

                    val dataSet = PieDataSet(entries, "")
                    dataSet.setColors(colors)
                    dataSet.setDrawValues(false) // Don't show % inside pie
                    dataSet.sliceSpace = 2f

                    val pieData = PieData(dataSet)

                    this.data = pieData
                    this.setUsePercentValues(true)
                    this.description.isEnabled = false
                    this.legend.isEnabled = false
                    this.setDrawEntryLabels(false)
                    this.setHoleRadius(0f)
                    this.setTransparentCircleRadius(0f)
                    this.invalidate()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        categories.zip(values).forEachIndexed { index, (category, value) ->
            val percentage = (value / total) * 100
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(Color(colors[index % colors.size]))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$category - ${"%.1f".format(percentage)}%",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Upcoming Payments Section
        Text("Upcoming Payments", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(10.dp))

        // List of upcoming payments
        Column(modifier = Modifier.fillMaxWidth()) {
            upcomingPayments.forEach { payment ->
                Text(
                    text = payment,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}
