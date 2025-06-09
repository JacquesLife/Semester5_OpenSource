
# 💸 BuckSavers

Welcome to the **BuckSavers**, a Kotlin-based Android application designed to help users manage their monthly expenses, visualize spending habits, and stay on top of upcoming bills — all with a sleek and interactive UI built in Jetpack Compose.

---

## Group

- Jacques du Plessis ST10329686
- Ethan Smyth ST10255309
- Caleb Voskuil ST10397320
- Ethan Donaldson ST10318621

## Links 

- Github: https://github.com/JacquesLife/Semester5_OpenSource
- Walkthrough Video: https://www.youtube.com/watch?v=mXjAB-rR950

##  App Purpose

This app allows users to:

- 📊 Track income and expenses per category
- 📅 Stay ahead of upcoming bills
- 🧾 Add detailed transactions with optional photos
- 🎯 Set monthly financial goals
- 👤 Manage and personalize their user profile

---

##  Navigation Walkthrough

The app uses a bottom navigation bar (except on login/register screens) to switch between core features. Here's how to navigate:

###  Getting Started

1. **Splash Screen**
   - Automatically redirects to login after a brief display

2. **Login**
   - Enter username and password
   - If already registered, user is logged in
   - If not, registration logic checks for duplicates and hashes password

>  Bottom nav is hidden here to prevent bypassing security

---

###  Home 

- Displays current balance, recent transactions, and quick navigation to add expenses or review stats

### Transactions 

- Shows all past transactions
- Grouped and filterable by category/date
- Each item shows amount, category, and time range

---

###  Upcoming Bills 

- Lists all bills due in the next 30 days
- Click to expand each category for detailed entries
- View "Paid Bills" vs. "Upcoming Bills" in a visual card

---

###  Add Expense 

- Allows manual entry of a transaction
- Supports:
  - Amount
  - Category
  - Description
  - Date and time range
  - Optional image attachment

---

###  Stats 

- Breakdown of total spending per category
- Displays pie chart or bar graphs
- Helps visualize where most money is spent

---

###  Profile 

- View current username and rank
- Personal settings screen (rank may be based on spending habits or goals)

---

###  Rewards

- Shows the users current rank based on their points earned through saving money.
- Motivates users to stay within their budget.

---

### Dark Mode

- The user can change the device theme from light mode to dark in the settings page located in the menu drawer.
- Limits eye strain for prolonged app usage.

--- 

##  Technical Highlights

- **Room Database** with DAOs for `User`, `Expense`, `Category`, `BudgetSettings`
- **MVVM architecture** for clean separation of logic
- **Secure authentication** with hashed password handling using BCrypt
- **StateFlow + Compose** for reactive UI updates
- **ConstraintLayout, LazyColumn, and tabs** in Jetpack Compose

---

##  Getting Started

1. Clone or extract the project.
2. Add this dependency to your `build.gradle.kts`:
   ```kotlin
   implementation("org.mindrot:jbcrypt:0.4")
   ```
3. Sync Gradle.
4. Run on emulator or device (API level 26+ recommended).

---

##  Developer Tips

- All navigation routes include `userId` for user-specific views.
- `fallbackToDestructiveMigration()` is enabled — safe for prototyping.
- Ensure to call `viewModel.loadExpenses(userId)` before showing data-dependent screens.

---

##  Contact

For contributions or issues, please reach out to us or fork this repo and enhance it further.

---

## Reference List:

- https://www.youtube.com/watch?v=LfHkAUzup5E

- https://medium.com/@rowaido.game/mastering-layout-basics-in-jetpack-compose-8f85853855e3

- https://www.youtube.com/watch?v=Q0gRqbtFLcw

- https://www.youtube.com/watch?v=-Kj9T1sa6zk
  
- https://www.svgrepo.com/collection/responsive-flat-icons/

- https://developer.android.com/develop/ui/compose/text/user-input

- https://www.youtube.com/watch?v=-Kj9T1sa6zk 

- https://medium.com/@acceldia/jetpack-compose-creating-expandable-cards-with-content-9ea1eae09efe

- https://www.youtube.com/watch?v=mq8lekRbF4I&list=PL0pXjGnY7POS_IS8gGkwZfxKRMiJ2DSEO&index=2


