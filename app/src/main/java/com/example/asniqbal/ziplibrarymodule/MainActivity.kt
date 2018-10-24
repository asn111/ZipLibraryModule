package com.example.asniqbal.ziplibrarymodule

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.annotation.RequiresApi
import android.support.v7.app.AlertDialog
import android.util.Log
import com.zubair.permissionmanager.PermissionManager
import com.zubair.permissionmanager.PermissionUtils
import com.zubair.permissionmanager.enums.PermissionEnum
import com.zubair.permissionmanager.interfaces.FullCallback
import java.io.File
import java.net.URISyntaxException

class MainActivity : AppCompatActivity(), FullCallback {


    var REQUEST_CHOOSER = 1234
    var REQUEST_CHOSER2 = 1122
    lateinit var filee: File
    val oPath = Environment.getExternalStorageDirectory().path + File.separator + "Documents/name" + File.separator + "myZip2222.zip"
    val pathO = Environment.getExternalStorageDirectory().path + File.separator + "Documents"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun readFile() {


        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Zip"),
                    REQUEST_CHOOSER
            )
        } catch (ex: android.content.ActivityNotFoundException) {


        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun readDir() {


        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Zip"),
                    REQUEST_CHOSER2
            )
        } catch (ex: android.content.ActivityNotFoundException) {


        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CHOOSER && resultCode == Activity.RESULT_OK) {
            val uri = data!!.data
            Log.i("URI11", "File Uri: " + uri!!.toString())
            val path = getPath(this, uri)
            Log.i("FILE PATH11", "File Path: $path")

            filee = File(path)


        }
        if (requestCode == REQUEST_CHOSER2 && resultCode == Activity.RESULT_OK) {
            val uri = data!!.data
            Log.i("URI22", "File Uri: " + uri!!.toString())
            var path = uri.path

            Log.i("FILE PATH22", "File Path: $path")


        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    @Throws(URISyntaxException::class)
    fun getPath(context: Context, uri: Uri): String? {
        if ("content".equals(uri.scheme, ignoreCase = true)) {
            val projection = arrayOf("_data")
            var cursor: Cursor?

            try {
                cursor = context.contentResolver.query(uri, projection, null, null, null)
                val column_index = cursor.getColumnIndexOrThrow("_data")
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index)
                }
            } catch (e: Exception) {

            }

        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }

        return null
    }


    fun reqStoragePermission() {
        PermissionManager.Builder().key(1)
                .permission(PermissionEnum.READ_EXTERNAL_STORAGE, PermissionEnum.WRITE_EXTERNAL_STORAGE)
                .callback(this@MainActivity)
                .ask(this@MainActivity)
    }

    fun isStoragePermissionGranted(): Boolean {
        var flag = false

        if (PermissionUtils.isGranted(this, PermissionEnum.WRITE_EXTERNAL_STORAGE) &&
                PermissionUtils.isGranted(this, PermissionEnum.READ_EXTERNAL_STORAGE)) {
            flag = true
        }
        return flag
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        PermissionManager.handleResult(this, requestCode, permissions, grantResults)
    }

    override fun result(permissionsGranted: ArrayList<PermissionEnum>, permissionsDenied: ArrayList<PermissionEnum>,
                        permissionsDeniedForever: ArrayList<PermissionEnum>, permissionsAsked: ArrayList<PermissionEnum>) {
        if (permissionsGranted.size == permissionsAsked.size) {
            //Do some action

        } else if (permissionsDeniedForever.size > 0) {
            //If user answer "Never ask again" to a request for permission, you can redirect user to app settings, with an utils
            showDialog(true)
        } else {
            showDialog(false)
        }
    }


    fun showDialog(isNeverAskAgainChecked: Boolean) {
        AlertDialog.Builder(this)
                .setTitle("Permission needed")
                .setMessage("Give Permissions Text Here")
                .setPositiveButton(android.R.string.ok) { dialogInterface, i ->

                    if (!isNeverAskAgainChecked) {
                        reqStoragePermission()
                    } else {
                        PermissionUtils.openApplicationSettings(this@MainActivity, R::class.java.getPackage().name)
                    }
                }
                .setNegativeButton(android.R.string.cancel) { dialogInterface, i -> dialogInterface.dismiss() }
                .show()
    }

}
