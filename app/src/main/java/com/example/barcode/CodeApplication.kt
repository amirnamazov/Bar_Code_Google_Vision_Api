package com.example.barcode

import android.app.Application
import android.content.Context
import com.example.barcode.data.api.RetrofitInstance
import com.example.barcode.data.api.SimpleApi
import com.example.barcode.data.model.Post
import com.example.barcode.ui.CodeRecyclerView
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.barcode.BarcodeDetector
import org.kodein.di.*
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class CodeApplication() : Application(), KodeinAware {

    override val kodein: Kodein = Kodein.lazy {
        bind<SimpleApi>() with singleton { RetrofitInstance.api }
        bind<ArrayList<Post>>() with singleton { ArrayList() }
        bind<CodeRecyclerView>() with singleton { CodeRecyclerView(instance()) }
        bind<BarcodeDetector>() with singleton { BarcodeDetector.Builder(this@CodeApplication).build() }
        bind<CameraSource>() with singleton { CameraSource.Builder(this@CodeApplication,
            instance()).setAutoFocusEnabled(true).build() }
    }
}