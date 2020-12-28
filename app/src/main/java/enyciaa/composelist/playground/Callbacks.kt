package enyciaa.composelist.playground

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import enyciaa.composelist.playground.theming.ComposeListPlaygroundTheme

class CallbackListActivity : AppCompatActivity() {

    private val callbackViewModel = CallbackViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeListPlaygroundTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val dataItems = (0..100).map { CallbackListDataItem("Click me", it) }
                    MyCallbackList(
                        modifier = Modifier.fillMaxSize(),
                        callbackListDataItem = dataItems,
                        itemClickedCallback = { callbackViewModel.onListItemClicked(it) }
                    )
                }
            }
        }
    }
}

data class CallbackListDataItem(
    val text: String,
    val number: Int,
)

@Composable
fun MyCallbackList(
    modifier: Modifier = Modifier,
    callbackListDataItem: List<CallbackListDataItem>,
    itemClickedCallback: (callbackListDataItem: CallbackListDataItem) -> Unit,
) {
    LazyColumn(modifier = modifier) {
        items(callbackListDataItem) { item ->
            CallbackListItem(
                callbackListDataItem = item,
                itemClickedCallback = itemClickedCallback
            )
        }
    }
}

@Composable
fun CallbackListItem(
    callbackListDataItem: CallbackListDataItem,
    itemClickedCallback: (callbackListDataItem: CallbackListDataItem) -> Unit,
) {
    Button(onClick = { itemClickedCallback(callbackListDataItem) }) {
        Text(text = callbackListDataItem.text)
    }
}

class CallbackViewModel {

    fun onListItemClicked(callbackListDataItem: CallbackListDataItem) {
        Log.v("Tiny Terry", "Item ${callbackListDataItem.number} was clicked")
    }
}