package io.github.bungabear.statusbargesture

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val REQ_OVERLAY_PERMISSION = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(!Settings.canDrawOverlays(this)){
            val dialog = AlertDialog.Builder(this).run {
                setTitle("오버레이 권한이 필요합니다")
                setPositiveButton("설정하기"){ _, _ ->
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                    startActivityForResult(intent, REQ_OVERLAY_PERMISSION)
                }
                setNegativeButton("그만두기"){ _, _ ->
                    cancel()
                }
                setCancelable(false)
                create()
            }

            dialog.show()
        }
        else {
            next()
        }

        swOverlayService.setOnCheckedChangeListener { buttonView, isChecked ->
            Toast.makeText(this, "checked $isChecked", Toast.LENGTH_LONG).show()
            val intent = Intent(this, StatusBarOverlayService::class.java)
            if (isChecked){
                if (Build.VERSION.SDK_INT >= 26) {
                    startForegroundService(intent)
                } else {
                    startService(intent)
                }
            }
            else {
                stopService(intent)
            }
        }

    }

    private fun next(){
        if (Settings.System.canWrite(this)) {
            Toast.makeText(this, "onCreate: Already Granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "onCreate: Not Granted. Permission Requested", Toast.LENGTH_SHORT).show()
            val intent = Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:" + this.packageName)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
//        Toast.makeText(this, "hi", Toast.LENGTH_LONG).show()
    }

    private fun cancel(){
        Toast.makeText(this, "앱 종료", Toast.LENGTH_LONG).show()
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        log("req : $requestCode, res : $resultCode, $data")
        when (requestCode) {
            REQ_OVERLAY_PERMISSION -> {
                if(Settings.canDrawOverlays(this)){
                    next()
                } else {
                    cancel()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
