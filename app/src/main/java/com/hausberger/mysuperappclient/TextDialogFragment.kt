package com.hausberger.mysuperappclient

import android.annotation.SuppressLint
import android.app.Dialog
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import android.util.Log
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import java.io.BufferedReader
import java.io.FileDescriptor
import java.io.FileReader
import java.io.IOException


/** Create a Bitmap from the URI for that image and return it.
 *
 * @param uri the Uri for the image to return.
 */
class TextDialogFragment : DialogFragment() {

    private var mDialog: Dialog? = null
    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        uri = arguments?.getParcelable("URI")
    }

    override fun onStop() {
        super.onStop()
        if (dialog != null) {
            dialog!!.dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mDialog = super.onCreateDialog(savedInstanceState)

        // To optimize for the "lightbox" style layout.  Since we're not actually displaying a
        // title, remove the bar along the top of the fragment where a dialog title would
        // normally go.
        // To optimize for the "lightbox" style layout.  Since we're not actually displaying a
        // title, remove the bar along the top of the fragment where a dialog title would
        // normally go.
        mDialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        val textView = TextView(activity)
        textView.setTextColor(resources.getColor(R.color.white))
        textView.setPadding(50,50,50,50)
        mDialog?.setContentView(textView)

        textView.text = getTextFromUri(uri)
        return mDialog!!
    }

    private fun getTextFromUri(uri: Uri?): String? {
        var parcelFileDescriptor: ParcelFileDescriptor? = null

        return try {
            parcelFileDescriptor = requireActivity().contentResolver.openFileDescriptor(uri!!, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val fileReader = FileReader(fileDescriptor)
            val text = BufferedReader(fileReader).use {
                it.readText()
            }
            parcelFileDescriptor.close()
            text
        } catch (e: Exception) {
            Log.e("TAG", "Failed to load image.", e)
            null
        } finally {
            try {
                parcelFileDescriptor?.close()
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("TAG", "Error closing ParcelFile Descriptor")
            }
        }
    }

    /**
     * Grabs metadata for a document specified by URI, logs it to the screen.
     *
     * @param uri The uri for the document whose metadata should be printed.
     */
    fun dumpImageMetaData(uri: Uri?) {
        // BEGIN_INCLUDE (dump_metadata)

        // The query, since it only applies to a single document, will only return one row.
        // no need to filter, sort, or select fields, since we want all fields for one
        // document.
        val cursor: Cursor? = requireActivity().contentResolver
            .query(uri!!, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE), null, null, null, null)

        cursor.use { cursor ->
            // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (cursor != null && cursor.moveToFirst()) {

                // Note it's called "Display Name".  This is provider-specific, and
                // might not necessarily be the file name.
                val displayName: String = cursor.getString(
                    cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                )
                Log.i("TAG", "Display Name: $displayName")
                val sizeIndex: Int = cursor.getColumnIndex(OpenableColumns.SIZE)
                // If the size is unknown, the value stored is null.  But since an int can't be
                // null in java, the behavior is implementation-specific, which is just a fancy
                // term for "unpredictable".  So as a rule, check if it's null before assigning
                // to an int.  This will happen often:  The storage API allows for remote
                // files, whose size might not be locally known.
                var size: String? = null
                size = if (!cursor.isNull(sizeIndex)) {
                    // Technically the column stores an int, but cursor.getString will do the
                    // conversion automatically.
                    cursor.getString(sizeIndex)
                } else {
                    "Unknown"
                }
                Log.i("TAG", "Size: $size")
            }
        }
        // END_INCLUDE (dump_metadata)
    }
}