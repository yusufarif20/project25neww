package com.example.project25

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Register : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var buttonRegister: ImageView
    private lateinit var emailText: EditText
    private lateinit var namaText: EditText
    private lateinit var kelasChoose: Spinner
    private lateinit var loadingProgressBar: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        //initialize the elemet
        auth = FirebaseAuth.getInstance()
        buttonRegister = findViewById(R.id.btnlogin)
        emailText = findViewById(R.id.et_email)
        namaText = findViewById(R.id.et_namaakun)
        kelasChoose = findViewById(R.id.et_kelas)
        loadingProgressBar = findViewById(R.id.register_loading_progress_bar)

        val kelasTypes = arrayOf("Kelas 1", "Kelas 2", "Kelas 3", "Kelas 4", "Kelas 5")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, kelasTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        kelasChoose.adapter = adapter

        buttonRegister.setOnClickListener {
            RegisterUser()
        }

    }

    private fun RegisterUser() {
        val email = emailText.text.toString()
        val nama = namaText.text.toString()
        val kelas = kelasChoose.selectedItem.toString()

        if (email.isEmpty() || nama.isEmpty()) {
            Toast.makeText(this, "Email atau Nama Masih Kosong", Toast.LENGTH_SHORT).show()
        } else if (nama.length < 6) {
            Toast.makeText(this, "Nama harus minimal 6 huruf", Toast.LENGTH_SHORT).show()
            return
        }
        registerToDatabase(email, nama, kelas)
    }

    private fun registerToDatabase(email: String, nama: String, kelas: String) {
        loadingProgressBar.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(email, nama)
            .addOnCompleteListener(this){
                if (it.isSuccessful){
                    loadingProgressBar.visibility = View.GONE
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                    saveNewUserDatabase(uid, email, nama, kelas)
                    Toast.makeText(this, "Register Berhasil", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                } else {
                    loadingProgressBar.visibility = View.GONE
                    Toast.makeText(this, "Gagal registrasi", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveNewUserDatabase(uid: String, email: String, nama: String, kelas: String) {
        val database = FirebaseDatabase.getInstance().getReference("users")
        val user = User(uid,email, nama, kelas)
        database.child(uid).setValue(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Toast.makeText(this, "Berhasil Registrasi", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "gagal registrasi", Toast.LENGTH_SHORT).show()
                }
            }
    }
}