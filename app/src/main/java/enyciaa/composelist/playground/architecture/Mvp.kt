package enyciaa.composelist.playground.architecture

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import enyciaa.composelist.playground.Todo
import enyciaa.composelist.playground.TodoViewModel
import enyciaa.composelist.playground.theming.ComposeListPlaygroundTheme
import kotlinx.coroutines.flow.*
import kotlin.random.Random

class MvpActivity : AppCompatActivity() {

    private val mvpViewModel = MvpViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeListPlaygroundTheme {
                MvpDestination(mvpViewModel = mvpViewModel)
            }
        }
    }
}

@Composable
fun MvpDestination(
    mvpViewModel: MvpViewModel
) {
    Surface(color = MaterialTheme.colors.background) {
        val viewState = mvpViewModel.viewStateStream().collectAsState()
        TodoList(
            modifier = Modifier.fillMaxSize(),
            todos = viewState.value.todoList,
            onTodoChecked = { todo, isChecked ->
                mvpViewModel.onAction(TodoViewModel.UiAction.TodoCompleted(todo, isChecked))
            }
        )
    }
}

class MvpViewModel {

    private val todoRepository = TodoRepository()
    private val viewStatePublisher: MutableStateFlow<ViewState> = MutableStateFlow(initViewState())
    private val lastViewState: ViewState
        get() = viewStatePublisher.value

    private fun initViewState(): ViewState {
        val todos = todoRepository.fetchTodos()
        return ViewState(todoList = todos)
    }

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.TodoCompleted -> {
                // Alternatively, filter out the item
//                val newList = lastViewState.todoList.filter { it.id != uiAction.todo.id }
                val newList = lastViewState.todoList.map {
                    if (it.id == uiAction.todo.id) {
                        it.copy(complete = uiAction.isCompleted)
                    } else {
                        it
                    }
                }
                emit(lastViewState.copy(todoList = newList))
            }
        }
    }

    data class ViewState(
        val todoList: List<Todo>
    )

    sealed class UiAction {
        class TodoCompleted(val todo: Todo, val isCompleted: Boolean) : UiAction()
    }

    private fun emit(viewState: ViewState) {
        viewStatePublisher.value = viewState
    }

    fun viewStateStream(): StateFlow<ViewState> {
        return viewStatePublisher.asStateFlow()
    }
}

class TodoRepository {

    fun fetchTodos(): List<Todo> {
        return (0..100).map {
            Todo(
                id = it,
                title = "Item - $it",
                complete = Random.nextInt(0, 3) == 0,
            )
        }
    }
}

data class Todo(
    val id: Int,
    val title: String,
    val complete: Boolean,
)
