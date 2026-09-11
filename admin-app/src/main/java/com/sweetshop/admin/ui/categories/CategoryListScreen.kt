package com.sweetshop.admin.ui.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sweetshop.admin.data.dto.CategoryDto
import com.sweetshop.admin.ui.components.ErrorState
import com.sweetshop.admin.ui.components.LoadingState
import com.sweetshop.admin.ui.theme.AdminPrimary
import com.sweetshop.admin.ui.theme.AdminSuccess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(
    onOpenDrawer: () -> Unit,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var deleteCategoryId by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categories", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AdminPrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddDialog() },
                containerColor = AdminPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Category")
            }
        }
    ) { padding ->
        when {
            state.isLoading && state.categories.isEmpty() -> {
                LoadingState(modifier = Modifier.padding(padding))
            }
            state.error != null && state.categories.isEmpty() -> {
                ErrorState(
                    message = state.error!!,
                    onRetry = { viewModel.loadCategories() },
                    modifier = Modifier.padding(padding)
                )
            }
            state.categories.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No categories found", color = Color.Gray)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.categories) { category ->
                        CategoryCard(
                            category = category,
                            onEdit = { viewModel.showEditDialog(category) },
                            onDelete = { deleteCategoryId = category.id },
                            onToggleStatus = { viewModel.toggleCategoryStatus(category) }
                        )
                    }
                }
            }
        }
    }

    // Add/Edit Dialog
    if (state.showDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDialog() },
            title = {
                Text(
                    if (state.editingCategory != null) "Edit Category" else "Add Category",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = state.dialogName,
                        onValueChange = { viewModel.onDialogNameChange(it) },
                        label = { Text("Category Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.dialogDescription,
                        onValueChange = { viewModel.onDialogDescriptionChange(it) },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3
                    )
                    OutlinedTextField(
                        value = state.dialogImage,
                        onValueChange = { viewModel.onDialogImageChange(it) },
                        label = { Text("Image URL") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    if (state.dialogError != null) {
                        Text(
                            text = state.dialogError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.saveCategory() },
                    enabled = !state.isSaving
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    } else {
                        Text(
                            if (state.editingCategory != null) "Update" else "Create",
                            color = AdminPrimary
                        )
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete confirmation
    if (deleteCategoryId != null) {
        AlertDialog(
            onDismissRequest = { deleteCategoryId = null },
            title = { Text("Delete Category") },
            text = { Text("Are you sure you want to delete this category?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        deleteCategoryId?.let { viewModel.deleteCategory(it) }
                        deleteCategoryId = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteCategoryId = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun CategoryCard(
    category: CategoryDto,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleStatus: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEdit() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AdminPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Category,
                    contentDescription = category.name,
                    tint = AdminPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                category.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }
                category.productCount?.let {
                    Text(
                        text = "$it products",
                        style = MaterialTheme.typography.labelSmall,
                        color = AdminPrimary,
                        fontSize = 11.sp
                    )
                }
            }

            Switch(
                checked = category.isActive,
                onCheckedChange = { onToggleStatus() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = AdminSuccess
                ),
                modifier = Modifier.padding(end = 4.dp)
            )

            IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = AdminPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
