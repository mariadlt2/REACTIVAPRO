package com.delatorremaria.proyectoreactivapro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class Registro : AppCompatActivity() {

    lateinit var nombre: EditText
    lateinit var apellido: EditText
    lateinit var cargo: EditText
    lateinit var usuario: EditText
    lateinit var contraseña: EditText
    lateinit var registrar: Button
    lateinit var cancelar: Button
    val bd = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        //Inicializacion de las variables
        nombre = findViewById<EditText>(R.id.txtRegistroNombre)
        apellido = findViewById<EditText>(R.id.txtRegistroApellido)
        cargo = findViewById<EditText>(R.id.txtRegistroCargo)
        usuario = findViewById<EditText>(R.id.txtRegistroUsuario)
        contraseña = findViewById<EditText>(R.id.txtRegistroContraseña)
        registrar = findViewById<Button>(R.id.btnRegistrar)
        cancelar = findViewById<Button>(R.id.btnRegistroCancelar)

        //Ejecucion boton Registrar
        registrar.setOnClickListener {
            //view -> irAMenu(usuario,contraseña)
            if (!ValidarDatos())
                return@setOnClickListener
            //Registra Usuario en Firebase
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(usuario.text.toString()
                ,contraseña.text.toString()).addOnCompleteListener {
                if (it.isSuccessful){
                    RegistrarUsuario()
                }else{
                    alerta()
                }
            }

        }

        //Ejecucion boton Cancelar
        cancelar.setOnClickListener {
                view -> Regresar()
        }
    }

    private fun RegistrarUsuario(){
        bd.collection("User").document(usuario.text.toString()).set(
            hashMapOf("Nombre" to nombre.text.toString(),
            "Apellido" to apellido.text.toString(),
            "Cargo" to cargo.text.toString(),
            "Email" to usuario.text.toString(),
            "Contraseña" to contraseña.text.toString())
        )
        Regresar()
    }

    private fun alerta(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se a producido un error de autenticacion")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun Regresar(){
        val menuIntent: Intent = Intent(this, Login::class.java)
        startActivity(menuIntent)
    }

    fun ValidarDatos(): Boolean {

        fun CharSequence?.isValidEmail() =
            !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
        if (nombre.text.isNullOrEmpty()) {
            nombre.setError("Nombre Vacio")
            nombre.requestFocus()
            Toast.makeText(this, "Nombre Vacio", Toast.LENGTH_SHORT).show()
            return false
        }
        if (apellido.text.isNullOrEmpty()) {
            apellido.setError("Apellido vacio")
            apellido.requestFocus()
            Toast.makeText(this, "Apellido Vacio", Toast.LENGTH_SHORT).show()
            return false
        }
        if (cargo.text.isNullOrEmpty()) {
            cargo.setError("Cargo vacio")
            cargo.requestFocus()
            Toast.makeText(this, "Cargo Vacio", Toast.LENGTH_SHORT).show()
            return false
        }
        if (usuario.text.isNullOrEmpty()) {
            usuario.setError("Email vacio")
            usuario.requestFocus()
            Toast.makeText(this, "Email Vacio", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!usuario.text.isValidEmail()) {
            usuario.setError("Email no valido")//getString(R.string.email_NoValido))
            usuario.requestFocus()
            Toast.makeText(this, "Email invalido", Toast.LENGTH_SHORT).show()
            return false
        }
        if (contraseña.text.isNullOrEmpty()) {
            contraseña.setError("Contraseña vacia")//getString(R.string.editTextPassword_hint))
            contraseña.requestFocus()
            Toast.makeText(this, "Password Vacio", Toast.LENGTH_SHORT).show()
            return false
        }
        if (contraseña.text.length < 8) {
            contraseña.setError("Contraseña minima de 8 caracteres")//getString(R.string.password_longitudNoValida))
            contraseña.requestFocus()
            Toast.makeText(this, "Password invalido", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}