package com.example.cameratest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        
        tvLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        bottomRegister.setOnClickListener{
            if(editUsername.text.trim().isEmpty() or editEmail.text.trim().isEmpty() or editPassword.text.trim().isEmpty() or editCPassword.text.trim().isEmpty()){
                Toast.makeText(this, "Please, write information", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this, "Все ок", Toast.LENGTH_LONG).show()
            }
        }
    }

}