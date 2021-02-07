package enyciaa.composelist.playground.architecture

import android.util.Log
import kotlinx.coroutines.delay

class AnswerService {

    suspend fun save(answer: String) {
        Log.v("Api call", "Make a blocking call to an api")
        delay(1000)
    }
}
