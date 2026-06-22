package com.example.pico_botella.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pico_botella.databinding.ActivityMainBinding

/**
 * MainActivity: Contenedor único (Single Activity) para toda la aplicación.
 * Implementa ViewBinding para un acceso seguro a las vistas.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}