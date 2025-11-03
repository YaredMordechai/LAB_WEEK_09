package com.example.lab_week_09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lab_week_09.ui.theme.LAB_WEEK_09Theme
import com.example.lab_week_09.ui.theme.OnBackgroundItemText
import com.example.lab_week_09.ui.theme.OnBackgroundTitleText
import com.example.lab_week_09.ui.theme.PrimaryTextButton

// Navigation imports
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LAB_WEEK_09Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    App(navController = navController)
                }
            }
        }
    }
}

@Composable
fun App(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            Home { listStr ->
                navController.navigate("resultContent/?listData=$listStr")
            }
        }
        composable(
            route = "resultContent/?listData={listData}",
            arguments = listOf(navArgument("listData") { type = NavType.StringType })
        ) { backStackEntry ->
            ResultContent(listData = backStackEntry.arguments?.getString("listData").orEmpty())
        }
    }
}

// ---------------------- App Screens ----------------------

data class Student(var name: String)

@Composable
fun Home(
    navigateFromHomeToResult: (String) -> Unit
) {
    val listData = remember {
        mutableStateListOf(
            Student("Tanu"),
            Student("Tina"),
            Student("Tono")
        )
    }
    var inputField by remember { mutableStateOf(Student("")) }

    // NEW: track error visibility for empty submit attempts
    var showError by remember { mutableStateOf(false) }

    // NEW: validity flag and sanitized text
    val trimmed = inputField.name.trim()
    val isSubmitEnabled = trimmed.isNotEmpty()

    HomeContent(
        listData = listData,
        inputField = inputField,
        isSubmitEnabled = isSubmitEnabled,       // NEW
        showInputError = showError,              // NEW
        onInputValueChange = { input ->
            inputField = inputField.copy(name = input)
            // Clear error as soon as user types something meaningful
            if (showError && input.trim().isNotEmpty()) showError = false
        },
        onClickSubmit = {
            // HARD BLOCK: don't add blanks; also trim before add
            if (trimmed.isEmpty()) {
                showError = true
                return@HomeContent
            }
            listData.add(Student(trimmed))
            inputField = Student("")             // reset field
            showError = false                    // clear error after successful add
        },
        onClickFinish = {
            navigateFromHomeToResult(listData.toList().toString())
        }
    )
}

@Composable
fun HomeContent(
    listData: SnapshotStateList<Student>,
    inputField: Student,
    isSubmitEnabled: Boolean,                    // NEW
    showInputError: Boolean,                     // NEW
    onInputValueChange: (String) -> Unit,
    onClickSubmit: () -> Unit,
    onClickFinish: () -> Unit
) {
    LazyColumn {
        item {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OnBackgroundTitleText(text = stringResource(id = R.string.enter_item))

                TextField(
                    value = inputField.name,
                    onValueChange = onInputValueChange,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    // NEW: simple visual error + helper text when user tried to submit empty
                    isError = showInputError,
                    supportingText = {
                        if (showInputError) {
                            Text("Input cannot be empty.")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Row {
                    // NEW: Disable submit when input is blank/whitespace
                    PrimaryTextButton(
                        text = stringResource(id = R.string.button_click),
                        enabled = isSubmitEnabled,
                        onClick = onClickSubmit
                    )
                    PrimaryTextButton(
                        text = stringResource(id = R.string.button_navigate),
                        onClick = onClickFinish
                    )
                }
            }
        }
        items(listData) { item ->
            Column(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OnBackgroundItemText(text = item.name)
            }
        }
    }
}

@Composable
fun ResultContent(listData: String) {
    Column(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OnBackgroundItemText(text = listData)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeCommit4Fixed() {
    LAB_WEEK_09Theme {
        Home(navigateFromHomeToResult = {})
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAppCommit4Fixed() {
    LAB_WEEK_09Theme {
        val navController = rememberNavController()
        App(navController)
    }
}