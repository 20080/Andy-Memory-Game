package com.example.memorygame

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memorygame.models.BoardSize
import com.example.memorygame.utilsvec.BitmapScaler
import com.example.memorygame.utilsvec.EXTRA_BOARD_SIZE
import com.example.memorygame.utilsvec.isPermissionGranted
import com.example.memorygame.utilsvec.requestPermission
import java.io.ByteArrayOutputStream

class CreateActivity : AppCompatActivity() {
    companion object{
        private const val PICK_PHOTO_CODE =512
        private const val READ_PHOTOS_PERMISSION = android.Manifest.permission.READ_EXTERNAL_STORAGE
        private const val READ_EXTERNAL_PHOTOS_CODE = 1024
        private const val TAG = "Create Activity"
        private const val MIN_GAME_NAME_LENGTH = 3
        private const val MAX_GAME_NAME_LENGTH = 14
    }
    private lateinit var boardSize: BoardSize
    private lateinit var rvImagePicker: RecyclerView
    private lateinit var btnSave: Button
    private lateinit var etGameName: EditText
    private lateinit var adapter: ImagePickerAdapter
    private var numImagesRequired = -1
    private val chosenImageUris = mutableListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        rvImagePicker = findViewById(R.id.rvImagePicker)
        btnSave = findViewById(R.id.btnSave)
        etGameName = findViewById(R.id.etGameName)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val boardSize = intent.getSerializableExtra(EXTRA_BOARD_SIZE) as BoardSize
        //boom
        numImagesRequired = boardSize.getNumPairs()
        supportActionBar?.title = "Choose pics (0/$numImagesRequired)"

        btnSave.setOnClickListener{
            saveDataToFirebase()
        }

        //Bc kya chutiyaapa hai
        etGameName.filters = arrayOf(InputFilter.LengthFilter(MAX_GAME_NAME_LENGTH))

        etGameName.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                btnSave.isEnabled = shouldEnableSaveButton()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        adapter = ImagePickerAdapter(this,chosenImageUris,boardSize, object :
            ImagePickerAdapter.ImageClickListener{
            override fun onPlaceHolderClicked() {
                if(isPermissionGranted(this@CreateActivity,READ_PHOTOS_PERMISSION)){
                    launchIntentForPhotos()
                }
                else
                    requestPermission(this@CreateActivity, READ_PHOTOS_PERMISSION, READ_EXTERNAL_PHOTOS_CODE)
            }

        })
        rvImagePicker.adapter = adapter
        rvImagePicker.setHasFixedSize(true)
        rvImagePicker.layoutManager = GridLayoutManager(this,boardSize.getWidth())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode== READ_EXTERNAL_PHOTOS_CODE){
            if(grantResults.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                launchIntentForPhotos()
            }
            else{
                Toast.makeText(this,"Permission Required",Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
        {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != PICK_PHOTO_CODE|| resultCode!= Activity.RESULT_OK||data == null){
            Log.w(TAG,"Did not get data back form launched activity")
            return
        }
        // there are two types of return 1 selectedUri & clipData
        val selectedUri = data.data
        val clipData = data.clipData

        if(clipData!=null){
            Log.i(TAG,"clipData numImages ${clipData.itemCount}: $clipData")
            for (i in 0 until clipData.itemCount){
                val clipItem = clipData.getItemAt(i)
                if (chosenImageUris.size<numImagesRequired){
                    chosenImageUris.add(clipItem.uri)
                }
            }
        }

        else if(selectedUri!=null){
            Log.i(TAG,"data $selectedUri")
            chosenImageUris.add(selectedUri)
        }

        adapter.notifyDataSetChanged()
        supportActionBar?.title = "Choose pics (${chosenImageUris.size}/$numImagesRequired)"
        btnSave.isEnabled  = shouldEnableSaveButton()
    }

    private fun saveDataToFirebase() {
        Log.i(TAG,"Save Data to firebase")
        for ((index,photoUri) in chosenImageUris.withIndex()){
            val imageByteArray = getImageByteArray(photoUri)
        }
    }

    private fun getImageByteArray(photoUri: Uri): ByteArray {
        val originalBitmap =  if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.P){
            val source = ImageDecoder.createSource(contentResolver,photoUri)
            ImageDecoder.decodeBitmap(source)
        }
        else{
            MediaStore.Images.Media.getBitmap(contentResolver,photoUri)
        }

        Log.i(TAG,"Original Width: ${originalBitmap.width} and height ${originalBitmap.height}")
        val scaledBitmap = BitmapScaler.scaleToFitHeight(originalBitmap,250)
        Log.i(TAG,"Scaled Width: ${scaledBitmap.width} and height ${scaledBitmap.height}")
        val byteOutputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG,60,byteOutputStream)
        return byteOutputStream.toByteArray()
    }

    private fun shouldEnableSaveButton(): Boolean{
        //Check if enable the button
        if (chosenImageUris.size != numImagesRequired){
            return false
        }
        else if (etGameName.text.isBlank() || etGameName.text.length< MIN_GAME_NAME_LENGTH)
            return false
        return true
    }

    private fun launchIntentForPhotos() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
        startActivityForResult(Intent.createChooser(intent,"Choose pics"), PICK_PHOTO_CODE)
    }
}