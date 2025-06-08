/// References: https://www.youtube.com/watch?v=HGsVBqUrnGY
/// References: https://youtu.be/cJxo96eTHVU?si=Id_53lmEb-Vo87IR
/// This page will graphically display the user's stats and upcoming payments
/// it will display them neatly with a pie chart

package com.example.budgettrackerapp.ui.theme.stats

import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.example.budgettrackerapp.data.BudgetViewModel
import com.example.budgettrackerapp.widget.getCategoryIcon
import com.example.budgettrackerapp.widget.formatDate
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StatsScreen(navController: NavController, viewModel: BudgetViewModel, userId: String) {
    // Load data from the databases
    LaunchedEffect(Unit) {
        viewModel.loadExpenses(userId)
        viewModel.loadBudgetSettings()
    }

    val allExpenses = viewModel.expenses.collectAsState(emptyList()).value

    val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val expenses = allExpenses.filter { expense ->
        val expenseDate = LocalDate.parse(expense.date, dateFormatter)
        (startDate == null || !expenseDate.isBefore(startDate)) &&
                (endDate == null || !expenseDate.isAfter(endDate))
    }

    // Grouping the expenses by category and amount
    //https://medium.com/@paritasampa95/piechart-in-android-jetpack-compose-5e7642c9f955
    //https://www.geeksforgeeks.org/pie-chart-in-android-using-jetpack-compose/
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
    val sliceColors = List(categories.size) { index -> baseColors[index % baseColors.size].toArgb() }

    val scrollState = rememberScrollState()

    // Stats screen content
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

        Spacer(modifier = Modifier.height(12.dp))

        // Date range filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "Start Date", color = themeColors.primary)
                Button(
                    onClick = { showStartDatePicker = true },
                    colors = ButtonDefaults.buttonColors(containerColor = themeColors.primary),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = startDate?.toString() ?: "Select Start Date",
                        color = themeColors.onPrimary
                    )
                }
            }
            Column {
                Text(text = "End Date", color = themeColors.primary)
                Button(
                    onClick = { showEndDatePicker = true },
                    colors = ButtonDefaults.buttonColors(containerColor = themeColors.primary),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = endDate?.toString() ?: "Select End Date",
                        color = themeColors.onPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Button to clear the date range
        Button(onClick = {
            startDate = null
            endDate = null
        }) {
            Text("Clear Date Filter")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Updating the pie chart from the database
        AndroidView(
            factory = { context ->
                PieChart(context).apply {
                    setUsePercentValues(true)
                    description.isEnabled = false
                    legend.isEnabled = false
                    setDrawEntryLabels(false)
                    holeRadius = 0f
                    transparentCircleRadius = 0f
                }
            },

            // Updating the pie chart with the new data
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

        // Displaying the upcoming payments
        Column(modifier = Modifier.fillMaxWidth()) {
            val upcoming = expenses.sortedBy { it.date }

            // If there are no upcoming payments, display a message
            if (upcoming.isEmpty()) {
                Text(
                    text = "No upcoming payments",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = themeColors.onBackground
                )
                // If there are upcoming payments, display them
            } else {
                upcoming.forEach { expense ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        // Displaying the category icon and expense details
                        Image(
                            painter = painterResource(id = getCategoryIcon(expense.category)),
                            contentDescription = expense.category,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            // Displaying the category and expense details
                            Text(
                                text = expense.category,
                                style = MaterialTheme.typography.bodyMedium,
                                color = themeColors.onBackground
                            )
                            // Displaying the expense details with proper string formatting
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

    // Show the date pickers after the UI
    if (showStartDatePicker) {
        ShowDatePicker { selectedDate ->
            startDate = selectedDate
            showStartDatePicker = false
        }
    }
    if (showEndDatePicker) {
        ShowDatePicker { selectedDate ->
            endDate = selectedDate
            showEndDatePicker = false
        }
    }
}

/// Methods to filter expenses based on date
/// https://youtu.be/cJxo96eTHVU?si=Id_53lmEb-Vo87IR
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShowDatePicker(onDateSelected: (LocalDate) -> Unit) {
    val context = LocalContext.current
    val today = LocalDate.now()
    val year = today.year
    val month = today.monthValue - 1
    val day = today.dayOfMonth

    DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
            onDateSelected(selectedDate)
        },
        year, month, day
    ).show()
}