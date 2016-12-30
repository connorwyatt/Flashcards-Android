package io.connorwyatt.flashcards.data.services

import io.connorwyatt.flashcards.data.datasources.CategoryDataSource
import io.connorwyatt.flashcards.data.entities.Category
import io.reactivex.Observable

object CategoryService
{
    fun getAll(): Observable<List<Category>>
        = CategoryDataSource().getAll()

    fun getById(id: String): Observable<Category>
        = CategoryDataSource().getById(id)

    fun getByName(name: String): Observable<List<Category>>
    {
        val normalisedName = normaliseCategoryName(name)

        return CategoryDataSource().getByName(normalisedName)
    }

    fun createCategoriesByName(categoryNames: List<String>): Observable<List<Category>>
    {
        val observables = categoryNames.distinctBy { normaliseCategoryName(it) }
            .map { name ->
                getByName(name)
                    .flatMap { category ->
                        category.singleOrNull()?.let { return@flatMap Observable.just(it) }

                        val newCategory = Category(null)
                        newCategory.name = name

                        return@flatMap save(newCategory)
                    }
            }

        if (observables.isNotEmpty())
        {
            return Observable.combineLatest(observables,
                                            { it.filterIsInstance(Category::class.java) })
        }

        return Observable.just(listOf())
    }


    fun save(category: Category): Observable<Category>
    {
        return getByName(category.name!!).flatMap { categories ->
            if (categories.isEmpty())
            {
                return@flatMap CategoryDataSource().save(category).flatMap { getById(it) }
            }
            else
            {
                return@flatMap Observable.error<Category>(CategoryNameTakenException())
            }
        }
    }

    fun delete(category: Category): Observable<Any?>
        = CategoryDataSource().delete(category)

    fun deleteWithFlashcards(category: Category): Observable<Any?>
        = TODO("Stub Method") // TODO Replace with real method body

    class CategoryNameTakenException : Exception()
    {}

    private fun normaliseCategoryName(name: String): String = name.trim()
}
