package com.example.barcode.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import android.content.Intent
import android.content.pm.PackageManager
import android.opengl.Visibility
import android.util.SparseArray
import android.view.ContextMenu
import android.view.SurfaceHolder
import android.view.View
import android.widget.inline.InlineContentView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.util.isNotEmpty
import androidx.recyclerview.widget.RecyclerView
import com.example.barcode.R
import com.example.barcode.data.api.SimpleApi
import com.example.barcode.data.model.Post
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.material.snackbar.Snackbar
import java.security.Permission
import java.util.jar.Manifest

class MainActivity : AppCompatActivity(R.layout.activity_main), KodeinAware {

    override val kodein by closestKodein()
    private val api: SimpleApi by instance()
    private val arraylist: ArrayList<Post> by instance()
    private val codeRecyclerView: CodeRecyclerView by instance()
    private val detector: BarcodeDetector by instance()
    private val cameraSource: CameraSource by instance()
    private val requestCode = 100

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = codeRecyclerView

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED){
            askForCameraPermission()
        } else {
            setupControls()
        }

        btn_scan.setOnClickListener {
            codeRecyclerView.notifyDataSetChanged()
        }
    }

    private fun setupControls() {
        cameraSurface.holder.addCallback(surfaceCallback)
        detector.setProcessor(processor)
    }

    private fun postCode(post: Post){
        api.postCode(post).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (response.isSuccessful){
                    Log.i("MyResponse", "$response")
                    Snackbar.make(ll_main, "Bar Code sent successfully!", Snackbar.LENGTH_LONG).show()
                }
                else Snackbar.make(ll_main, "Not sent!", Snackbar.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                Snackbar.make(ll_main, t.message.toString(), Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun askForCameraPermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.CAMERA),
            requestCode
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (this.requestCode == requestCode && grantResults.isNotEmpty()){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                setupControls()
            }
            else Snackbar.make(ll_main, "Permission denied!", Snackbar.LENGTH_LONG).show()
        }
    }

    private val surfaceCallback = object : SurfaceHolder.Callback{
        override fun surfaceCreated(holder: SurfaceHolder) {
            try {
                cameraSource.start(holder)
            } catch (e:Exception){
                Snackbar.make(ll_main, "Something went wrong!", Snackbar.LENGTH_LONG).show()
            }
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            cameraSource.stop()
        }

    }

    private val processor = object : Detector.Processor<Barcode>{
        override fun release() {
        }

        override fun receiveDetections(detects: Detector.Detections<Barcode>) {
            if (detects.detectedItems.isNotEmpty()){
                val qrCode = detects.detectedItems
                val code = qrCode.valueAt(0).displayValue
                val post = Post("QR code", code)
                if (!arraylist.contains(post)) {
                    arraylist.add(post)
                    postCode(post)
                    Snackbar.make(ll_main, "Scanned!", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }
}