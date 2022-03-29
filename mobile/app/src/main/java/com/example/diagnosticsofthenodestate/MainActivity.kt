package com.example.diagnosticsofthenodestate

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), OnItemClickListener, OnEditTextChanged {
    private lateinit var adapter: RecyclerAdapter
    private lateinit var cameraSource: CameraSource
    private lateinit var detector: BarcodeDetector
    private lateinit var parentLayout: View
    private lateinit var auth: FirebaseAuth
    private lateinit var authUser: FirebaseUser
    private var qrCodeRead: String = ""
    private var requestCodeCameraPermission = 1001
    private val database = Firebase.database
    private val myRef = database.reference
    private lateinit var vibrator: Vibrator

    private var email: String = BuildConfig.email
    private var passw: String = BuildConfig.passw

    private var guild: String = ""
    private var unit: String = ""
    private var node: String = ""
    private var nodeTimeStart: String = ""

    val users = mutableListOf<User>()
    private lateinit var outRYG : OutRYG


    @Suppress("DEPRECATED_IDENTITY_EQUALS")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        parentLayout = findViewById(android.R.id.content)


        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, passw)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Authentication successful",
                        Toast.LENGTH_SHORT).show()
                    authUser = auth.currentUser!!
                } else {
                    Toast.makeText(this, "Authentication failed",
                        Toast.LENGTH_LONG).show()
                }
            }

        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            askForCameraPermission()
        } else {
            setupControls()
        }

        vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        val usersList = findViewById<RecyclerView>(R.id.recyclerView)
        usersList.layoutManager = LinearLayoutManager(this)
        adapter = RecyclerAdapter(users,this, this)
        usersList.adapter = adapter


        btnUpload.setOnClickListener {
            val currentDateTime : String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())
            val unixTime: Long = System.currentTimeMillis()/1000L

            outRYG = OutRYG(
                guild,
                unit,
//                nodeTimeStart,
 //               unixTime,
                currentDateTime,
                node
            )
            myRef.child("labelRYG/$unixTime").setValue(outRYG)
            myRef.child("pointRYG/$unixTime").setValue(users)


            qrCodeRead = ""
            users.clear()
            adapter.notifyDataSetChanged()

            detector = BarcodeDetector.Builder(this@MainActivity).build()
            cameraSource = CameraSource.Builder(this@MainActivity, detector)
                .setAutoFocusEnabled(true)
                .build()
            cameraSource.start(cameraSurfaseView.holder)
            detector.setProcessor(processor)
            btnUpload.visibility = View.GONE
            lblCeh.text = resources.getString(R.string.lblCeh)
            lblAgregat.text = resources.getString(R.string.lblAgregat)
            lblMehanizm.text = resources.getString(R.string.lblMehanizm)
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(25)
            }
        }
    }


    private fun setupControls(){

        detector = BarcodeDetector.Builder(this@MainActivity).build()
        cameraSource = CameraSource.Builder(this@MainActivity, detector)
            .setAutoFocusEnabled(true)
            .build()
        cameraSurfaseView.holder.addCallback(surfaceCallBack)
        detector.setProcessor(processor)
    }

    private fun askForCameraPermission(){
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(Manifest.permission.CAMERA),
            requestCodeCameraPermission
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodeCameraPermission && grantResults.isNotEmpty()){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                setupControls()
            } else {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val surfaceCallBack = object : SurfaceHolder.Callback{
        override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
            try {
                if (ActivityCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                cameraSource.start(surfaceHolder)
            } catch (exception: Exception){
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }

        override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
        }

        override fun surfaceDestroyed(p0: SurfaceHolder) {
            cameraSource.stop()
        }
    }

    private val processor = object : Detector.Processor<Barcode>{
        override fun release() {
        }

        override fun receiveDetections(detections: Detector.Detections<Barcode>?) {
            if (detections != null && detections.detectedItems.size() > 0){
                val qrCodes: SparseArray<Barcode> = detections.detectedItems
                val code = qrCodes.valueAt(0)

                if (qrCodeRead != code.displayValue) {
                    qrCodeRead = code.displayValue
//                    Toast.makeText(this@MainActivity, "QR-code: $qrCodeRead\n" +
//                            "Светофор: $state", Toast.LENGTH_LONG).show()
//                    myRef.child("test").push().setValue(qrCodeRead)
//                    Log.d(TAG, "QRcodes scan: $qrCodeRead")
//                    Snackbar.make(parentLayout, "QR-code: $qrCodeRead", Snackbar.LENGTH_LONG)
//                        .setAction("CLOSE") { }.setAction("Action", null)
//                        .setActionTextColor(Color.YELLOW)
//                        .show()

                    myRef.child("QRCodes").addListenerForSingleValueEvent(object :
                        ValueEventListener {

                        override fun onDataChange(snapQRcodes: DataSnapshot) {
                            var search = true
                            snapQRcodes.children.forEach { snapCeh ->
                                snapCeh.children.forEach { snapAgregat ->
                                    snapAgregat.children.forEach { snapMetka ->


                                        if (snapMetka.key == qrCodeRead) {
                                            Log.d(TAG, "QRCode: $qrCodeRead")
                                            cameraSource.stop()
                                            cameraSource.release()
                                            detector.release()
                                            btnUpload.visibility = View.VISIBLE

                                            search = false
                                            guild = snapCeh.key.toString()
                                            unit = snapAgregat.key.toString()
                                            node = snapMetka.child("label").value.toString()
                                            if (node == "null") node = ""
                                            lblCeh.text = guild
                                            lblAgregat.text = unit
                                            lblMehanizm.text = node
                                            nodeTimeStart = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(Date())

                                            users.clear()
                                            snapMetka.child("node").children.forEach { snapPoint ->
                                                users.add(User(
                                                    snapPoint.child("0").value.toString(),
                                                    snapPoint.child("1").value.toString(),
                                                    snapPoint.child("2").value.toString(),
                                                    snapPoint.child("3").value.toString(),
                                                    0,
                                                    "null",
                                                    "null"
                                                ))
                                            }
                                            adapter.notifyDataSetChanged()

                                        }
                                    }
                                }
                            }
                            if (search) Toast.makeText(this@MainActivity,
                                "Этого QR-кода нет в базе:\n$qrCodeRead",
                                Toast.LENGTH_LONG
                            ).show()

                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@MainActivity, "$error", Toast.LENGTH_LONG).show()
                        }
                    })
                }
            }
        }

    }

    override fun onItemClicked(user: User) {
        Log.i(TAG, user.toString())

        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(25)
        }
        user.timeSet = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(Date())
    }

    companion object {
        private const val TAG = "OTDO"
    }

    override fun onTextChanged(position: Int, charSeq: String?) {
//        Log.i(TAG, "onTextChanged: $position $charSeq")
    }

}