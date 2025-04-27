package com.example.budgettrackerapp.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.budgettrackerapp.R
import com.example.budgettrackerapp.widget.ExpenseTextView

@Composable
fun AddExpense(navController: NavController? = null, initialAmount: String = "0.00") {
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
                    .padding(top = 60.dp, start = 16.dp, end = 16.dp)
                    .constrainAs(nameRow) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.backarrow),
                    contentDescription = "Go Back",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable { navController?.popBackStack() }
                )

                ExpenseTextView(
                    text = "Add Expense",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center)
                )

                Image(
                    painter = painterResource(id = R.drawable.dotsmenue),
                    contentDescription = "Menu",
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            DataForm(
                navController = navController,
                initialAmount = initialAmount,
                modifier = Modifier
                    .padding(top = 60.dp)
                    .constrainAs(card) {
                        top.linkTo(nameRow.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )
        }
    }
}

@Composable
fun DataForm(navController: NavController? = null, initialAmount: String = "0.00", modifier: Modifier) {
    var type by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf(initialAmount) }
    var date by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .shadow(16.dp)
            .background(Color.White)
            .clip(RoundedCornerShape(16.dp))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        ExpenseTextView(text = "Type", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.size(4.dp))
        OutlinedTextField(value = type, onValueChange = { type = it }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.size(16.dp))

        ExpenseTextView(text = "Name", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.size(4.dp))
        OutlinedTextField(value = name, onValueChange = { name = it }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.size(16.dp))

        ExpenseTextView(text = "Category", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.size(4.dp))
        OutlinedTextField(value = category, onValueChange = { category = it }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.size(16.dp))

        ExpenseTextView(text = "Amount", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.size(4.dp))
        OutlinedTextField(value = amount, onValueChange = { amount = it }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.size(16.dp))

        ExpenseTextView(text = "Date", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.size(4.dp))
        OutlinedTextField(value = date, onValueChange = { date = it }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.size(16.dp))

        Button(
            onClick = {
                // You can save the expense here
                navController?.popBackStack()
            },
            modifier = Modifier
                .clip(RoundedCornerShape(2.dp))
                .fillMaxWidth()
        ) {
            ExpenseTextView(
                text = "Add Expense",
                fontSize = 14.sp,
                color = Color.White,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AddExpensePreview() {
    AddExpense(rememberNavController())
}