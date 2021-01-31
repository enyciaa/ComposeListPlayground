package enyciaa.composelist.playground.list

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import enyciaa.composelist.playground.theming.ComposeListPlaygroundTheme

class MultipleItemActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeListPlaygroundTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val dataItems = (0..100).map {
                        if (it % 2 == 0) {
                            ItemTypeOne("How easy was that!")
                        } else {
                            ItemTypeTwo("Insanely easy", "See you later old android")
                        }
                    }
                    MyMultipleItemList(
                        modifier = Modifier.fillMaxSize(),
                        dataItems,
                    )
                }
            }
        }
    }
}

sealed class DataItem
data class ItemTypeOne(val text: String) : DataItem()
data class ItemTypeTwo(
    val text: String,
    val description: String,
) : DataItem()

@Composable
fun MyMultipleItemList(
    modifier: Modifier = Modifier,
    dataItems: List<DataItem>,
) {
    LazyColumn(modifier = modifier) {
        items(dataItems) { data ->
            when (data) {
                is ItemTypeOne -> ItemOne(itemTypeOne = data)
                is ItemTypeTwo -> ItemTwo(itemTypeTwo = data)
            }
        }
    }
}

@Composable
fun ItemOne(itemTypeOne: ItemTypeOne) {
    Text(text = itemTypeOne.text)
}

@Composable
fun ItemTwo(itemTypeTwo: ItemTypeTwo) {
    Column {
        Text(text = itemTypeTwo.text)
        Text(text = itemTypeTwo.description)
    }
}
