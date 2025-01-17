package com.example.noaaweatherapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Base64
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class MyBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Constants.ACTION_SEND_CONTACTS) {
            Log.d("MyBroadcastReceiver", "Received broadcast with action: ${intent.action}")
            val encodedData = intent.getStringExtra("data")
            if (!encodedData.isNullOrEmpty()) {
                val contacts = String(Base64.decode(encodedData, Base64.DEFAULT))
                Log.d("MyBroadcastReceiver", "Contacts received: $contacts")

                // Send a local broadcast to notify the Fragment
                val localIntent = Intent(Constants.ACTION_LOCAL_UPDATE_CONTACTS)
                localIntent.putExtra("contacts", contacts)
                LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent)
            } else {
                Log.e("MyBroadcastReceiver", "No data received in broadcast")
            }
        }
    }
}
object Constants {
    const val ACTION_REQUEST_CONTACTS = "com.example.noaaweatherapp.ACTION_REQUEST"
    const val ACTION_SEND_CONTACTS = "com.example.bezpiecznynotatnik.ACTION_SEND"
    const val ACTION_LOCAL_UPDATE_CONTACTS = "com.example.ACTION_LOCAL_UPDATE_CONTACTS"
}
