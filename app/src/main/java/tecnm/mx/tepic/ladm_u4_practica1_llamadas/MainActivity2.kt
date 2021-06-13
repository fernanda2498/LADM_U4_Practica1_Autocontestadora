package tecnm.mx.tepic.ladm_u4_practica1_llamadas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main2.*

class MainActivity2 : AppCompatActivity() {
    var baseRemota = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)


        agregarContacto.setOnClickListener {
            agregarContacto()
        }


        cancelar.setOnClickListener {
            finish()
        }
    }

    private fun agregarContacto() {
        if(deseado.isChecked){
            insertar(nombre.text.toString(),telefono.text.toString(),deseado.text.toString())
        }
        if (noDeseado.isChecked){
            insertar(nombre.text.toString(),telefono.text.toString(),noDeseado.text.toString())
        }
        nombre.setText("")
        telefono.setText("")
    }

    private fun insertar(nombre:String,telefono:String,tipo:String){
        var datosInsertar = hashMapOf(
            "nombre" to nombre,
            "telefono" to telefono,
            "tipo" to tipo
        )
            baseRemota.collection("autocontestadora")
                .add(datosInsertar)
                .addOnSuccessListener {
                    alerta("SE AGREGO EL CONTACTO")
                }
                .addOnFailureListener {
                    mensaje("ERROR: ${it.message!!}")
                }
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