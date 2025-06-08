/// Reference: https://www.youtube.com/watch?v=-Kj9T1sa6zk
/// https://www.svgrepo.com/collection/responsive-flat-icons/
/// This page is responsible for creating an expense with an image, date, and category and amount it also
/// pass this logic to the database to be stored

package com.example.budgettrackerapp.ui.theme

import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.budgettrackerapp.R
import com.example.budgettrackerapp.data.BudgetViewModel
import com.example.budgettrackerapp.data.Expense
import com.example.budgettrackerapp.widget.ExpenseTextView
import okhttp3.internal.userAgent
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddExpense(navController: NavController? = null, initialAmount: String = "0.00", userId: String) {
    Surface(modifier = Modifier.fillMaxSize()) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (imageRef, nameRow, list, card) = createRefs()

            // Background image
            Image(
                painter = painterResource(id = R.drawable.toppage),
                contentDescription = null,
                modifier = Modifier.constrainAs(imageRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )

            // Name row
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

                // Back arrow
                Image(
                    painter = painterResource(id = R.drawable.backarrow),
                    contentDescription = "Go Back",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable { navController?.popBackStack() }
                )

                // Add expense text
                ExpenseTextView(
                    text = "Add Expense",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center)
                )
                // Dots menu
                Image(
                    painter = painterResource(id = R.drawable.dotsmenue),
                    contentDescription = "Menu",
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
            // Expense list
            DataForm(
                navController = navController,
                initialAmount = initialAmount,
                userId,
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
fun DataForm(navController: NavController? = null, initialAmount: String = "0.00", userId: String, modifier: Modifier) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("Select Category") }
    var amount by remember { mutableStateOf(initialAmount) }
    var date by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    // New notification-related fields
    var isRecurring by remember { mutableStateOf(false) }
    var recurringInterval by remember { mutableStateOf("monthly") }
    var recurringExpanded by remember { mutableStateOf(false) }
    var notificationEnabled by remember { mutableStateOf(false) }
    var notificationDaysBefore by remember { mutableStateOf(3f) }

    // Image picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

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
    

    // Category selection
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
        // Expense description
        ExpenseTextView(text = "CATEGORY", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.size(4.dp))

        Box {
            // Dropdown menu for category selection
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                // Display selected category
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val selectedIcon = categories.find { it.first == selectedCategory }?.second
                    if (selectedIcon != null) {
                        Image(
                            // Display the selected category icon
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
                // Display the dropdown menu
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
                            // Display each category option
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
        // Expense amount
        ExpenseTextView(text = "AMOUNT", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.size(4.dp))
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(16.dp))

        // Expense/Due date
        ExpenseTextView(text = "DATE/DUE DATE", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.size(4.dp))
        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Select date or due date") },
            trailingIcon = {
                IconButton(onClick = { datePickerDialog.show() }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select Date"
                    )
                }
            },
            // Read-only field
            readOnly = true
        )

        Spacer(modifier = Modifier.size(16.dp))

        // Recurring Expense Checkbox
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = isRecurring,
                onCheckedChange = { isRecurring = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            ExpenseTextView(text = "This is a recurring expense", fontSize = 14.sp)
        }

        // Recurring Interval (only show if recurring is enabled)
        if (isRecurring) {
            Spacer(modifier = Modifier.size(8.dp))
            ExpenseTextView(text = "RECURRING INTERVAL", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.size(4.dp))
            
            Box {
                OutlinedButton(
                    onClick = { recurringExpanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = recurringInterval.replaceFirstChar { it.uppercase() },
                        color = Color.Black
                    )
                }

                DropdownMenu(
                    expanded = recurringExpanded,
                    onDismissRequest = { recurringExpanded = false }
                ) {
                    listOf("weekly", "monthly", "yearly").forEach { interval ->
                        DropdownMenuItem(
                            onClick = {
                                recurringInterval = interval
                                recurringExpanded = false
                            },
                            text = {
                                Text(interval.replaceFirstChar { it.uppercase() })
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        // Notification Settings (always show notification option)
        ExpenseTextView(text = "NOTIFICATION SETTINGS", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.size(8.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = notificationEnabled,
                onCheckedChange = { notificationEnabled = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            ExpenseTextView(text = "Enable notifications for this expense", fontSize = 14.sp)
        }

        if (notificationEnabled) {
            Spacer(modifier = Modifier.size(8.dp))
            ExpenseTextView(
                text = "Notify ${notificationDaysBefore.toInt()} days before due date",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Slider(
                value = notificationDaysBefore,
                onValueChange = { notificationDaysBefore = it },
                valueRange = 1f..14f,
                steps = 12,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        Spacer(modifier = Modifier.size(16.dp))

        ExpenseTextView(text = "PHOTO", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.size(4.dp))

        // Photo selection button
        OutlinedButton(
            onClick = { photoPickerLauncher.launch("image/*") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Add photo icon
                Icon(
                    imageVector = Icons.Filled.AddAPhoto,
                    contentDescription = "Add Photo",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = if (selectedImageUri == null) "Add Receipt Image" else "Change Image")
            }
        }

        // Show selected image preview
        selectedImageUri?.let { uri ->
            Spacer(modifier = Modifier.size(8.dp))
            Box(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
            ) {
                Image(
                    // Display the selected image
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Selected Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.size(24.dp))

        val viewModel: BudgetViewModel = viewModel()
        // Add expense button
        Button(
            // Handle add expense button click
            onClick = {
                val expense = Expense(
                    amount = amount.toDoubleOrNull() ?: 0.0,
                    date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                        dateFormatter.parse(date) ?: Calendar.getInstance().time
                    ),
                    isRecurring = isRecurring,
                    recurringInterval = if (isRecurring) recurringInterval else "",
                    startTime = "",
                    endTime = "",
                    description = "Expense on $date",
                    category = selectedCategory,
                    photoUri = selectedImageUri?.toString(),
                    userOwnerId = userId,
                    notificationEnabled = notificationEnabled,
                    notificationDaysBefore = notificationDaysBefore.toInt()
                )
                //Add expense button
                viewModel.addExpense(expense) { success, expenseId ->
                    if (success && expense.notificationEnabled) {
                        // Trigger immediate notification check for new expense
                        val notificationManager = com.example.budgettrackerapp.data.ExpenseNotificationManager(context)
                        notificationManager.scheduleImmediateCheck()
                    }
                }
                navController?.navigate("transaction/$userId") {
                    popUpTo("add_expense") { inclusive = true }
                }
            },
            // Button
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .fillMaxWidth()
        ) {
            Text("Add Expense", color = Color.White)
        }
    }
}

//---------------------------------------------------End_of_File-----------------------------------------------------------------------------------------