package com.example.training.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.training.data.mapper.toDomain
import com.example.training.model.Category
import com.example.training.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {

    private val categoryRepository = CategoryRepository()

    // État : Liste des catégories (Domain Models)
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    // État : Chargement
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // Démarrer l'observation des catégories au démarrage
        observeCategories()
    }

    /**
     * Observer les catégories en temps réel
     */
    private fun observeCategories() {
        viewModelScope.launch {
            _isLoading.value = true

            categoryRepository.observeCategories()
                .map { categoryDtos ->
                    // Convertir List<CategoryDto> → List<Category> avec le mapper
                    categoryDtos.map { it.toDomain() }
                }
                .catch { e ->
                    // Gérer l'erreur silencieusement (log si besoin)
                    _isLoading.value = false
                    // Si vous voulez afficher l'erreur à l'utilisateur, ajoutez un UiEvent ici
                }
                .collect { categoryList ->
                    _categories.value = categoryList
                    _isLoading.value = false
                }
        }
    }
}
