package com.example.root.buoi1kotlin

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    private lateinit var mAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mGoogleApiClient: GoogleApiClient
    private lateinit var dialog: ProgressDialog
    private val RC_SIGN_IN = 999
    override fun onClick(p0: View?) {
        when (p0) {
            btnLogin -> {
                val email: String = edtEmail.text.toString().trim()
                val password: String = edtPassword.text.toString().trim()
                if (!Check()) {
                    return
                }
                Login(email, password)
            }
            btnRegister -> {
                val email: String = edtEmail.text.toString().trim()
                val password: String = edtPassword.text.toString().trim()
                if (!Check()) {
                    return
                }
                Register(email, password)
            }
            btnGoogle -> {
                signInGoogle()
            }
        }
    }

    private fun signInGoogle() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                val account = result.signInAccount
                firebaseAuthWithGoogle(account!!)
            } else {
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        dialog = ProgressDialog.show(this, "Loading", "Please waite", true)
        val credential = GoogleAuthProvider.getCredential(account.getIdToken(), null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        dialog.dismiss()
                        shortToast("Login Success")
                        route()
                    } else {
                        dialog.dismiss()
                        shortToast("Login Fail")
                    }
                }
    }

    private fun Check(): Boolean {
        var check: Boolean = true
        val email: String = edtEmail.text.toString().trim()
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Please enter email!")
            check = false
        }
        val password: String = edtPassword.text.toString().trim()
        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Please enter password")
            check = false
        }
        return check
    }

    private fun Register(email: String, password: String) {
        dialog = ProgressDialog.show(this, "Loading", "Please wait", true)
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        dialog.dismiss()
                        shortToast("Register Success")
                    } else {
                        dialog.dismiss()
                        shortToast("Register Fail")
                    }
                }
    }

    private fun Login(email: String, password: String) {
        dialog = ProgressDialog.show(this, "Loading", "Please wait", true)
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isComplete) {
                        dialog.dismiss()
                        shortToast("Login Success")
                        if (chkSave.isChecked) {
                            val editor: SharedPreferences.Editor = sharedPreferences.edit()
                            editor.putString("email", email)
                            editor.putString("password", password)
                            editor.putBoolean("check", true)
                            editor.commit()
                        } else {
                            val editor: SharedPreferences.Editor = sharedPreferences.edit()
                            editor.remove("email")
                            editor.remove("password")
                            editor.remove("check")
                            editor.commit()
                        }

                        route()
                    } else {
                        dialog.dismiss()
                        shortToast("Login Fail")
                    }
                }
    }

    private fun route() {
        startActivity(Intent(MainActivity@ this, Main2Activity::class.java))
    }

    private fun shortToast(message: String, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, length).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        initToolbar()
        sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE)
        edtEmail.setText(sharedPreferences.getString("email", ""))
        edtPassword.setText(sharedPreferences.getString("password", ""))
        chkSave.isChecked = sharedPreferences.getBoolean("check", false)
        initGoogle()
        mAuth = FirebaseAuth.getInstance()
    }

    private fun initGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        // [END config_signin]

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
    }

    private fun init() {
        btnLogin.setOnClickListener(this)
        btnRegister.setOnClickListener(this)
        btnGoogle.setOnClickListener(this)
    }

    private fun initToolbar() {
        val toolbar = findViewById(R.id.toolbarMain) as Toolbar
        setSupportActionBar(toolbar)
        //? Co the null
        supportActionBar?.title = "LOGIN/REGISTER"
    }

    //Bay gio tich hop FireBase vao.

}
