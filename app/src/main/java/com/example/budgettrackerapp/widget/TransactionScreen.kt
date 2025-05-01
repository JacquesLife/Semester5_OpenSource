package com.example.budgettrackerapp.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.budgettrackerapp.R
import com.example.budgettrackerapp.data.BudgetViewModel
import com.example.budgettrackerapp.data.Expense
import com.example.budgettrackerapp.ui.theme.DarkBlue
import androidx.compose.ui.unit.TextUnit

@Composable
fun TransactionScreen(navController: NavController, viewModel: BudgetViewModel, userId: Int) {
    val expenses by viewModel.expenses.collectAsState(initial = emptyList())

    Surface(modifier = Modifier.fillMaxSize()) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (imageRef, nameRow, list, card) = createRefs()

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
                Image(
                    painter = painterResource(id = R.drawable.bell),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            CardItem(modifier = Modifier.constrainAs(card) {
                top.linkTo(nameRow.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            })

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
fun TransactionList(
    modifier: Modifier,
    navController: NavController,
    expenses: List<Expense>,
    userId: Int
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Transactions", "Upcoming Bills")

    val groupedExpenses = expenses.groupBy { it.category }
    val expandedCategories = remember { mutableStateMapOf<String, Boolean>() }

    Column(modifier = modifier.padding(horizontal = 16.dp)) {
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

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Box(modifier = Modifier.fillMaxWidth()) {
                ExpenseTextView(text = "Recent Transactions", fontSize = 20.sp)
                ExpenseTextView(
                    text = "Collapse All",
                    fontSize = 14.sp,
                    color = Color.Blue,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable {
                            groupedExpenses.keys.forEach { expandedCategories[it] = false }
                        }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            groupedExpenses.forEach { (category, items) ->
                val expanded = expandedCategories[category] ?: false
                val total = items.sumOf { it.amount }
                val icon = when (category) {
                    "Food" -> R.drawable.food
                    "Transportation" -> R.drawable.car
                    "Shopping" -> R.drawable.shopping
                    "Entertainment" -> R.drawable.entertainment
                    "Healthcare" -> R.drawable.health
                    "Rent" -> R.drawable.house
                    "Phone and Internet" -> R.drawable.communication
                    "Utilities" -> R.drawable.utilities
                    "Saving" -> R.drawable.savings
                    "Investment" -> R.drawable.investment
                    else -> R.drawable.other
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedCategories[category] = !expanded }
                            .padding(vertical = 8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = icon),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        ExpenseTextView(
                            text = "$category - Total: R$total",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (expanded) {
                        items.forEach { expense ->
                            TransactionItem(
                                title = expense.category,
                                amount = "R${expense.amount}",
                                icon = icon,
                                date = expense.date,
                                color = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CardItem(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp,
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            ExpenseTextView(
                text = "Card Title",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            ExpenseTextView(
                text = "This is a detailed description of the card content. You can add more information here.",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            var isVisible by remember { mutableStateOf(true) }

            if (isVisible) {
                Button(
                    onClick = { isVisible = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
                ) {
                    Text(
                        text = "Action",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
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
            .padding(vertical = 8.dp)
            .background(color.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            ExpenseTextView(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            ExpenseTextView(text = date, fontSize = 14.sp, color = Color.Gray)
        }
        ExpenseTextView(text = amount, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun ExpenseTextView(
    text: String,
    fontSize: TextUnit = 16.sp,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color = Color.Black,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontSize = fontSize,
        fontWeight = fontWeight,
        color = color,
        modifier = modifier
    )
}
