package enyciaa.composelist.playground

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.tooling.preview.Preview
import enyciaa.composelist.playground.ui.ComposeListPlaygroundTheme

class SimpleListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeListPlaygroundTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val dataItems = (0..100).map { SimpleListDataItem("How easy was that!") }
                    MySimpleList(
                        modifier = Modifier.fillMaxSize(),
                        dataItems,
                    )
                }
            }
        }
    }
}

data class SimpleListDataItem(
    val text: String
)

@Composable
fun MySimpleList(
    modifier: Modifier = Modifier,
    simpleListDataItems: List<SimpleListDataItem>
) {
    LazyColumn(modifier = modifier) {
        items(simpleListDataItems) { data ->
            MySimpleListItem(simpleListDataItem = data)
        }
    }
}

@Composable
fun MySimpleListItem(simpleListDataItem: SimpleListDataItem) {
    Text(text = simpleListDataItem.text)
}
