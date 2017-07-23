package com.example.root.buoi1kotlin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main2.*

class Main2Activity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        initEvent()
        mAuth = FirebaseAuth.getInstance()
    }

    private fun initEvent() {
        btnLogOut.setOnClickListener {
            mAuth.signOut()
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        val mUser = mAuth.currentUser as FirebaseUser
        updateUI(mUser)
    }

    private fun updateUI(mUser: FirebaseUser?) {
            val email = mUser?.email
            textViewHello.text = email
    }

    private fun shortToast(message: String, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this,message,length).show()
    }
}
