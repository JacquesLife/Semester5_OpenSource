package com.example.budgettrackerapp.ui.theme

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import java.text.SimpleDateFormat
import java.util.*

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
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("Select Category") }
    var amount by remember { mutableStateOf(initialAmount) }
    var date by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // Calendar setup
    val calendar = remember { Calendar.getInstance() }
    val dateFormatter = remember { SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()) }

    // Date picker dialog
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            date = dateFormatter.format(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val categories = listOf(
        "Food" to R.drawable.food,
        "Transportation" to R.drawable.car,
        "Shopping" to R.drawable.shopping,
        "Entertainment" to R.drawable.entertainment,
        "Healthcare" to R.drawable.health,
        "Rent" to R.drawable.house,
        "Phone and Internet" to R.drawable.communication,
        "Utilities" to R.drawable.utilities,
        "Saving" to R.drawable.savings,
        "Investment" to R.drawable.investment,
        "Other" to R.drawable.other
    )

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
        ExpenseTextView(text = "CATEGORY", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.size(4.dp))

        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val selectedIcon = categories.find { it.first == selectedCategory }?.second
                    if (selectedIcon != null) {
                        Image(
                            painter = painterResource(id = selectedIcon),
                            contentDescription = selectedCategory,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = selectedCategory,
                        color = Color.Black
                    )
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { (categoryName, iconRes) ->
                    DropdownMenuItem(
                        onClick = {
                            selectedCategory = categoryName
                            expanded = false
                        },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = iconRes),
                                    contentDescription = categoryName,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(categoryName)
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        ExpenseTextView(text = "AMOUNT", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.size(4.dp))
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(16.dp))

        ExpenseTextView(text = "DATE", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.size(4.dp))
        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { datePickerDialog.show() }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select Date"
                    )
                }
            },
            readOnly = true
        )

        Spacer(modifier = Modifier.size(24.dp))

        Button(
            onClick = {
                navController?.popBackStack()
            },
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .fillMaxWidth()
        ) {
            Text("Add Expense", color = Color.White)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AddExpensePreview() {
    AddExpense(rememberNavController())
}