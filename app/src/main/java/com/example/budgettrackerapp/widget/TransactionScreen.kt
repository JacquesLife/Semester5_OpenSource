package com.example.budgettrackerapp.widget

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.example.budgettrackerapp.R
import com.example.budgettrackerapp.data.BudgetViewModel
import com.example.budgettrackerapp.data.Expense
import com.example.budgettrackerapp.ui.theme.DarkBlue
import coil.compose.rememberAsyncImagePainter
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionScreen(navController: NavController, viewModel: BudgetViewModel, userId: Int) {
    val expenses by viewModel.expenses.collectAsState(initial = emptyList())
    val budgetSettings by viewModel.budgetSettings.collectAsState()

    // Load expenses and budget settings
    LaunchedEffect(Unit) {
        viewModel.loadExpenses(userId)
        viewModel.loadBudgetSettings()
    }

    val totalBalance = budgetSettings?.monthlyBudget ?: 0.0
    val totalExpenses = expenses.sumOf { it.amount }

    // Screen content
    Surface(modifier = Modifier.fillMaxSize()) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (imageRef, nameRow, card, list, fab) = createRefs()

            Image(
                painter = painterResource(id = R.drawable.toppage),
                contentDescription = null,
                modifier = Modifier.constrainAs(imageRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )
            // Name and actions
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 64.dp, start = 16.dp, end = 16.dp)
                    .constrainAs(nameRow) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                // Name and actions
                Column {
                    ExpenseTextView("Transactions", fontSize = 24.sp, color = Color.White)
                    ExpenseTextView(
                        "View your transaction history",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                // Bell icon
                Image(
                    painter = painterResource(id = R.drawable.bell),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            // Card item
            TransactionCardItem(
                modifier = Modifier.constrainAs(card) {
                    top.linkTo(nameRow.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                totalBalance = totalBalance,
                totalExpensesAmount = totalExpenses
            )

            // Transaction list
            TransactionList(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(list) {
                        top.linkTo(card.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        height = Dimension.fillToConstraints
                    },
                navController = navController,
                expenses = expenses,
                userId = userId
            )

        }
    }
}
@Composable
fun TransactionCardItem(modifier: Modifier, totalBalance: Double, totalExpensesAmount: Double) {
    Column(
        // Card item
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(DarkBlue)
            .padding(16.dp)
    ) {
        Box(Modifier.fillMaxWidth().weight(1f)) {
            Column(Modifier.align(Alignment.CenterStart)) {
                ExpenseTextView("Total Balance", fontSize = 16.sp, color = Color.White)
                ExpenseTextView("R%.2f".format(totalBalance), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Image(painter = painterResource(id = R.drawable.dotsmenue), contentDescription = null, modifier = Modifier.align(Alignment.CenterEnd))
        }
        Box(Modifier.fillMaxWidth().weight(1f)) {
            TransactionCardRowItem(Modifier.align(Alignment.CenterStart), "Total Expenses", "R%.2f".format(totalExpensesAmount), R.drawable.downarrow)
            TransactionCardRowItem(Modifier.align(Alignment.CenterEnd), "Remaining", "R%.2f".format(totalBalance - totalExpensesAmount), R.drawable.uparrow)
        }
    }
}

@Composable
fun TransactionCardRowItem(modifier: Modifier, title: String, amount: String, image: Int) {
    Column(modifier = modifier) {
        Row {
            Image(painter = painterResource(id = image), contentDescription = null)
            Spacer(Modifier.size(8.dp))
            ExpenseTextView(title, fontSize = 16.sp, color = Color.White)
        }
        ExpenseTextView(amount, fontSize = 20.sp, color = Color.White)
    }
}

@Composable
fun TransactionList(
    modifier: Modifier,
    navController: NavController,
    expenses: List<Expense>,
    userId: Int
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Transactions", "Upcoming Bills")

    // Search state
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    // Date filter
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectingStartDate by remember { mutableStateOf(true) }

    // Context for date picker
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    // Inside your TransactionList composable, modify the filteredExpenses code block:

    // Inside your TransactionList composable, modify the filteredExpenses code block:

    val filteredExpenses = expenses.filter { expense ->
        val matchesSearch = if (searchQuery.isBlank()) {
            true
        } else {
            expense.category.contains(searchQuery, ignoreCase = true) ||
                    expense.description.contains(searchQuery, ignoreCase = true)
        }

        val matchesDateRange = if (startDate.isBlank() || endDate.isBlank()) {
            true
        } else {
            try {
                // Define multiple date format parsers to handle different formats
                val possibleDateFormats = listOf(
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()),
                    SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()),
                    SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                )

                // Helper function to try parsing with multiple formats
                fun parseDate(dateStr: String): Date? {
                    for (format in possibleDateFormats) {
                        try {
                            return format.parse(dateStr.trim())
                        } catch (e: Exception) {
                            // Continue to next format
                        }
                    }
                    // If no format worked, log the failure
                    Log.e("DateFilter", "Failed to parse date: $dateStr")
                    return null
                }

                // For debugging
                Log.d("DateFilter", "Trying to parse - Expense date: ${expense.date}")
                Log.d("DateFilter", "Trying to parse - Start date: $startDate, End date: $endDate")

                // Parse dates with our multi-format parser
                val expenseDate = parseDate(expense.date)
                val start = parseDate(startDate)
                val end = parseDate(endDate)

                // Normalize times for comparison (if dates parsed successfully)
                val normalizedExpenseDate = expenseDate?.let {
                    Calendar.getInstance().apply {
                        time = it
                        set(Calendar.HOUR_OF_DAY, 12)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                    }.time
                }

                val normalizedStart = start?.let {
                    Calendar.getInstance().apply {
                        time = it
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                    }.time
                }

                val normalizedEnd = end?.let {
                    Calendar.getInstance().apply {
                        time = it
                        set(Calendar.HOUR_OF_DAY, 23)
                        set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 59)
                    }.time
                }

                // Debug
                Log.d("DateFilter", "Normalized - Expense date: ${normalizedExpenseDate}, Start: ${normalizedStart}, End: ${normalizedEnd}")

                // Make sure all dates are valid before comparing
                if (normalizedExpenseDate != null && normalizedStart != null && normalizedEnd != null) {
                    // Check if expense date is within range (inclusive of both start and end dates)
                    normalizedExpenseDate.compareTo(normalizedStart) >= 0 && normalizedExpenseDate.compareTo(normalizedEnd) <= 0
                } else {
                    Log.d("DateFilter", "Null date after parsing, skipping expense")
                    false
                }
            } catch (e: Exception) {
                Log.e("DateFilter", "Error filtering date: ${e.message}", e)
                false // If there's an error, don't include this expense
            }
        }

        matchesSearch && matchesDateRange
    }

    val groupedExpenses = filteredExpenses.groupBy { it.category }
    val expandedCategories = remember { mutableStateMapOf<String, Boolean>() }

    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        // Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .background(
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(4.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .background(
                            color = if (selectedTab == index) Color.White else Color.Transparent,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable {
                            selectedTab = index
                            if (index == 1) {
                                navController.navigate("upcoming_bills/$userId")
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    ExpenseTextView(
                        text = title,
                        fontSize = 16.sp,
                        color = if (selectedTab == index) Color.Black else Color.Gray
                    )
                }
            }
        }

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .focusRequester(focusRequester),
            placeholder = { Text("Search transactions") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search"
                        )
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            shape = RoundedCornerShape(8.dp)
        )

        // Date filter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = {
                    selectingStartDate = true
                    showDatePicker = true
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Start Date"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (startDate.isEmpty()) "Start Date" else formatDisplayDate(startDate))
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedButton(
                onClick = {
                    selectingStartDate = false
                    showDatePicker = true
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "End Date"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (endDate.isEmpty()) "End Date" else formatDisplayDate(endDate))
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    startDate = ""
                    endDate = ""
                },
                modifier = Modifier.clip(RoundedCornerShape(8.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear dates",
                    tint = if (startDate.isNotEmpty() || endDate.isNotEmpty()) Color.Red else Color.Gray
                )
            }
        }

        // Date picker dialog
        if (showDatePicker) {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            android.app.DatePickerDialog(
                context,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    if (selectingStartDate) {
                        startDate = dateFormat.format(selectedCalendar.time)
                    } else {
                        endDate = dateFormat.format(selectedCalendar.time)
                    }
                    showDatePicker = false
                },
                year,
                month,
                day
            ).apply {
                show()
            }
        }

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            // Header with actions
            Box(modifier = Modifier.fillMaxWidth()) {
                ExpenseTextView(
                    text = if (filteredExpenses.isEmpty()) "No Transactions Found" else "Transactions (${filteredExpenses.size})",
                    fontSize = 20.sp
                )

                Row(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (groupedExpenses.isNotEmpty()) {
                        val anyExpanded = expandedCategories.any { it.value }
                        ExpenseTextView(
                            text = if (anyExpanded) "Collapse All" else "Expand All",
                            fontSize = 14.sp,
                            color = DarkBlue,
                            modifier = Modifier.clickable {
                                val newState = !anyExpanded
                                groupedExpenses.keys.forEach { expandedCategories[it] = newState }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Show transactions grouped by category
            if (groupedExpenses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (searchQuery.isNotEmpty() || startDate.isNotEmpty() || endDate.isNotEmpty()) {
                        ExpenseTextView("No transactions match your search", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    } else {
                        ExpenseTextView("No transactions found", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
            } else {
                groupedExpenses.forEach { (category, items) ->
                    val expanded = expandedCategories[category] ?: false
                    val total = items.sumOf { it.amount }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedCategories[category] = !expanded }
                            .padding(vertical = 8.dp)
                    ) {
                        TransactionItem(
                            title = category,
                            amount = "R%.2f".format(total),
                            icon = getCategoryIcon(category),
                            date = "${items.size} transaction${if (items.size > 1) "s" else ""}",
                            color = Color.Red
                        )

                        if (expanded) {
                            items.forEach { expense ->
                                Spacer(modifier = Modifier.height(4.dp))
                                TransactionDetailItem(
                                    expense = expense,
                                    icon = getCategoryIcon(expense.category)
                                )
                            }
                        }
                    }

                    Divider(
                        color = Color.LightGray,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            // Add some space at the bottom for the FAB
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun TransactionItem(
    title: String,
    amount: String,
    icon: Int,
    date: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            ExpenseTextView(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            ExpenseTextView(text = date, fontSize = 12.sp, color = Color.Gray)
        }
        ExpenseTextView(
            text = amount,
            fontSize = 20.sp,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun TransactionDetailItem(
    expense: Expense,
    icon: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F8F8)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Show receipt image if available, otherwise show category icon
            if (expense.photoUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(Uri.parse(expense.photoUri)),
                    contentDescription = "Receipt",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                ExpenseTextView(
                    text = if (expense.description.isNotBlank()) expense.description else expense.category,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                ExpenseTextView(
                    text = formatDisplayDate(expense.date),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            ExpenseTextView(
                text = "R%.2f".format(expense.amount),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Red
            )
        }
    }
}

// Helper function to format dates for display
private fun formatDisplayDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        outputFormat.format(inputFormat.parse(dateString)!!)
    } catch (e: Exception) {
        dateString
    }
}

// You don't need to modify getCategoryIcon since it's already defined in your UpcomingBills.kt