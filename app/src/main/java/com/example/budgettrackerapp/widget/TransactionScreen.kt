package com.example.budgettrackerapp.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
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
import androidx.navigation.NavController
import com.example.budgettrackerapp.R
import com.example.budgettrackerapp.ui.theme.DarkBlue

@Composable
fun TransactionScreen(navController: NavController) {
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
                Column {
                    ExpenseTextView(
                        text = "Hello World",
                        fontSize = 24.sp,
                        color = Color.White
                    )
                    ExpenseTextView(
                        text = "This is a sample app",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }

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
                navController = navController
            )
        }
    }
}

@Composable
fun CardItem(modifier: Modifier) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(DarkBlue)
            .padding(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                ExpenseTextView(text = "Total Balance", fontSize = 16.sp, color = Color.White)
                ExpenseTextView(
                    text = "R1000.00",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Image(
                painter = painterResource(id = R.drawable.dotsmenue),
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            CardRowItem(
                modifier = Modifier.align(Alignment.CenterStart),
                title = "Income",
                amount = "R3,494",
                image = R.drawable.uparrow
            )
            CardRowItem(
                modifier = Modifier.align(Alignment.CenterEnd),
                title = "Expense",
                amount = "R1,230",
                image = R.drawable.downarrow
            )
        }
    }
}

@Composable
fun TransactionList(modifier: Modifier, navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Transactions", "Upcoming Bills")

    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        // Tab switcher
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
                                navController.navigate("upcoming_bills")
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

        // Show transactions when on the Transactions tab
        RecentTransactions()
    }
}

@Composable
fun RecentTransactions() {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            ExpenseTextView(text = "Recent Transactions", fontSize = 20.sp)
            ExpenseTextView(
                text = "See All",
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        TransactionItem(
            title = "Food",
            amount = "R200.00",
            icon = R.drawable.food,
            date = "Today",
            color = Color.Red
        )
        TransactionItem(
            title = "Transport",
            amount = "R200.00",
            icon = R.drawable.car,
            date = "Today",
            color = Color.Red
        )
        TransactionItem(
            title = "Groceries",
            amount = "R200.00",
            icon = R.drawable.shopping,
            date = "Today",
            color = Color.Red
        )
        TransactionItem(
            title = "Phone and Internet",
            amount = "R200.00",
            icon = R.drawable.communication,
            date = "Today",
            color = Color.Red
        )
        TransactionItem(
            title = "Entertainment",
            amount = "R200.00",
            icon = R.drawable.entertainment,
            date = "Today",
            color = Color.Red
        )
        TransactionItem(
            title = "Healthcare",
            amount = "R200.00",
            icon = R.drawable.health,
            date = "Today",
            color = Color.Red
        )
        TransactionItem(
            title = "Rent",
            amount = "R200.00",
            icon = R.drawable.house,
            date = "Today",
            color = Color.Red
        )
        TransactionItem(
            title = "Utilities",
            amount = "R200.00",
            icon = R.drawable.utilities,
            date = "Today",
            color = Color.Red
        )
        TransactionItem(
            title = "Savings",
            amount = "R200.00",
            icon = R.drawable.savings,
            date = "Today",
            color = Color.Red
        )
        TransactionItem(
            title = "Investment",
            amount = "R200.00",
            icon = R.drawable.investment,
            date = "Today",
            color = Color.Red
        )
        TransactionItem(
            title = "Other",
            amount = "R200.00",
            icon = R.drawable.other,
            date = "Today",
            color = Color.Red
        )
    }
}

@Composable
fun CardRowItem(modifier: Modifier, title: String, amount: String, image: Int) {
    Column(modifier = modifier) {
        Row {
            Image(
                painter = painterResource(id = image),
                contentDescription = null
            )
            Spacer(modifier = Modifier.size(8.dp))
            ExpenseTextView(text = title, fontSize = 16.sp, color = Color.White)
        }
        ExpenseTextView(text = amount, fontSize = 20.sp, color = Color.White)
    }
}

@Composable
fun TransactionItem(title: String, amount: String, icon: Int, date: String, color: Color) {
    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Column {
                ExpenseTextView(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                ExpenseTextView(text = date, fontSize = 12.sp)
            }
        }
        ExpenseTextView(
            text = amount,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterEnd),
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}