package com.example.findmycircle.view.logInScreen

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.findmycircle.ui.theme.AccentCyan
import com.example.findmycircle.ui.theme.AccentViolet
import com.example.findmycircle.ui.theme.SurfaceCard
import com.example.findmycircle.ui.theme.TextPrimary
import com.example.findmycircle.ui.theme.TextSecondary
import com.example.findmycircle.viewModel.LoginScreenViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.example.findmycircle.view.logInScreen.components.GoogleSignInButtonLight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import com.example.findmycircle.R
import com.example.findmycircle.ui.theme.DividerColor

@Composable
fun LoginScreen(
    googleSignInClient: GoogleSignInClient,
    onNavigateToHome: () -> Unit
) {
    val appFont = FontFamily(
        Font(R.font.quicksandmedium)
    )

    val viewModel: LoginScreenViewModel = hiltViewModel()
    val state = viewModel.state

    // 🎯 Activity Result Launcher
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == Activity.RESULT_OK) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken

                if (idToken != null) {
                    viewModel.handleFirebaseAuth(idToken)
                } else {
                    viewModel.onSignInFailure("No ID Token")
                }

            } catch (e: Exception) {
                viewModel.onSignInFailure(e.message ?: "Sign-in failed")
            }
        } else {
            viewModel.onSignInFailure("User cancelled")
        }
    }

    // 🚀 Navigate when logged in
    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) {
            onNavigateToHome()
        }
    }

    // 🎨 UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        AccentViolet.copy(alpha = 0.15f),
                        AccentCyan.copy(alpha = 0.15f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🔷 Logo Badge
            Text(
                text = "Find My Circle",
                color = Color.Black,
                fontSize = 35.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = appFont
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 🪪 Card
            Card(
                modifier = Modifier.fillMaxWidth()
                    .height(300.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp, vertical = 36.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Hello there",
                        color = TextPrimary,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Sign in to connect and stay\nclose.",
                        textAlign = TextAlign.Center,
                        color = TextSecondary,
                        fontSize = 18.sp,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    // 🔥 Google Button
                    GoogleSignInButtonLight(
                        isLoading = state.isLoading,
                        onClick = {
                            val canProceed = viewModel.onGoogleSignInClicked()

                            if (canProceed) {
                                launcher.launch(googleSignInClient.signInIntent)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            thickness = 3.dp,
                            color = DividerColor
                        )

                        Spacer(modifier = Modifier.width(3.dp))

                        Text(
                            text = "  SECURE LOGIN  ",
                            color = TextSecondary,
                            fontSize = 17.sp,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.width(3.dp))

                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            thickness = 3.dp,
                            color = DividerColor
                        )
                    }

                    // ❗ Error message
                    state.error?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}