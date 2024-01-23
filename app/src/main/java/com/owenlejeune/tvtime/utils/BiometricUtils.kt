package com.owenlejeune.tvtime.utils

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

object BiometricUtils {

    fun showBiometricPrompt(activity: FragmentActivity, onError: () -> Unit = {}, onFailed: () -> Unit = {}, onSuccess: () -> Unit = {}) {
        val executor = ContextCompat.getMainExecutor(activity)
        val biometricPrompt = BiometricPrompt(activity, executor, object: BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onError()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onFailed()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }
        })
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Title")
            .setSubtitle("Subtitle")
            .setNegativeButtonText("Cancel")
            .build()
        biometricPrompt.authenticate(promptInfo)
    }

}

@Composable
fun canShowBiometricsPrompt(): Boolean {
    val context = LocalContext.current
    val biometricManager = BiometricManager.from(context)
    return biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS
}