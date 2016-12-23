package io.connorwyatt.flashcards.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.app.NavUtils
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import io.connorwyatt.flashcards.R
import io.connorwyatt.flashcards.data.entities.legacy.CategoryLegacy
import io.connorwyatt.flashcards.data.entities.legacy.FlashcardLegacy
import io.connorwyatt.flashcards.data.services.legacy.CategoryServiceLegacy
import io.connorwyatt.flashcards.data.services.legacy.FlashcardServiceLegacy
import java.util.ArrayList

class FlashcardDetailsActivity : BaseActivity()
{
    private var flashcard: FlashcardLegacy? = FlashcardLegacy()
    private var titleLayout: TextInputLayout? = null
    private var title: TextInputEditText? = null
    private var titleTouched = false
    private var textLayout: TextInputLayout? = null
    private var text: TextInputEditText? = null
    private var textTouched = false
    private var categoriesLayout: TextInputLayout? = null
    private var categories: TextInputEditText? = null
    private var saveButton: Button? = null

    private val categoriesError: String?
        get()
        {
            val categoryNames = splitCategoriesString(categories!!.text.toString())

            categoryNames.forEach {
                if (it.length > 40)
                {
                    return getString(R.string.validation_tags_max_length, 40)
                }
            }

            return null
        }

    private val textError: String?
        get()
        {
            val value = text!!.text.toString()

            if (value.isEmpty())
            {
                return getString(R.string.validation_required)
            }
            else
            {
                return null
            }
        }

    private val titleError: String?
        get()
        {
            val value = title!!.text.toString()

            if (value.isEmpty())
            {
                return getString(R.string.validation_required)
            }
            else if (value.length > 80)
            {
                return getString(R.string.validation_max_length, value.length, 80)
            }
            else
            {
                return null
            }
        }

    private val isCreate: Boolean
        get() = flashcard == null

    private val isValid: Boolean
        get() = titleError == null && textError == null && categoriesError == null

    private fun setViewFromFlashcard(flashcard: FlashcardLegacy)
    {
        if (flashcard.title != null && flashcard.title!!.isNotEmpty())
            title!!.setText(flashcard.title)

        if (flashcard.text != null && flashcard.text!!.isNotEmpty())
            text!!.setText(flashcard.text)

        if (flashcard.categories.isNotEmpty())
            categories!!.setText(flashcard.categoriesString)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard_details)

