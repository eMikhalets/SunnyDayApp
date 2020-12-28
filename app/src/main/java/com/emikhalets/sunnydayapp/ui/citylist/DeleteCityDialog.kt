package com.emikhalets.sunnydayapp.ui.citylist

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.emikhalets.sunnydayapp.R
import com.emikhalets.sunnydayapp.data.database.City
import javax.inject.Inject

class DeleteCityDialog @Inject constructor(
    val city: City,
    private val listener: DeleteCityListener
) : DialogFragment() {

    interface DeleteCityListener {
        fun onDeleteCity(city: City)
    }

    //TODO: make dialog text is black
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(getString(R.string.cities_dialog_message, city.name, city.country))
            .setPositiveButton(getString(R.string.cities_dialog_positive)) { _, _ ->
                listener.onDeleteCity(city)
            }
            .setNegativeButton(getString(R.string.cities_dialog_negative)) { dialog, _ ->
                dialog.cancel()
            }
        return builder.create()
    }
}