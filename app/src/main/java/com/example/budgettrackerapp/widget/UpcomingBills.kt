package com.example.budgettrackerapp.widget

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.budgettrackerapp.data.User
import com.example.budgettrackerapp.ui.theme.DarkBlue
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun UpcomingBillsScreen(navController: NavController, viewModel: BudgetViewModel = androidx.lifecycle.viewmodel.compose.viewModel(), userId: Int) {
    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val oneMonthLater = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
        Calendar.getInstance().apply { add(Calendar.MONTH, 1) }.time
    )

    LaunchedEffect(Unit) {
        viewModel.loadExpenses(userId)
        viewModel.loadBudgetSettings()
    }

    val expenses by viewModel.expenses.collectAsState(emptyList())
    val budgetSettings by viewModel.budgetSettings.collectAsState()

    val totalBalance = budgetSettings?.monthlyBudget ?: 0.0
    val upcomingBillsAmount = expenses.sumOf { it.amount }
    val paidBillsAmount = 0.0

    Surface(modifier = Modifier.fillMaxSize()) {
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
                    ExpenseTextView("Upcoming Bills", fontSize = 24.sp, color = Color.White)
                    ExpenseTextView("Search your Transactions", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
            ExpenseTextView(title, fontSize = 16.sp, color = Color.White)
        }
        ExpenseTextView(amount, fontSize = 20.sp, color = Color.White)
    }
}

@Composable
fun UpcomingBillsList(modifier: Modifier, navController: NavController, expenses: List<Expense>, userId: Int) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Upcoming Bills", "Transactions")

    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .background(Color(0xFFF5F5F5), RoundedCornerShape(24.dp))
                .padding(4.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .background(if (selectedTab == index) Color.White else Color.Transparent, RoundedCornerShape(20.dp))
                        .clickable {
                            selectedTab = index
                            if (index == 1) navController.navigate("transaction/$userId")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    ExpenseTextView(title, fontSize = 16.sp, color = if (selectedTab == index) Color.Black else Color.Gray)
                }
            }
        }

        if (selectedTab == 0) {
            UpcomingBillItems(expenses, navController)
        }
    }
}

@Composable
fun UpcomingBillItems(expenses: List<Expense>, navController: NavController) {
    val expandedCategories = remember { mutableStateMapOf<String, Boolean>() }
    var allExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Box(modifier = Modifier.fillMaxWidth()) {
            ExpenseTextView("Upcoming Bills", fontSize = 20.sp)
            ExpenseTextView(
                if (allExpanded) "Collapse All" else "Expand All",
                fontSize = 16.sp,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable {
                        val grouped = expenses.groupBy { it.category }
                        grouped.keys.forEach { category ->
                            expandedCategories[category] = !allExpanded
                        }
                        allExpanded = !allExpanded
                    }
            )
        }
        Spacer(Modifier.height(8.dp))

        if (expenses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                ExpenseTextView("No upcoming bills found", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        } else {
            val grouped = expenses.groupBy { it.category }
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
                    BillItem(category, "R%.2f".format(total), getCategoryIcon(category), formatDate(items.first().date), Color.Red)

                    if (expanded) {
                        items.forEach { item ->
                            BillItem(
                                title = item.description,
                                amount = "R%.2f".format(item.amount),
                                icon = getCategoryIcon(item.category),
                                date = formatDate(item.date),
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
            // Show category icon if no photo, otherwise show the photo
            if (photoUri == null) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)
                )
            } else {
                // Display the receipt image
                Image(
                    painter = rememberAsyncImagePainter(Uri.parse(photoUri)),
                    contentDescription = "Receipt",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(Modifier.size(8.dp))
            Column {
                ExpenseTextView(title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                ExpenseTextView(date, fontSize = 12.sp)
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

fun formatDate(dateString: String): String {
    return try {
        val input = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val output = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        output.format(input.parse(dateString) ?: Date())
    } catch (e: Exception) {
        dateString
    }
}