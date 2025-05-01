package com.example.budgettrackerapp.ui.theme.stats

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.ui.graphics.toArgb
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.example.budgettrackerapp.data.BudgetViewModel
import com.example.budgettrackerapp.data.Expense
import com.example.budgettrackerapp.data.User
import com.example.budgettrackerapp.widget.getCategoryIcon
import com.example.budgettrackerapp.widget.formatDate

@Composable
fun StatsScreen(navController: NavController, viewModel: BudgetViewModel, userId: Int)
{
    // Load data from the databases
    LaunchedEffect(Unit) {
        val userId = viewModel.loginResult.value?.userId ?: return@LaunchedEffect
        viewModel.loadExpenses(userId)
        viewModel.loadBudgetSettings()
    }


    val expenses = viewModel.expenses.collectAsState(emptyList()).value

    // Grouping the expenses by category and amount
    val grouped = expenses.groupBy { it.category }
    val categories = grouped.keys.toList()
    val values = grouped.values.map { list -> list.sumOf { it.amount }.toFloat() }
    val total = values.sum()

    // Setting a color for each expense category
    val themeColors = MaterialTheme.colorScheme
    val baseColors = listOf(
        themeColors.primary,
        themeColors.secondary,
        themeColors.tertiary,
        Color(0xFF81D4FA), // light blue
        Color(0xFFAED581), // light green
        Color(0xFFFF8A65), // orange
        Color(0xFFFFD54F), // yellow
        Color(0xFFBA68C8), // purple
        Color(0xFF4DB6AC), // teal
        Color(0xFF7986CB), // blue grey
        Color(0xFFE57373)  // red
    )
    val sliceColors = categories.mapIndexed { index, _ -> baseColors[index % baseColors.size].toArgb() }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Stats",
            style = MaterialTheme.typography.headlineSmall,
            color = themeColors.primary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Updating the pie chart from the database
        AndroidView(
            factory = { context ->
                PieChart(context).apply {
                    setUsePercentValues(true)
                    description.isEnabled = false
                    legend.isEnabled = false
                    setDrawEntryLabels(false)
                    setHoleRadius(0f)
                    setTransparentCircleRadius(0f)
                }
            },
            update = { chart: PieChart ->
                val entries = categories.mapIndexed { i, cat -> PieEntry(values[i], cat) }
                val dataSet = PieDataSet(entries, "").apply {
                    colors = sliceColors.toMutableList()
                    setDrawValues(false)
                    sliceSpace = 2f
                }
                chart.data = PieData(dataSet)
                chart.notifyDataSetChanged()
                chart.invalidate()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Creating a legend for the categories in the pie chart
        categories.zip(values).forEachIndexed { index, (category, value) ->
            val percentage = if (total > 0f) (value / total) * 100 else 0f
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(color = Color(sliceColors[index]))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$category - ${"%.1f".format(percentage)}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = themeColors.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Listing future Payments
        Text(
            text = "Payments",
            style = MaterialTheme.typography.titleLarge,
            color = themeColors.primary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            val upcoming = expenses.sortedBy { it.date }
            if (upcoming.isEmpty()) {
                Text(
                    text = "No upcoming payments",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = themeColors.onBackground
                )
            } else {
                upcoming.forEach { expense ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = getCategoryIcon(expense.category)),
                            contentDescription = expense.category,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = expense.category,
                                style = MaterialTheme.typography.bodyMedium,
                                color = themeColors.onBackground
                            )
                            Text(
                                text = "${expense.description} - R%.2f - Due: ${formatDate(expense.date)}".format(expense.amount),
                                style = MaterialTheme.typography.bodyLarge,
                                color = themeColors.onBackground
                            )
                        }
                    }
                }
            }
        }
    }
}
//
