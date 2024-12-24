package com.example.clock

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.clock.adapter.ClockStateAdapter
import com.example.clock.fragments.HelpFragment
import com.example.clock.fragments.SettingFragment
import com.example.clock.viewmodel.MainActivityViewmodel


class MainActivity : AppCompatActivity() {

    private lateinit var fragmentStateAdapter : ClockStateAdapter
    private lateinit var viewpager2:ViewPager2
    private lateinit var toolbar: Toolbar
    private lateinit var viewmodel: MainActivityViewmodel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        viewmodel = ViewModelProvider(this)[MainActivityViewmodel::class.java]

       toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        val drawable = ContextCompat.getDrawable(this,R.drawable.three_dot_menu_icon)
        toolbar.overflowIcon = drawable

        viewpager2 = findViewById(R.id.viewpager2)


        fragmentStateAdapter = ClockStateAdapter(this)
        viewpager2.adapter = fragmentStateAdapter

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu,menu)


        return  true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        when(item.itemId){
            R.id.help->{
               showFragment(HelpFragment())
            }
            R.id.setting->{
                showFragment(SettingFragment())
//                Toast.makeText(this, "Setting option selected", Toast.LENGTH_SHORT).show()
            }
        }
       return true
    }

    override fun onResume() {
        super.onResume()
        viewmodel.showViewpager.observe(this, Observer {
            if(it){
                viewpager2.visibility = View.VISIBLE
                toolbar.visibility = View.VISIBLE
            }else{
                viewpager2.visibility = View.GONE
                toolbar.visibility = View.GONE
            }
        })
    }

    private fun showFragment(fragment: Fragment) {
        viewmodel.toggleViewpagerVisibility(false)

        supportFragmentManager.beginTransaction()
            .replace(R.id.main,fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        viewmodel.toggleViewpagerVisibility(true)
    }
}