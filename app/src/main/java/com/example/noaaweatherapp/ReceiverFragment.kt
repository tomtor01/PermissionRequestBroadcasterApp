package com.example.noaaweatherapp

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager


class ReceiverFragment : Fragment(R.layout.fragment_receiver) {

    private lateinit var sendButton: Button
    private lateinit var textView: TextView

    private val contactsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Constants.ACTION_LOCAL_UPDATE_CONTACTS) {
                val contacts = intent.getStringExtra("contacts")
                textView.text = contacts ?: "No contacts received"
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textView = view.findViewById(R.id.text_receiver)
        sendButton = view.findViewById(R.id.send_button)

        sendButton.setOnClickListener {
            sendContactRequest()
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(Constants.ACTION_LOCAL_UPDATE_CONTACTS)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(contactsReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(contactsReceiver)
    }

    private fun sendContactRequest() {
        val intent = Intent()
        intent.action = Constants.ACTION_REQUEST_CONTACTS
        intent.component = ComponentName(
            "com.example.bezpiecznynotatnik",
            "com.example.bezpiecznynotatnik.RequestBroadcastReceiver"
        )
        requireContext().sendBroadcast(intent)
        Log.d("ReceiverFragment", "Broadcast sent with action: ${intent.action}")
    }
}