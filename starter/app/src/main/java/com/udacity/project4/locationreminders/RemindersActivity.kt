package com.udacity.project4.locationreminders
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.udacity.project4.R

import kotlinx.android.synthetic.main.activity_reminders.*

/**
 * The RemindersActivity that holds the reminders fragments
 */
class RemindersActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminders)
//        val navController=this.findNavController(R.id.nav_host_fragment)
//        NavigationUI.setupActionBarWithNavController(this,navController)
        Log.i("xdx","reminders activity")

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                (nav_host_fragment as NavHostFragment).navController.popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}