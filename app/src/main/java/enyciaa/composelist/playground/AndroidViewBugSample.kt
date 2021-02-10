package enyciaa.composelist.playground

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.viewinterop.AndroidView
import enyciaa.composelist.playground.theming.ComposeListPlaygroundTheme

class AndroidViewBugActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeListPlaygroundTheme {
                val someState = remember { mutableStateOf("Button Text") }
                AndroidViewBugSample(
                    string = someState.value,
                    onButtonClick = { someState.value = "State changed" }
                )
            }
        }
    }
}

@Composable
fun AndroidViewBugSample(
    string: String,
    onButtonClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            viewBlock = {
                val button = Button(it)
                button.text = string
                button.setOnClickListener { onButtonClick() }
                button
            },
        )
    }
}