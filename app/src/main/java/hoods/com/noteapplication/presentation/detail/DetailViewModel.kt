package hoods.com.noteapplication.presentation.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import hoods.com.noteapplication.data.local.model.Note
import hoods.com.noteapplication.domain.use_cases.AddUseCase
import hoods.com.noteapplication.domain.use_cases.AddUseCase_Factory
import hoods.com.noteapplication.domain.use_cases.GetNoteByIdUseCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date

class DetailViewModel @AssistedInject constructor(
    private val addUseCase: AddUseCase,
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    @Assisted private val noteId: Long,
) : ViewModel() {
    var state by mutableStateOf(DetailState())
        private set
    val isFormNotBlank: Boolean
        get() = state.title.isNotEmpty() &&
                state.content.isNotEmpty()
    private val note: Note
        get() = state.run {
            Note(
                id = id,
                title = title,
                content = content,
                createdDate = createdDate,
               isBookMarked = isBookmark
            )
        }

        init {
            initialize()
        }


    private fun initialize() {
        val isUpdatingNote = noteId != -1L
        state = state.copy(isUpdatingNote = isUpdatingNote)
        if (isUpdatingNote) {
            getNoteById()
        }
    }

    private fun getNoteById() = viewModelScope.launch {
        getNoteByIdUseCase(noteId).collectLatest { note ->
            state = state.copy(
                id = note.id,
                title = note.title,
                content = note.content,
                isBookmark = note.isBookMarked,
                createdDate = note.createdDate
            )
        }
    }

    fun onTitleChange(title: String) {
        state = state.copy(title = title)
    }

    fun onContentChange(content: String) {
        state = state.copy(content = content)
    }

    fun onBookMarkChange(isBookmark: Boolean) {
        state = state.copy(isBookmark = isBookmark)
    }

    fun addOrUpdateNote() = viewModelScope.launch {
        addUseCase(note = note)
    }


}

data class DetailState(
    val id: Long = 0,
    val title: String = "",
    val content: String = "",
    val isBookmark: Boolean = false,
    val createdDate: Date = Date(),
    val isUpdatingNote: Boolean = false,
)


@Suppress("UNCHECKED_CAST")
class DetailedViewModelFactory(
    private val noteId: Long,
    private val assistedFactory: DetailAssistedFactory,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return assistedFactory.create(noteId) as T
    }
}

@AssistedFactory
interface DetailAssistedFactory {
    fun create(noteId: Long): DetailViewModel
}















