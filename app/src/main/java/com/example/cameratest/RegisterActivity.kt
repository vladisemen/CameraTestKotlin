package com.example.cameratest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        tvLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        bottomRegister.setOnClickListener {
            if (editPassword.text != editCPassword.text) {
                Toast.makeText(this, "Password mismatch", Toast.LENGTH_LONG).show()
            } else {
                if (editUsername.text.trim().isEmpty() or editEmail.text.trim()
                        .isEmpty() or editPassword.text.trim()
                        .isEmpty() or editCPassword.text.trim().isEmpty()
                ) {
                    Toast.makeText(this, "Please, write all information", Toast.LENGTH_LONG).show()
                } else {
                    GlobalScope.launch(Dispatchers.Main) {
                        val nameStr = withContext(Dispatchers.Default) {
                            post()
                        }
                        if (nameStr == "Found") {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Пользовтаель с таким e-mail уже создан!",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(this@RegisterActivity, "Все ок", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
            }
        }
    }

    private fun post(): String {
        val firstName = editUsername.text
        val textEmail = editEmail.text
        val textPassword = editPassword.text
        val ans =
            Fuel.post(Server().Path) // проверить русские буквы, могут быть проблемы с кодировкой
                .jsonBody(
                    "{\"id\": \"100\", " +
                            "\"firstName\": \"$firstName\", " +
                            "\"lastName\": \"$firstName\", " +
                            "\"password\": \"$textPassword\", " +
                            "\"email\": \"$textEmail\"}"
                )
                .also { println(it) }
                .response()
        return ans.second.responseMessage
    }

}
