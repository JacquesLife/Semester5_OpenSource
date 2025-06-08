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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.budgettrackerapp.R
import com.example.budgettrackerapp.data.BudgetViewModel
import com.example.budgettrackerapp.data.Expense
import com.example.budgettrackerapp.utils.DateUtils
import com.example.budgettrackerapp.widget.ExpenseTextView
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddExpense(navController: NavController? = null, initialAmount: String = "", userId: String) {
    Surface(modifier = Modifier.fillMaxSize()) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (imageRef, nameRow, _, card) = createRefs()

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
                    color = MaterialTheme.colorScheme.onPrimary,
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
                        bottom.linkTo(parent.bottom)
                        height = Dimension.fillToConstraints
                    }
            )
        }
    }
}

@Composable
fun DataForm(navController: NavController? = null, initialAmount: String = "", userId: String, modifier: Modifier) {
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
    var notificationDaysBefore by remember { mutableFloatStateOf(3f) }

    // Image picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = try {
            uri
        } catch (e: Exception) {
            // Silently fail if image selection fails
            null
        }
    }

    // Calendar setup
    val calendar = remember { Calendar.getInstance() }
    remember { SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()) }

    // Date picker dialog
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            date = DateUtils.formatForDisplayLong(calendar.time)
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
            .fillMaxSize()
            .padding(16.dp)
            .fillMaxWidth()
            .shadow(16.dp)
            .background(MaterialTheme.colorScheme.surface)
            .clip(RoundedCornerShape(16.dp))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Expense description
        ExpenseTextView(text = "CATEGORY", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
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
                        color = MaterialTheme.colorScheme.onSurface
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
        ExpenseTextView(text = "AMOUNT", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        Spacer(modifier = Modifier.size(4.dp))
        OutlinedTextField(
            value = amount,
            onValueChange = { newValue ->
                // Only allow numbers and decimal points
                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                    amount = newValue
                }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter amount", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.size(16.dp))

        // Expense date
        ExpenseTextView(text = "DATE", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        Spacer(modifier = Modifier.size(4.dp))
        OutlinedButton(
            onClick = { 
                try {
                    datePickerDialog.show()
                } catch (e: Exception) {
                    // Silently fail if date picker can't be shown
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Select Date",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = date.ifEmpty { "Select date" },
                    color = if (date.isEmpty()) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                )
            }
        }

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

        ExpenseTextView(text = "PHOTO", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        Spacer(modifier = Modifier.size(4.dp))

        // Photo selection button
        OutlinedButton(
            onClick = { 
                try {
                    photoPickerLauncher.launch("image/*")
                } catch (e: Exception) {
                    // Silently fail if photo picker can't be launched
                }
            },
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
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                    .background(MaterialTheme.colorScheme.surfaceVariant)
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
                // Validate inputs and provide safe fallbacks
                val validAmount = amount.toDoubleOrNull()?.takeIf { it > 0.0 } ?: 0.01
                
                val validCategory = if (selectedCategory == "Select Category" || selectedCategory.isBlank()) {
                    "Other"
                } else {
                    selectedCategory
                }
                
                val expenseDate = if (date.isNotEmpty()) {
                    DateUtils.parseDate(date)
                } else {
                    Date()
                }
                
                val formattedDate = DateUtils.formatForStorage(expenseDate)
                
                val validUserId = userId.ifBlank { "default_user" }
                
                val validRecurringInterval = if (isRecurring && recurringInterval.isNotBlank()) {
                    recurringInterval
                } else if (isRecurring) {
                    "monthly"
                } else {
                    ""
                }
                
                val validNotificationDays = notificationDaysBefore.toInt().coerceIn(1, 14)
                
                val displayDate = date.ifEmpty { "today" }
                
                try {
                    val expense = Expense(
                        amount = validAmount,
                        date = formattedDate,
                        isRecurring = isRecurring,
                        recurringInterval = validRecurringInterval,
                        startTime = "",
                        endTime = "",
                        description = "Expense on $displayDate",
                        category = validCategory,
                        photoUri = selectedImageUri?.toString(),
                        userOwnerId = validUserId,
                        notificationEnabled = notificationEnabled,
                        notificationDaysBefore = validNotificationDays
                    )
                    
                    //Add expense button
                    viewModel.addExpense(expense) { success, _ ->
                        try {
                            if (success && expense.notificationEnabled) {
                                // Trigger immediate notification check for new expense
                                val notificationManager = com.example.budgettrackerapp.data.ExpenseNotificationManager(context)
                                notificationManager.scheduleImmediateCheck()
                            }
                        } catch (e: Exception) {
                            // Silently fail notification setup rather than crash
                        }
                    }
                    
                    // Safe navigation with fallback
                    try {
                        navController?.navigate("transaction/$validUserId") {
                            popUpTo("add_expense") { inclusive = true }
                        }
                    } catch (e: Exception) {
                        // Fallback: just pop back if navigation fails
                        navController?.popBackStack()
                    }
                } catch (e: Exception) {
                    // Ultimate fallback: just pop back if expense creation fails
                    navController?.popBackStack()
                }
            },
            // Button
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Add Expense", color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

//---------------------------------------------------End_of_File-----------------------------------------------------------------------------------------