//Reference: https://www.youtube.com/watch?v=LfHkAUzup5E
/// This file contains the code for the Transaction Screen which allows the users to
/// search for their transaction within a given time frame by using  a date picker
/// The user also has the option to search by name the category of the transactions.

package com.example.budgettrackerapp.widget

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
import com.example.budgettrackerapp.utils.DateUtils
import coil.compose.rememberAsyncImagePainter
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.net.toUri

@Composable
fun TransactionScreen(navController: NavController, viewModel: BudgetViewModel, userId: String) {
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (imageRef, nameRow, card, list) = createRefs()

            Image(
                painter = painterResource(id = R.drawable.toppage),
                contentDescription = null,
                modifier = Modifier.constrainAs(imageRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )

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
                Column {
                    ExpenseTextView("Transactions", fontSize = 24.sp, color = MaterialTheme.colorScheme.onPrimary)
                    ExpenseTextView("Search your Transactions", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                }
                Image(
                    painter = painterResource(id = R.drawable.bell),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            TransactionCardItem(
                modifier = Modifier.constrainAs(card) {
                    top.linkTo(nameRow.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                totalBalance = totalBalance,
                totalExpensesAmount = totalExpenses
            )

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
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp)
    ) {
        Box(Modifier.fillMaxWidth().weight(1f)) {
            Column(Modifier.align(Alignment.CenterStart)) {
                ExpenseTextView("Total Balance", fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimary)
                ExpenseTextView("R%.2f".format(totalBalance), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
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
            ExpenseTextView(title, fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimary)
        }
        ExpenseTextView(amount, fontSize = 20.sp, color = MaterialTheme.colorScheme.onPrimary)
    }
}

@Composable
fun TransactionList(modifier: Modifier, navController: NavController, expenses: List<Expense>, userId: String) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Transactions", "Upcoming Bills")

    // Search state
    var searchQuery by remember { mutableStateOf("") }

    // Date filter
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectingStartDate by remember { mutableStateOf(true) }

    // Context for date picker
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

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
            DateUtils.isDateInRange(expense.date, startDate, endDate)
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
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp))
                .padding(4.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .background(
                            if (selectedTab == index) MaterialTheme.colorScheme.surface else Color.Transparent,
                            RoundedCornerShape(20.dp)
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
                        color = if (selectedTab == index) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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
            placeholder = { Text("Search transactions", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        )

        // Date filter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date range picker
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
                    contentDescription = "Start Date",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (startDate.isEmpty()) "Start Date" else formatDisplayDate(startDate), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Date range picker
            OutlinedButton(
                onClick = {
                    selectingStartDate = false
                    showDatePicker = true
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                // Date range picker
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "End Date",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (endDate.isEmpty()) "End Date" else formatDisplayDate(endDate), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
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
                    tint = if (startDate.isNotEmpty() || endDate.isNotEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
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
                            color = MaterialTheme.colorScheme.primary,
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
                        ExpenseTextView("No transactions match your search", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.error)
                    } else {
                        ExpenseTextView("No transactions found", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.error)
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
                            color = MaterialTheme.colorScheme.error
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

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant
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
            ExpenseTextView(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            ExpenseTextView(text = date, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
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
            containerColor = MaterialTheme.colorScheme.surface
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
                    painter = rememberAsyncImagePainter(expense.photoUri.toUri()),
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
                    text = expense.description.ifBlank { expense.category },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                ExpenseTextView(
                    text = formatDisplayDate(expense.date),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            ExpenseTextView(
                text = "R%.2f".format(expense.amount),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

// Helper function to format dates for display
private fun formatDisplayDate(dateString: String): String {
    return DateUtils.formatForDisplay(dateString)
}

//---------------------------------------------------End_of_File-----------------------------------------------------------------------------------------