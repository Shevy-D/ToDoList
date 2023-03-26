package com.shevy.todolist.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.shevy.todolist.R


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)*/
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.aboutMeMenuItem -> {
                /*val i = Intent(this, AboutActivity::class.java)
                startActivity(i)*/
                true
            }
            R.id.preferences -> {
                /*val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)*/
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}