package enyciaa.composelist.playground

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
import enyciaa.composelist.playground.theming.ComposeListPlaygroundTheme
import kotlinx.coroutines.flow.*
import kotlin.random.Random

class TodoActivity : AppCompatActivity() {

    private val todoViewModel = TodoViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeListPlaygroundTheme {
                TodoScreen(todoViewModel = todoViewModel)
            }
        }
    }
}

@Composable
fun TodoScreen(
    todoViewModel: TodoViewModel
) {
    Surface(color = MaterialTheme.colors.background) {
        val viewState = todoViewModel.viewStateStream().collectAsState(todoViewModel.defaultViewState())
        TodoList(
            modifier = Modifier.fillMaxSize(),
            todos = viewState.value.todoList,
            onTodoChecked = { todo, isChecked ->
                todoViewModel.onAction(TodoViewModel.UiAction.TodoCompleted(todo, isChecked))
            }
        )
    }
}

@Composable
fun TodoList(
    modifier: Modifier = Modifier,
    todos: List<Todo>,
    onTodoChecked: (todo: Todo, isChecked: Boolean) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = paddingValues(16.dp, 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(todos) { todo ->
            TodoItem(
                modifier = Modifier.fillMaxWidth(),
                todo = todo,
                onTodoChecked = onTodoChecked
            )
        }
    }
}

@Composable
fun TodoItem(
    modifier: Modifier,
    todo: Todo,
    onTodoChecked: (todo: Todo, isChecked: Boolean) -> Unit,
) {
    Card(modifier = modifier, elevation = 8.dp) {
        Row(modifier = Modifier.padding(8.dp)) {
            Text(text = todo.title)
            Spacer(modifier = Modifier.weight(1f))
            Checkbox(
                checked = todo.complete,
                onCheckedChange = { isChecked -> onTodoChecked(todo, isChecked) }
            )
        }
    }
}

class TodoViewModel {

    private val todoRepository = TodoRepository()
    private val viewStatePublisher: MutableStateFlow<ViewState> = MutableStateFlow(defaultViewState())
    private val lastViewState: ViewState
        get() = viewStatePublisher.value

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.TodoCompleted -> {
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

    fun defaultViewState(): ViewState {
        val todos = todoRepository.fetchTodos()
        return ViewState(todoList = todos)
    }

    data class ViewState(
        val todoList: List<Todo>
    )

    sealed class UiAction {
        class TodoCompleted(val todo: Todo, val isCompleted: Boolean) : UiAction()
    }

    protected fun emit(viewState: ViewState) {
        viewStatePublisher.value = viewState
    }

    fun viewStateStream(): Flow<ViewState> {
        return viewStatePublisher.asStateFlow()
    }
}

class TodoRepository {

    fun fetchTodos(): List<Todo> {
        return (0..100).map {
            Todo(
                id = it,
                title = LoremIpsum(3).values.joinToString(" ") { it },
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
