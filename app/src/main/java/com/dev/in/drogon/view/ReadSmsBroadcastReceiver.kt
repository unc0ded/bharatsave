package com.dev.`in`.drogon.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.widget.Toast

class ReadSmsBroadcastReceiver : BroadcastReceiver() {

    private val TAG = "ReadSmsBroadcastReceiver"
    private val round = 10

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action.equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            Telephony.Sms.Intents.getMessagesFromIntent(intent)
                .forEach {
                    if (it.messageBody.lowercase().contains("debited for inr")) {
                        val amt = try {
                            it.messageBody.lowercase().split(" inr ")[1].split(" ")[0].trim()
                        } catch (e: IndexOutOfBoundsException) {
                            "0"
                        }

                        val amtDouble = try {
                            amt.toDouble()
                        } catch (e: NumberFormatException) {
                            0.0
                        }

                        val roundAmt = round - (amtDouble % round)
                        Toast.makeText(context, "Rounded up ₹ $roundAmt!", Toast.LENGTH_LONG).show()
                    }
                }
        }

    }
}