package design.sandwwraith.networkdemo

import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    lateinit var downloadTask: DownloadPostsAsyncTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            downloadTask = DownloadPostsAsyncTask()
            downloadTask.execute(URL("https://jsonplaceholder.typicode.com/posts/1"))
        }

        text_view.setOnClickListener {
            downloadTask.cancel(true)
        }
    }

    inner class DownloadPostsAsyncTask: AsyncTask<URL, Int, String>() {
        override fun onPreExecute() {
            Log.d(logTag, "onPreExecute: ${Thread.currentThread().name}")
        }

        override fun onProgressUpdate(vararg values: Int?) {
            text_view.text = getString(R.string.progress_message, values[0])
        }

        override fun onPostExecute(result: String?) {
            Log.d(logTag, "onPostExecute: ${Thread.currentThread().name}")
            text_view.text = getString(R.string.result_message, result)
        }

        private val logTag = "ASYNC_TASK"

        override fun onCancelled(result: String?) {
            Log.d(logTag, "Cancelled with result: $result")
            text_view.text = "Cancelled"
        }

        override fun onCancelled() {
            Log.d(logTag, "Cancelled without result")
            text_view.text = "Cancelled"
        }

        override fun doInBackground(vararg params: URL): String {
            Log.d(logTag, "doInBackground: ${Thread.currentThread().name}")
            publishProgress(0)
            val response = params[0].openConnection().run {
                connect()
                val code = (this as? HttpURLConnection)?.responseCode
                Log.d(logTag, "Response code: $code")

                getInputStream().bufferedReader().readLines().joinToString("")
            }
            Log.d(logTag, "Response: $response")
            publishProgress(50)
            Thread.sleep(1000)
            publishProgress(100)
            return response
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
