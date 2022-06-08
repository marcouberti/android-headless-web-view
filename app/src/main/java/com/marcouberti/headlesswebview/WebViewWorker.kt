package com.marcouberti.headlesswebview

import android.content.Context
import android.util.Base64
import android.webkit.WebView
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * A worker that executes Javascript code inside a WebView.
 */
class WebViewWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    private val ctx = appContext

    override suspend fun doWork(): Result {

        // Do the work here--in this case, upload the images.
        println("### Worker started...")
        withContext(Dispatchers.Main) {
            val myWebView = WebView(ctx)

            myWebView.settings.javaScriptEnabled = true
            myWebView.addJavascriptInterface(WebAppInterface(ctx), "Android")

            // Create an unencoded HTML string
            // then convert the unencoded HTML string into bytes, encode
            // it with Base64, and load the data.
            val unencodedHtml =
                """
               <html>
               <script type="text/javascript">
                   function showAndroidToast(toast) {
                       Android.showToast(toast);
                   }
               </script>
               <body onload="showAndroidToast('Hello headless WebView!')">
                    HEADLESS WEB VIEW, NO BODY REQUIRED
               </body>
               </html>
            """.trimIndent()
            val encodedHtml = Base64.encodeToString(unencodedHtml.toByteArray(), Base64.NO_PADDING)
            myWebView.loadData(encodedHtml, "text/html", "base64")
        }
        // Indicate whether the work finished successfully with the Result
        println("### Worker finished.")
        return Result.success()
    }
}
