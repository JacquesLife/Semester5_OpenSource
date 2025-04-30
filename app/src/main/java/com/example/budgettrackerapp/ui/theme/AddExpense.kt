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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.budgettrackerapp.R
import com.example.budgettrackerapp.widget.ExpenseTextView
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddExpense(navController: NavController? = null, initialAmount: String = "0.00") {
    val context = LocalContext.current
    val imageUri = remember { mutableStateOf<Uri?>(null) }

    val launcherGallery = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri.value = uri
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (imageRef, headerContent, form) = createRefs()

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

            // Header content with back button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp)
                    .constrainAs(headerContent) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                // Back button
                IconButton(
                    onClick = { navController?.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.backarrow),
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Title
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    ExpenseTextView(
                        text = "Add Expense",
                        fontSize = 24.sp,
                        color = Color.White
                    )
                    ExpenseTextView(
                        text = "Record your expenses",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }

            // Form content
            DataForm(
                navController = navController,
                initialAmount = initialAmount,
                imageUri = imageUri.value,
                onGalleryClick = { launcherGallery.launch("image/*") },
                modifier = Modifier
                    .constrainAs(form) {
                        top.linkTo(headerContent.bottom, margin = 24.dp)
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
fun DataForm(
    navController: NavController?,
    initialAmount: String,
    imageUri: Uri?,
    onGalleryClick: () -> Unit,
    modifier: Modifier
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("Select Category") }
    var amount by remember { mutableStateOf(initialAmount) }
    var date by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val calendar = remember { Calendar.getInstance() }
    val dateFormatter = remember { SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()) }

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
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .shadow(8.dp)
            .background(Color.White)
            .clip(RoundedCornerShape(16.dp))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Category
        ExpenseTextView(text = "CATEGORY", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
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
                    Text(text = selectedCategory, color = Color.Black)
                }
            }

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                categories.forEach { (name, icon) ->
                    DropdownMenuItem(
                        onClick = {
                            selectedCategory = name
                            expanded = false
                        },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = icon),
                                    contentDescription = name,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(name)
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Amount
        ExpenseTextView(text = "AMOUNT", fontSize = 14.sp, color = Color.Gray)
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Date
        ExpenseTextView(text = "DATE", fontSize = 14.sp, color = Color.Gray)
        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { datePickerDialog.show() }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Pick Date")
                }
            },
            readOnly = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Image picker
        Button(
            onClick = onGalleryClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add photo")
        }

        Spacer(modifier = Modifier.height(12.dp))

        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Receipt",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Submit
        Button(
            onClick = { navController?.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Expense", color = Color.White)
        }
    }
}