package enyciaa.composelist.playground.architecture

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.navigation.compose.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import enyciaa.composelist.playground.list.Todo
import enyciaa.composelist.playground.list.TodoRepository
import enyciaa.composelist.playground.list.TodoViewModel
import enyciaa.composelist.playground.theming.ComposeListPlaygroundTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MvvmActivity : AppCompatActivity() {

    // To inject via dagger, pass the Provider into the nav graph
    private val mvvmViewModel = MvvmViewModel(AnswerService())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeListPlaygroundTheme {
                MvvmApp(mvvmViewModel)
            }
        }
    }
}

@Composable
fun MvvmApp(
    mvvmViewModel: MvvmViewModel
) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "question") {
        composable("question") {
            MvvmQuestionDestination(
                mvvmViewModel = mvvmViewModel,
                // You could pass the nav controller to further composables,
                // but I like keeping nav logic in a single spot
                onConfirm = { navController.navigate("result") },
            )
        }
        composable("result") {
            MvvmResultDestination(
                mvvmViewModel,
            )
        }
    }
}

@Composable
fun MvvmQuestionDestination(
    mvvmViewModel: MvvmViewModel,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "What do you call a mexican cheese?")
        val textState = remember { mutableStateOf(TextFieldValue()) }
        TextField(
            value = textState.value,
            onValueChange = { textState.value = it }
        )
        if (mvvmViewModel.isLoading.collectAsState().value) {
            CircularProgressIndicator()
        } else {
            Button(onClick = {
                mvvmViewModel.confirmAnswer(textState.value.text)
                onConfirm()
            }) {
                Text(text = "Confirm")
            }
        }
    }
}

@Composable
fun MvvmResultDestination(
    mvvmViewModel: MvvmViewModel
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = mvvmViewModel.textToDisplay.collectAsState().value)
    }
}

class MvvmViewModel(
    private val answerService: AnswerService,
) {

    private val coroutineScope = MainScope()
    val isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val textToDisplay: MutableStateFlow<String> = MutableStateFlow("")

    fun confirmAnswer(answer: String) {
        coroutineScope.launch {
            isLoading.value = true
            withContext(Dispatchers.IO) { answerService.save(answer) }
            val validatedAnswer = if (answer.isBlank()) "wrong answer" else answer
            val text = if (answer == "Nacho cheese") {
                "You've heard too many cheese jokes"
            } else {
                "Nacho cheese"
            }
            textToDisplay.emit(text)
            isLoading.value = false
        }
    }
}
