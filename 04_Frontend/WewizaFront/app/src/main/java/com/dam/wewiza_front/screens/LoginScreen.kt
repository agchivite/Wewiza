package com.dam.wewiza_front.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dam.wewiza_front.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.dam.wewiza_front.constants.Constants.FIREBASE_CLIENT_ID
import com.dam.wewiza_front.viewModels.LoginViewModel
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.lang.Exception

private val GOOGLE_SIGN_IN = 100


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel 
){
    Scaffold() {
        BodyContent(viewModel)
    }

}

@Composable
fun BodyContent(viewModel: LoginViewModel) {
    var emailValue by remember { mutableStateOf(TextFieldValue()) }
    var passwordValue by remember { mutableStateOf(TextFieldValue()) }
    val context = LocalContext.current


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ){
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            viewModel.signInWithGoogleCredential(credential)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }


    Box(modifier = Modifier.run {
        padding(top = 20.dp)
        background(Color.Green)
    }){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Image(
                painter = painterResource(id = R.drawable.provisional_logo),
                contentDescription = "Logo", modifier = Modifier.size(200.dp)
            )
            Text("¡Inicia Sesión en WeWiza!", style = TextStyle(fontSize = 30.sp))

            TextInputs(emailValue, passwordValue) { email, password ->
                emailValue = email
                passwordValue = password
            }
            Row() {
                Button(
                    onClick = {
                        val email = emailValue.text
                        val password = passwordValue.text
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Login Correcto", Toast.LENGTH_SHORT).show()
                                } else {
                                    // Error al autenticar al usuario
                                    // Maneja el error según tus necesidades
                                }
                            }
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Iniciar Sesión")
                }


                Button(onClick = { }, modifier = Modifier.padding(16.dp)) {
                    Row(

                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Registrarse")
                    }

                }
            }

            Button(onClick = {

                loginWithGoogle(context, launcher)


            }, modifier = Modifier.run {
                height(40.dp)
                width(200.dp)
            }) {
                Row(

                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(painter = painterResource(id = R.drawable.google), contentDescription = "google", modifier = Modifier.size(30.dp))
                    Spacer(modifier = Modifier.size(20.dp))
                    Text(text = "Google")
                }

            }


        }
    }
}

fun loginWithGoogle(
    context: Context,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(FIREBASE_CLIENT_ID)
        .requestEmail()
        .build()

    val googleClient = GoogleSignIn.getClient(context, gso)

    launcher.launch(googleClient.signInIntent)


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextInputs(
    emailValue: TextFieldValue,
    passwordValue: TextFieldValue,
    onValuesChange: (email: TextFieldValue, password: TextFieldValue) -> Unit
) {
    Column {
        TextField(
            value = emailValue,
            onValueChange = { onValuesChange(it, passwordValue) },
            label = { Text("Correo") },
            modifier = Modifier.padding(16.dp)
        )
        TextField(
            value = passwordValue,
            onValueChange = { onValuesChange(emailValue, it) },
            label = { Text("Contraseña") },
            modifier = Modifier.padding(16.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview(){
    val viewModel = LoginViewModel()
    LoginScreen(viewModel)
}