package io.connorwyatt.flashcards.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.adapters.CategoryListAdapter
import io.connorwyatt.flashcards.data.services.CategoryService
import io.connorwyatt.flashcards.data.viewmodels.CategoryViewModel
import io.reactivex.Observable

class CategoryListActivity : BaseActivity()
{
    private val categoryListAdapter = CategoryListAdapter()

    //region Activity

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_list)

        initialiseUI()
    }

    //endregion

    //region Data

    private fun getData(): Observable<List<CategoryViewModel>>
    {
        return CategoryService.getAll().flatMap { categories ->
            val observables = categories.map {
                CategoryViewModel.getFromCategory(it)
            }

            if (observables.isNotEmpty())
            {
                Observable.combineLatest(
                    observables,
                    { it.filterIsInstance(CategoryViewModel::class.java) }
                )
            }
            else
            {
                Observable.just(listOf())
            }
        }
    }

    //endregion

    //region UI

    private fun initialiseUI(): Unit
    {
        initialiseRecycler()
    }

    private fun initialiseRecycler(): Unit
    {
        getData().subscribe {
            updateAdapterData(it)
        }

        val recycler = findViewById(R.id.category_list_recycler) as RecyclerView
        recycler.adapter = categoryListAdapter
        recycler.layoutManager = LinearLayoutManager(this)
    }

    private fun updateAdapterData(viewModels: List<CategoryViewModel>): Unit
    {
        categoryListAdapter.setItems(viewModels)
    }

    //endregion

    companion object
    {
        fun startActivity(context: Context)
        {
            val intent = Intent(context, CategoryListActivity::class.java)

            context.startActivity(intent)
        }
    }
}
