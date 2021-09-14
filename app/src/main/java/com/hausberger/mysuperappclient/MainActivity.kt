package com.hausberger.mysuperappclient

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AppCompatActivity
import com.hausberger.mysuperappclient.databinding.ActivityMainBinding

const val READ_REQUEST_CODE = 555

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fileChooserButton.setOnClickListener {
            launchFileChooser()
        }
    }

    fun launchFileChooser() {
        // Opec document
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file browser.
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)

        // Filter to only show results that can be "opened", such as a file (as opposed to a list
        // of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers, it would be
        // "*/*".
        intent.type = "*/*";

        startActivityForResult(intent, READ_REQUEST_CODE)

          // Create document
//        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
//        // will trigger exception if no  appropriate category passed
//        // will trigger exception if no  appropriate category passed
//        intent.addCategory(Intent.CATEGORY_OPENABLE)
//        // or whatever mimeType you want
//        // or whatever mimeType you want
//        intent.type = "*/*"
//        intent.putExtra(Intent.EXTRA_TITLE, "file_name_to_save_as")
//        startActivityForResult(intent, 556)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.  Pull that uri using "resultData.getData()"
            var uri: Uri?
            data?.let {
                uri = data.data
                val mime = MimeTypeMap.getSingleton()
                when (mime.getExtensionFromMimeType(contentResolver.getType(it.data!!))) {
                    "jpg" -> {
                        showImage(uri)
                    }

                    "txt" -> {
                        showText(uri)
                    }
                }


                //renameDocument(data.data)
            }
        }
    }

    /**
     * Given the URI of an image, shows it on the screen using a DialogFragment.
     *
     * @param uri the Uri of the image to display.
     */
    private fun showImage(uri: Uri?) {
        uri?.let {
            // Since the URI is to an image, create and show a DialogFragment to display the
            // image to the user.
            val imageDialog = ImageDialogFragment()
            val fragmentArguments = Bundle()
            fragmentArguments.putParcelable("URI", uri)
            imageDialog.arguments = fragmentArguments
            imageDialog.show(supportFragmentManager, "image_dialog")
        }
    }

    /**
     * Given the URI of a text, shows it on the screen using a DialogFragment.
     *
     * @param uri the Uri of the text to display.
     */
    private fun showText(uri: Uri?) {
        uri?.let {
            // Since the URI is to an image, create and show a DialogFragment to display the
            // image to the user.
            val textDialog = TextDialogFragment()
            val fragmentArguments = Bundle()
            fragmentArguments.putParcelable("URI", uri)
            textDialog.arguments = fragmentArguments
            textDialog.show(supportFragmentManager, "image_dialog")
        }
    }

    private fun renameDocument(uri: Uri?) {
        uri?.let {
            DocumentsContract.renameDocument(contentResolver, uri, "renamed_name")
        }
    }
}