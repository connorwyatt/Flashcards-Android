import android.content.Context
import io.connorwyatt.flashcards.data.datasources.FlashcardDataSource
import io.connorwyatt.flashcards.data.entities.Flashcard
import io.reactivex.Observable

class FlashcardService(private val context: Context)
{
    fun getAll(): Observable<List<Flashcard>>
    {
        val dataSource = FlashcardDataSource()

        return dataSource.getAll()
    }
}

