package com.sweetshop.admin.ui.products

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sweetshop.admin.data.api.AdminApi
import com.sweetshop.admin.data.dto.CategoryDto
import com.sweetshop.admin.data.dto.CreateProductRequest
import com.sweetshop.admin.data.dto.ProductDto
import com.sweetshop.admin.data.dto.ProductListDto
import com.sweetshop.admin.data.dto.UpdateProductRequest
import com.sweetshop.admin.domain.repository.CategoryRepository
import com.sweetshop.admin.domain.repository.ProductRepository
import com.sweetshop.admin.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

data class ProductListState(
    val isLoading: Boolean = false,
    val products: List<ProductListDto> = emptyList(),
    val categories: List<CategoryDto> = emptyList(),
    val searchQuery: String = "",
    val selectedCategoryId: Long? = null,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val hasNext: Boolean = false,
    val error: String? = null
)

data class ProductFormState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val product: ProductDto? = null,
    val name: String = "",
    val description: String = "",
    val categoryId: Long = 0,
    val imageUrl: String = "",
    val selectedImageUri: Uri? = null,
    val isUploadingImage: Boolean = false,
    val price: String = "",
    val discountPrice: String = "",
    val unit: String = "500g",
    val stockQuantity: String = "",
    val minimumOrderQuantity: String = "1",
    val isAvailable: Boolean = true,
    val isFeatured: Boolean = false,
    val isBestseller: Boolean = false,
    val ingredients: String = "",
    val allergenInfo: String = "",
    val categories: List<CategoryDto> = emptyList(),
    val error: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val adminApi: AdminApi,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _listState = MutableStateFlow(ProductListState())
    val listState: StateFlow<ProductListState> = _listState.asStateFlow()

    private val _formState = MutableStateFlow(ProductFormState())
    val formState: StateFlow<ProductFormState> = _formState.asStateFlow()

    init {
        loadProducts()
        loadCategories()
    }

    fun loadProducts(page: Int = 0) {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }
            val search = _listState.value.searchQuery.ifBlank { null }
            val categoryId = _listState.value.selectedCategoryId
            when (val result = productRepository.getProducts(page, 20, search, categoryId)) {
                is Resource.Success -> {
                    val data = result.data
                    _listState.update {
                        it.copy(
                            isLoading = false,
                            products = if (page == 0) data.content else it.products + data.content,
                            currentPage = data.page,
                            totalPages = data.totalPages,
                            hasNext = data.hasNext
                        )
                    }
                }
                is Resource.Error -> {
                    _listState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            when (val result = categoryRepository.getCategories()) {
                is Resource.Success -> {
                    _listState.update { it.copy(categories = result.data) }
                    _formState.update { it.copy(categories = result.data) }
                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        }
    }

    fun onSearchChange(query: String) {
        _listState.update { it.copy(searchQuery = query) }
        loadProducts(0)
    }

    fun onCategoryFilter(categoryId: Long?) {
        _listState.update { it.copy(selectedCategoryId = categoryId) }
        loadProducts(0)
    }

    fun loadNextPage() {
        if (_listState.value.hasNext && !_listState.value.isLoading) {
            loadProducts(_listState.value.currentPage + 1)
        }
    }

    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            when (val result = productRepository.deleteProduct(productId)) {
                is Resource.Success -> {
                    _listState.update { state ->
                        state.copy(products = state.products.filter { it.id != productId })
                    }
                }
                is Resource.Error -> {
                    _listState.update { it.copy(error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun loadProduct(productId: Long) {
        viewModelScope.launch {
            _formState.update { it.copy(isLoading = true) }
            when (val result = productRepository.getProduct(productId)) {
                is Resource.Success -> {
                    val p = result.data
                    _formState.update {
                        it.copy(
                            isLoading = false,
                            product = p,
                            name = p.name,
                            description = p.description ?: "",
                            categoryId = p.categoryId,
                            imageUrl = p.imageUrl ?: "",
                            price = p.price.toString(),
                            discountPrice = p.discountPrice?.toString() ?: "",
                            unit = p.unit ?: "",
                            stockQuantity = p.stockQuantity.toString(),
                            minimumOrderQuantity = p.minimumOrderQuantity.toString(),
                            isAvailable = p.isAvailable,
                            isFeatured = p.isFeatured,
                            isBestseller = p.isBestseller,
                            ingredients = p.ingredients ?: "",
                            allergenInfo = p.allergenInfo ?: ""
                        )
                    }
                }
                is Resource.Error -> {
                    _formState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun onFormFieldChange(field: String, value: Any) {
        _formState.update { state ->
            when (field) {
                "name" -> state.copy(name = value as String)
                "description" -> state.copy(description = value as String)
                "categoryId" -> state.copy(categoryId = value as Long)
                "imageUrl" -> state.copy(imageUrl = value as String)
                "price" -> state.copy(price = value as String)
                "discountPrice" -> state.copy(discountPrice = value as String)
                "unit" -> state.copy(unit = value as String)
                "stockQuantity" -> state.copy(stockQuantity = value as String)
                "minimumOrderQuantity" -> state.copy(minimumOrderQuantity = value as String)
                "isAvailable" -> state.copy(isAvailable = value as Boolean)
                "isFeatured" -> state.copy(isFeatured = value as Boolean)
                "isBestseller" -> state.copy(isBestseller = value as Boolean)
                "ingredients" -> state.copy(ingredients = value as String)
                "allergenInfo" -> state.copy(allergenInfo = value as String)
                else -> state
            }
        }
    }

    fun onImageSelected(uri: Uri) {
        _formState.update { it.copy(selectedImageUri = uri) }
        uploadImage(uri)
    }

    private fun uploadImage(uri: Uri) {
        viewModelScope.launch {
            _formState.update { it.copy(isUploadingImage = true) }
            try {
                val inputStream = appContext.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes() ?: return@launch
                inputStream.close()

                val mimeType = appContext.contentResolver.getType(uri) ?: "image/jpeg"
                val extension = when {
                    mimeType.contains("png") -> "png"
                    mimeType.contains("webp") -> "webp"
                    else -> "jpg"
                }
                val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData(
                    "file", "product_image.$extension", requestBody
                )

                val response = adminApi.uploadFile(filePart)
                if (response.isSuccessful && response.body()?.success == true) {
                    val fileUrl = response.body()!!.data!!["fileUrl"] ?: ""
                    _formState.update { it.copy(isUploadingImage = false, imageUrl = fileUrl) }
                } else {
                    _formState.update { it.copy(isUploadingImage = false, error = "Failed to upload image") }
                }
            } catch (e: Exception) {
                _formState.update { it.copy(isUploadingImage = false, error = "Image upload failed: ${e.message}") }
            }
        }
    }

    fun saveProduct() {
        val state = _formState.value
        if (state.name.isBlank()) {
            _formState.update { it.copy(error = "Product name is required") }
            return
        }
        if (state.price.toDoubleOrNull() == null) {
            _formState.update { it.copy(error = "Valid price is required") }
            return
        }
        if (state.categoryId == 0L) {
            _formState.update { it.copy(error = "Please select a category") }
            return
        }

        viewModelScope.launch {
            _formState.update { it.copy(isSaving = true, error = null) }

            val result = if (state.product != null) {
                productRepository.updateProduct(
                    state.product.id,
                    UpdateProductRequest(
                        name = state.name,
                        description = state.description.ifBlank { null },
                        categoryId = state.categoryId,
                        imageUrl = state.imageUrl.ifBlank { null },
                        price = state.price.toDoubleOrNull(),
                        discountPrice = state.discountPrice.toDoubleOrNull(),
                        unit = state.unit,
                        stockQuantity = state.stockQuantity.toIntOrNull(),
                        minimumOrderQuantity = state.minimumOrderQuantity.toIntOrNull(),
                        isAvailable = state.isAvailable,
                        isFeatured = state.isFeatured,
                        isBestseller = state.isBestseller,
                        ingredients = state.ingredients.ifBlank { null },
                        allergenInfo = state.allergenInfo.ifBlank { null }
                    )
                )
            } else {
                productRepository.createProduct(
                    CreateProductRequest(
                        name = state.name,
                        description = state.description.ifBlank { null },
                        categoryId = state.categoryId,
                        imageUrl = state.imageUrl.ifBlank { null },
                        price = state.price.toDouble(),
                        discountPrice = state.discountPrice.toDoubleOrNull(),
                        unit = state.unit,
                        stockQuantity = state.stockQuantity.toIntOrNull() ?: 0,
                        minimumOrderQuantity = state.minimumOrderQuantity.toIntOrNull() ?: 1,
                        isAvailable = state.isAvailable,
                        isFeatured = state.isFeatured,
                        isBestseller = state.isBestseller,
                        ingredients = state.ingredients.ifBlank { null },
                        allergenInfo = state.allergenInfo.ifBlank { null }
                    )
                )
            }

            when (result) {
                is Resource.Success -> {
                    _formState.update { it.copy(isSaving = false, isSaved = true) }
                }
                is Resource.Error -> {
                    _formState.update { it.copy(isSaving = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun resetFormState() {
        _formState.value = ProductFormState(categories = _listState.value.categories)
    }
}
