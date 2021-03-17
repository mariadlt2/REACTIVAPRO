package com.delatorremaria.proyectoreactivapro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Menu : AppCompatActivity() {

    lateinit var nuevaReserva : Button
    lateinit var historial : Button
    lateinit var nuevaIncidencia : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        //Inicializacion de variables
        nuevaReserva = findViewById(R.id.btnMenuNuevaReserva)
        historial = findViewById(R.id.btnMenuMisReservas)
        nuevaIncidencia = findViewById(R.id.btnMenuNuevaIncidencia)

        nuevaReserva.setOnClickListener {
            view -> val menuIntent: Intent = Intent(this, Principal::class.java)
            startActivity(menuIntent)
        }

        historial.setOnClickListener {
                view -> val menuIntent: Intent = Intent(this, MisReservas::class.java)
            startActivity(menuIntent)
        }

        nuevaIncidencia.setOnClickListener {
                view -> val menuIntent: Intent = Intent(this, Incidencias::class.java)
            startActivity(menuIntent)
        }
    }
}