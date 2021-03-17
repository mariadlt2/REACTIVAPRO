package com.delatorremaria.proyectoreactivapro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth


class Login : AppCompatActivity() {

    lateinit var usuario : EditText
    lateinit var contraseña : EditText
        lateinit var login : Button
        lateinit var registrar : Button
    lateinit var checkBoxRecordarme : CheckBox
    var usuarioRecordado : String? = null
    var contraseñaRecordada : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.ThemeProyectoReactivaPro)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Inicializacion de variables
        usuario = findViewById<EditText>(R.id.txtLoginUsuario)
        contraseña = findViewById<EditText>(R.id.txtLoginContraseña)
        login = findViewById<Button>(R.id.btnLogin)
        checkBoxRecordarme = findViewById<CheckBox>(R.id.checkBoxLogin)
        registrar = findViewById<Button>(R.id.btnRegistro)

        //Google Analytics
        val analitics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("Mensaje","Integracion de Firebase completa")
        analitics.logEvent("InitScreen",bundle)

        //Inicializo llave
        val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val sharedPreferences = EncryptedSharedPreferences.create(
            "secret_shared_prefs",//filename
            masterKeyAlias,
            this,//context
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        //Lectura de valores de archivo de preferencias en caso que exita
        usuarioRecordado = sharedPreferences.getString(LOGIN_KEY,"")
        contraseñaRecordada = sharedPreferences.getString(PASSWORD_KEY,"")
        usuario.setText ( usuarioRecordado )
        contraseña.setText ( contraseñaRecordada )
        if(usuarioRecordado.toString() != "" && contraseñaRecordada.toString()!= "" ) {
            checkBoxRecordarme.isChecked = true
        }

        //Ejecucion boton Login
        login.setOnClickListener {
            //view -> irAMenu(usuario,contraseña)
            if (!ValidarDatos())
                return@setOnClickListener
            //Autenticacion
            FirebaseAuth.getInstance().signInWithEmailAndPassword(usuario.text.toString()
                ,contraseña.text.toString()).addOnCompleteListener {
                if (it.isSuccessful){
                    if(checkBoxRecordarme.isChecked){
                        sharedPreferences
                            .edit()
                            .putString(LOGIN_KEY,usuario.text.toString())
                            .putString(PASSWORD_KEY,contraseña.text.toString())
                            .apply()
                        irAMenu()
                    }
                    else{
                        val editor = sharedPreferences.edit()
                        editor.putString(LOGIN_KEY,"")
                        editor.putString(PASSWORD_KEY,"")
                        editor.commit()
                        irAMenu()
                    }
                }else{
                    alerta()
                }
            }
        }

        //Ejecucion boton Registrar
        registrar.setOnClickListener {
            view -> irAlRegistro()
        }
    }
    private fun irAMenu(){
            val menuIntent: Intent = Intent(this, Menu::class.java)
            startActivity(menuIntent)
    }

    private fun irAlRegistro(){
        val menuIntent: Intent = Intent(this, Registro::class.java)
        startActivity(menuIntent)
    }

    private fun alerta(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se a producido un error de autenticacion")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun ValidarDatos(): Boolean {

        fun CharSequence?.isValidEmail() =
            !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
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