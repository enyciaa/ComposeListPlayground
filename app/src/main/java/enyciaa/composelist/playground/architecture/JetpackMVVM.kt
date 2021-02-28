package enyciaa.composelist.playground.architecture

import android.app.Application
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.navigation.compose.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import enyciaa.composelist.playground.theming.ComposeListPlaygroundTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltAndroidApp
class JetpackMvvmApplication : Application()

@AndroidEntryPoint
class JetpackMvvmActivity : AppCompatActivity() {

    private val jetpackMvvmViewModel: JetpackMvvmViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeListPlaygroundTheme {
                JetpackMvvmApp(jetpackMvvmViewModel)
            }
        }
    }
}

@Composable
fun JetpackMvvmApp(
    jetpackMvvmViewModel: JetpackMvvmViewModel
) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "question") {
        composable("question") {
            JetpackMvvmQuestionDestination(
                jetpackMvvmViewModel = jetpackMvvmViewModel,
                // You could pass the nav controller to further composables,
                // but I like keeping nav logic in a single spot by using the hoisting pattern
                // hoisting probably won't work as well in deep hierarchies,
                // in which case CompositionLocal might be more appropriate
                onConfirm = { navController.navigate("result") },
            )
        }
        composable("result") {
            JetpackMvvmResultDestination(jetpackMvvmViewModel = jetpackMvvmViewModel)
        }
    }
}

@Composable
fun JetpackMvvmQuestionDestination(
    jetpackMvvmViewModel: JetpackMvvmViewModel,
    onConfirm: () -> Unit
) {
    val textFieldState = remember { mutableStateOf(TextFieldValue()) }

    // We only want the event stream to be attached once
    // even if there are multiple re-compositions
    LaunchedEffect("key") {
        jetpackMvvmViewModel.navigateToResults
            .onEach { onConfirm() }
            .collect()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "What do you call a mexican cheese?")
        TextField(
            value = textFieldState.value,
            onValueChange = { textFieldState.value = it }
        )
        if (jetpackMvvmViewModel.isLoading.collectAsState().value) {
            CircularProgressIndicator()
        } else {
            Button(onClick = { jetpackMvvmViewModel.confirmAnswer(textFieldState.value.text) }) {
                Text(text = "Confirm")
            }
        }
    }
}

@Composable
fun JetpackMvvmResultDestination(
    jetpackMvvmViewModel: JetpackMvvmViewModel
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = jetpackMvvmViewModel.textToDisplay.collectAsState().value)
    }
}

@HiltViewModel
class JetpackMvvmViewModel @Inject constructor(
    private val answerService: AnswerService,
): ViewModel() {

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val _textToDisplay: MutableStateFlow<String> = MutableStateFlow("")
    val textToDisplay = _textToDisplay.asStateFlow()
    // See https://proandroiddev.com/android-singleliveevent-redux-with-kotlin-flow-b755c70bb055
    // For why channel > SharedFlow/StateFlow in this case
    private val _navigateToResults = Channel<Boolean>(Channel.BUFFERED)
    val navigateToResults = _navigateToResults.receiveAsFlow()

    fun confirmAnswer(answer: String) {
        viewModelScope.launch {
            _isLoading.value = true
            withContext(Dispatchers.IO) { answerService.save(answer) }
            val text = if (answer == "Nacho cheese") {
                "You've heard too many cheese jokes"
            } else {
                "Nacho cheese"
            }
            _textToDisplay.emit(text)
            _navigateToResults.send(true)
            _isLoading.value = false
        }
    }
}
