/// Reference: //Reference: https://www.youtube.com/watch?v=LfHkAUzup5E
/// This file is similar to the transaction file it displays a list of upcoming bills and categories
/// Users will be able to view their total budget and their upcoming bills

package com.example.budgettrackerapp.widget

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.budgettrackerapp.R
import com.example.budgettrackerapp.data.BudgetViewModel
import com.example.budgettrackerapp.data.Expense
import com.example.budgettrackerapp.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.net.toUri

@Composable
fun UpcomingBillsScreen(navController: NavController, viewModel: BudgetViewModel = androidx.lifecycle.viewmodel.compose.viewModel(), userId: String) {
    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
        Calendar.getInstance().apply { add(Calendar.MONTH, 1) }.time
    )

    LaunchedEffect(Unit) {
        viewModel.loadExpenses(userId)
        viewModel.loadBudgetSettings()
    }

    val expenses by viewModel.expenses.collectAsState(emptyList())
    val budgetSettings by viewModel.budgetSettings.collectAsState()

    val totalBalance = budgetSettings?.monthlyBudget ?: 0.0
    val upcomingBillsAmount = expenses.filter { it.notificationEnabled || it.isRecurring }.sumOf { it.amount }
    val paidBillsAmount = 0.0

    Surface(modifier = Modifier.fillMaxSize()) {
        ConstraintLayout {
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
                    ExpenseTextView("Upcoming Bills", fontSize = 24.sp, color = MaterialTheme.colorScheme.onPrimary)
                    ExpenseTextView("Search your Transactions", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                }
                Image(
                    painter = painterResource(id = R.drawable.bell),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            BillCardItem(
                modifier = Modifier.constrainAs(card) {
                    top.linkTo(nameRow.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                totalBalance = totalBalance,
                upcomingBillsAmount = upcomingBillsAmount,
                paidBillsAmount = paidBillsAmount
            )

            UpcomingBillsList(
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
fun BillCardItem(modifier: Modifier, totalBalance: Double, upcomingBillsAmount: Double, paidBillsAmount: Double) {
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
            BillCardRowItem(Modifier.align(Alignment.CenterStart), "Upcoming Bills", "R%.2f".format(upcomingBillsAmount), R.drawable.uparrow)
            BillCardRowItem(Modifier.align(Alignment.CenterEnd), "Paid Bills", "R%.2f".format(paidBillsAmount), R.drawable.downarrow)
        }
    }
}

@Composable
fun BillCardRowItem(modifier: Modifier, title: String, amount: String, image: Int) {
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
fun UpcomingBillsList(modifier: Modifier, navController: NavController, expenses: List<Expense>, userId: String) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Upcoming Bills", "Transactions")

    Column(modifier = modifier.padding(horizontal = 16.dp)) {
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
                        .background(if (selectedTab == index) MaterialTheme.colorScheme.surface else Color.Transparent, RoundedCornerShape(20.dp))
                        .clickable {
                            selectedTab = index
                            if (index == 1) navController.navigate("transaction/$userId")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    ExpenseTextView(
                        title,
                        fontSize = 16.sp,
                        color = if (selectedTab == index) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }

        if (selectedTab == 0) {
            UpcomingBillItems(expenses)
        }
    }
}

@Composable
fun UpcomingBillItems(expenses: List<Expense>) {
    val expandedCategories = remember { mutableStateMapOf<String, Boolean>() }
    var allExpanded by remember { mutableStateOf(false) }

    // Filter expenses to only show those that are bills (have notifications enabled or are recurring)
    val upcomingBills = expenses.filter { expense ->
        expense.notificationEnabled || expense.isRecurring
    }.sortedBy { expense ->
        // Sort by date
        DateUtils.parseDate(expense.date)
    }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Box(modifier = Modifier.fillMaxWidth()) {
            ExpenseTextView("Upcoming Bills", fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
            ExpenseTextView(
                if (allExpanded) "Collapse All" else "Expand All",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable {
                        val grouped = upcomingBills.groupBy { it.category }
                        grouped.keys.forEach { category ->
                            expandedCategories[category] = !allExpanded
                        }
                        allExpanded = !allExpanded
                    }
            )
        }
        Spacer(Modifier.height(8.dp))

        if (upcomingBills.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                ExpenseTextView("No upcoming bills found", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
            }
        } else {
            val grouped = upcomingBills.groupBy { it.category }
            grouped.forEach { (category, items) ->
                val total = items.sumOf { it.amount }
                val expanded = expandedCategories[category] ?: false

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        expandedCategories[category] = !expanded
                    }
                    .padding(vertical = 8.dp)
                ) {
                    BillItem(category, "R%.2f".format(total), getCategoryIcon(category), DateUtils.formatForDisplay(items.first().date), MaterialTheme.colorScheme.error)

                    if (expanded) {
                        items.forEach { item ->
                            BillItem(
                                title = "${item.description} (Due: ${DateUtils.formatDueDate(item.date)})",
                                amount = "R%.2f".format(item.amount),
                                icon = getCategoryIcon(item.category),
                                date = if (item.isRecurring) "Recurring ${item.recurringInterval}" else "One-time",
                                color = Color.Gray,
                                photoUri = item.photoUri
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BillItem(title: String, amount: String, icon: Int, date: String, color: Color, photoUri: String? = null) {
    Box(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (photoUri == null) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)
                )
            } else {
                Image(
                    painter = rememberAsyncImagePainter(photoUri.toUri()),
                    contentDescription = "Receipt",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(Modifier.size(8.dp))
            Column {
                ExpenseTextView(title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
                ExpenseTextView(date, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            }
        }
        ExpenseTextView(amount, fontSize = 20.sp, modifier = Modifier.align(Alignment.CenterEnd), color = color, fontWeight = FontWeight.SemiBold)
    }
}

fun getCategoryIcon(category: String): Int {
    return when (category.lowercase()) {
        "food" -> R.drawable.food
        "transport" -> R.drawable.car
        "groceries" -> R.drawable.shopping
        "phone and internet", "phone", "internet", "communication" -> R.drawable.communication
        "entertainment" -> R.drawable.entertainment
        "healthcare", "health" -> R.drawable.health
        "rent", "housing", "house" -> R.drawable.house
        "utilities" -> R.drawable.utilities
        "savings" -> R.drawable.savings
        "investment" -> R.drawable.investment
        else -> R.drawable.other
    }
}

// These functions are now deprecated - use DateUtils instead
fun formatDate(dateString: String): String {
    return DateUtils.formatForDisplay(dateString)
}

fun formatDueDate(dateString: String): String {
    return DateUtils.formatDueDate(dateString)
}

//---------------------------------------------------End_of_File-----------------------------------------------------------------------------------------