        val toolbar = findViewById(R.id.flashcard_details_toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowTitleEnabled(false)

        title = findViewById(R.id.flashcard_details_title) as TextInputEditText
        titleLayout = findViewById(R.id.flashcard_details_title_layout) as TextInputLayout
        text = findViewById(R.id.flashcard_details_text) as TextInputEditText
        textLayout = findViewById(R.id.flashcard_details_text_layout) as TextInputLayout
        categories = findViewById(R.id.flashcard_details_categories) as TextInputEditText
        categoriesLayout = findViewById(R.id.flashcard_details_categories_layout) as TextInputLayout
        saveButton = findViewById(R.id.flashcard_details_save_button) as Button

        setInputListeners()

        if (intent.hasExtra(INTENT_EXTRAS.FLASHCARD_ID))
        {
            val id = intent.getLongExtra(INTENT_EXTRAS.FLASHCARD_ID, -1)

            val flashcardService = FlashcardServiceLegacy(this)
            flashcard = flashcardService.getById(id)

            setViewFromFlashcard(flashcard!!)
        }
        else if (intent.hasExtra(INTENT_EXTRAS.CATEGORY_ID))
        {
            val categoryId = intent.getLongExtra(INTENT_EXTRAS.CATEGORY_ID, -1)

            val categoryService = CategoryServiceLegacy(this)
            val category = categoryService.getById(categoryId)

            flashcard!!.categories.add(category)

            setViewFromFlashcard(flashcard!!)
        }

        updateButton()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        menuInflater.inflate(R.menu.activity_flashcard_details_menu, menu)

        if (isCreate)
        {
            menu.findItem(R.id.action_delete).isEnabled = false
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        when (item.itemId)
        {
            R.id.action_delete ->
            {
                delete()
                return true
            }

            else               -> return super.onOptionsItemSelected(item)
        }
    }

    fun onSaveClick(view: View)
    {
        save()
    }

    private fun save()
    {
        flashcard!!.title = title!!.text.toString()
        flashcard!!.text = text!!.text.toString()
        flashcard!!.categories =
            processCategoriesString(categories!!.text.toString()).toMutableList()

        val flashcardService = FlashcardServiceLegacy(this)
        flashcard = flashcardService.save(flashcard!!)

        showToast(R.string.save_toast)

        setViewFromFlashcard(flashcard!!)
        this.invalidateOptionsMenu()
    }

    private fun delete()
    {
        val flashcardService = FlashcardServiceLegacy(this)
        flashcardService.delete(flashcard!!)

        showToast(R.string.flashcard_details_delete_toast)

        NavUtils.navigateUpFromSameTask(this)
    }

    private fun showToast(messageStringId: Int)
    {
        val context = applicationContext
        val toastMessage = getString(messageStringId)
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(context, toastMessage, duration)
        toast.show()
    }

    private fun setInputListeners()
    {
        title!!.onFocusChangeListener = View.OnFocusChangeListener { view, isFocused ->
            if (!isFocused)
            {
                titleTouched = true

                val error = titleError

                if (error != null)
                {
                    titleLayout!!.error = error
                }
                else
                {
                    titleLayout!!.isErrorEnabled = false
                }
            }
        }

        text!!.onFocusChangeListener = View.OnFocusChangeListener { view, isFocused ->
            if (!isFocused)
            {
                textTouched = true

                val error = textError

                if (error != null)
                {
                    textLayout!!.error = error
                }
                else
                {
                    textLayout!!.isErrorEnabled = false
                }
            }
        }

        title!!.addTextChangedListener(
            object : TextWatcher
            {
                override fun beforeTextChanged(charSequence: CharSequence,
                                               i: Int,
                                               i1: Int,
                                               i2: Int)
                {
                }

                override fun onTextChanged(charSequence: CharSequence,
                                           i: Int,
                                           i1: Int,
                                           i2: Int)
                {
                    titleTouched = true
                }

                override fun afterTextChanged(editable: Editable)
                {
                    val error = titleError

                    if (error != null)
                    {
                        titleLayout!!.error = error
                    }
                    else
                    {
                        titleLayout!!.isErrorEnabled = false
                    }

                    updateButton()
                }
            }
        )

        text!!.addTextChangedListener(
            object : TextWatcher
            {
                override fun beforeTextChanged(charSequence: CharSequence,
                                               i: Int,
                                               i1: Int,
                                               i2: Int)
                {
                }

                override fun onTextChanged(charSequence: CharSequence,
                                           i: Int,
                                           i1: Int,
                                           i2: Int)
                {
                    textTouched = true
                }

                override fun afterTextChanged(editable: Editable)
                {
                    val error = textError

                    if (error != null)
                    {
                        textLayout!!.error = error
                    }
                    else
                    {
                        textLayout!!.isErrorEnabled = false
                    }

                    updateButton()
                }
            }
        )

        categories!!.onFocusChangeListener = View.OnFocusChangeListener { view, isFocused ->
            if (!isFocused)
            {
                val error = categoriesError

                if (error != null)
                {
                    categoriesLayout!!.error = error
                }
                else
                {
                    categoriesLayout!!.isErrorEnabled = false
                }
            }
        }

        categories!!.addTextChangedListener(
            object : TextWatcher
            {
                override fun beforeTextChanged(charSequence: CharSequence,
                                               i: Int,
                                               i1: Int,
                                               i2: Int)
                {
                }

                override fun onTextChanged(charSequence: CharSequence,
                                           i: Int,
                                           i1: Int,
                                           i2: Int)
                {
                }

                override fun afterTextChanged(editable: Editable)
                {
                    val error = categoriesError

                    if (error != null)
                    {
                        categoriesLayout!!.error = error
                    }
                    else
                    {
                        categoriesLayout!!.isErrorEnabled = false
                    }

                    updateButton()
                }
            }
        )
    }

    private fun updateButton()
    {
        if (isValid)
        {
            saveButton!!.isEnabled = true
        }
        else
        {
            saveButton!!.isEnabled = false
        }
    }

    private fun splitCategoriesString(categoriesString: String): List<String>
    {
        val categories = ArrayList<String>()

        val categoryNames = categoriesString.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        for (categoryName in categoryNames)
        {
            val trimmedString = categoryName.trim { it <= ' ' }

            if (trimmedString.isNotEmpty())
            {
                categories.add(trimmedString)
            }
        }

        return categories
    }

    private fun processCategoriesString(categoriesString: String): List<CategoryLegacy>
    {
        val categories = ArrayList<CategoryLegacy>()

        val categoryNames = splitCategoriesString(categoriesString)

        for (categoryName in categoryNames)
        {
            val category = CategoryLegacy()
            category.name = categoryName
            categories.add(category)
        }

        return categories
    }

    object INTENT_EXTRAS
    {
        var FLASHCARD_ID = "FLASHCARD_ID"
        var CATEGORY_ID = "CATEGORY_ID"
    }

    companion object
    {

        fun startActivity(context: Context)
        {
            val intent = Intent(context, FlashcardDetailsActivity::class.java)

            context.startActivity(intent)
        }

        fun startActivityWithFlashcard(context: Context, flashcard: FlashcardLegacy)
        {
            val intent = Intent(context, FlashcardDetailsActivity::class.java)

            intent.putExtra(INTENT_EXTRAS.FLASHCARD_ID, flashcard.id)

            context.startActivity(intent)
        }

        fun startActivityWithCategory(context: Context, category: CategoryLegacy)
        {
            val intent = Intent(context, FlashcardDetailsActivity::class.java)

            intent.putExtra(INTENT_EXTRAS.CATEGORY_ID, category.id)

            context.startActivity(intent)
        }
    }
}
