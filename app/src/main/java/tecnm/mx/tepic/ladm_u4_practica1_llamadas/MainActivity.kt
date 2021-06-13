package tecnm.mx.tepic.ladm_u4_practica1_llamadas

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.CallLog
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    var baseRemota = FirebaseFirestore.getInstance()
    var lista = ArrayList<String>()
    var siPermisoLLamada = 1
    var siPermisoSMS = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.SEND_SMS),siPermisoSMS)
            }
        button.setOnClickListener {
            llamadasPerdidas()
        }
        button2.setOnClickListener {
            var intent = Intent(this,MainActivity2::class.java)
            startActivity(intent)
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == siPermisoLLamada){ llamadasPerdidas() }
    }

    private fun enviarMensajes() {
        var tipo = ""
        if (lista.isEmpty()){
            mensaje("Sin llamadas perdidas")
        }else {
            var numTel = ""
            lista.forEach {
                baseRemota.collection("autocontestadora").addSnapshotListener { querySnapshot, error ->
                    if (error != null) {
                        mensaje(error.message!!)
                        return@addSnapshotListener
                    }
                    for (document in querySnapshot!!) {
                        tipo = "${document.getString("tipo")}"
                        if (tipo.equals("deseados")) {
                            if (it.equals(document.getString("telefono"))) {
                                numTel= document.getString("telefono").toString()
                                android.telephony.SmsManager.getDefault().sendTextMessage(numTel,null, mensajeDeseado.toString(),null,null)
                                println("Mensaje deseado enviado a :${numTel}")
                            }
                        } else {
                            if (it.equals(document.getString("telefono"))) {
                                numTel = document.getString("telefono").toString()
                                android.telephony.SmsManager.getDefault().sendTextMessage(numTel,null, mensajeNoDeseado.toString(),null,null)
                                println("Se envio el mensaje no deseado al numero:${numTel}")
                            }
                        }
                    }
                }
            }
        }

    }

    private fun llamadasPerdidas() {
        var llamadas = ArrayList<String>()
        val seleccion = CallLog.Calls.TYPE + "=" + CallLog.Calls.MISSED_TYPE
        var cursor = contentResolver.query(
                Uri.parse("content://call_log/calls"),
                null, seleccion, null, null
        )
        lista.clear()
        var registroLlamadas = ""
            var nombre = cursor!!.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME))
            var telefono = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER))
            telefono = telefono.replace(" ".toRegex(), "")
            var tipo = cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE))

            registroLlamadas = "Nombre: ${nombre} \nTelefono: ${telefono} \nTipo: ${tipo} \n"
            llamadas.add(registroLlamadas)
            lista.add(telefono)
            println(lista)
        listallamadas.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, llamadas)
        cursor.close()
    }

    private fun mensaje(s: String) {
        AlertDialog.Builder(this).setTitle("ATENCION")
                .setMessage(s)
                .setPositiveButton("OK"){ d,i-> }
                .show()
    }

    private fun alerta(s: String) {
        Toast.makeText(this,s, Toast.LENGTH_LONG).show()
    }
}