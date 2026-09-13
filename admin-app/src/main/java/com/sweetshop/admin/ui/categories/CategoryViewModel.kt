package com.sweetshop.admin.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sweetshop.admin.data.dto.CategoryDto
import com.sweetshop.admin.data.dto.CreateCategoryRequest
import com.sweetshop.admin.data.dto.UpdateCategoryRequest
import com.sweetshop.admin.domain.repository.CategoryRepository
import com.sweetshop.admin.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryState(
    val isLoading: Boolean = false,
    val categories: List<CategoryDto> = emptyList(),
    val error: String? = null,
    val isSaving: Boolean = false,
    val showDialog: Boolean = false,
    val editingCategory: CategoryDto? = null,
    val dialogName: String = "",
    val dialogDescription: String = "",
    val dialogImage: String = "",
    val dialogError: String? = null
)

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CategoryState())
    val state: StateFlow<CategoryState> = _state.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = categoryRepository.getCategories()) {
                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false, categories = result.data) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun showAddDialog() {
        _state.update {
            it.copy(
                showDialog = true,
                editingCategory = null,
                dialogName = "",
                dialogDescription = "",
                dialogImage = "",
                dialogError = null
            )
        }
    }

    fun showEditDialog(category: CategoryDto) {
        _state.update {
            it.copy(
                showDialog = true,
                editingCategory = category,
                dialogName = category.name,
                dialogDescription = category.description ?: "",
                dialogImage = category.imageUrl ?: "",
                dialogError = null
            )
        }
    }

    fun dismissDialog() {
        _state.update { it.copy(showDialog = false, dialogError = null) }
    }

    fun onDialogNameChange(name: String) {
        _state.update { it.copy(dialogName = name, dialogError = null) }
    }

    fun onDialogDescriptionChange(description: String) {
        _state.update { it.copy(dialogDescription = description) }
    }

    fun onDialogImageChange(image: String) {
        _state.update { it.copy(dialogImage = image) }
    }

    fun saveCategory() {
        val currentState = _state.value
        if (currentState.dialogName.isBlank()) {
            _state.update { it.copy(dialogError = "Category name is required") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, dialogError = null) }

            val result = if (currentState.editingCategory != null) {
                categoryRepository.updateCategory(
                    currentState.editingCategory.id,
                    UpdateCategoryRequest(
                        name = currentState.dialogName,
                        description = currentState.dialogDescription.ifBlank { null },
                        image = currentState.dialogImage.ifBlank { null },
                        isActive = null,
                        sortOrder = null
                    )
                )
            } else {
                categoryRepository.createCategory(
                    CreateCategoryRequest(
                        name = currentState.dialogName,
                        description = currentState.dialogDescription.ifBlank { null },
                        image = currentState.dialogImage.ifBlank { null }
                    )
                )
            }

            when (result) {
                is Resource.Success -> {
                    _state.update { it.copy(isSaving = false, showDialog = false) }
                    loadCategories()
                }
                is Resource.Error -> {
                    _state.update { it.copy(isSaving = false, dialogError = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun toggleCategoryStatus(category: CategoryDto) {
        // Optimistic update
        val newStatus = !category.isActive
        _state.update { state ->
            state.copy(categories = state.categories.map {
                if (it.id == category.id) it.copy(isActive = newStatus) else it
            })
        }

        viewModelScope.launch {
            val result = categoryRepository.updateCategory(
                category.id,
                UpdateCategoryRequest(
                    name = null,
                    description = null,
                    image = null,
                    isActive = newStatus,
                    sortOrder = null
                )
            )
            when (result) {
                is Resource.Success -> loadCategories()
                is Resource.Error -> {
                    // Revert on failure
                    _state.update { state ->
                        state.copy(categories = state.categories.map {
                            if (it.id == category.id) it.copy(isActive = category.isActive) else it
                        })
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            when (val result = categoryRepository.deleteCategory(categoryId)) {
                is Resource.Success -> {
                    _state.update { state ->
                        state.copy(categories = state.categories.filter { it.id != categoryId })
                    }
                }
                is Resource.Error -> {
                    _state.update { it.copy(error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }
}
