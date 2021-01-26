package com.example.borrow

import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_ower.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    var owing_amt = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // total part
        val sum_layout = LinearLayout(this)
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        sum_layout.layoutParams = params
        sum_layout.orientation = LinearLayout.HORIZONTAL
        sum_layout.setBackgroundResource(R.drawable.border)

        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        lp.setMargins(10,0,10,0)

        val total_owing = TextView(this)
        total_owing.text = "Sum : "
        total_owing.textSize = 20.0f
        total_owing.setTypeface(null,Typeface.BOLD)
        total_owing.layoutParams = lp
        total_owing.id = 1

        val total_owing_amt = TextView(this)
        total_owing_amt.text = "0"
        owing_amt = total_owing_amt.text.toString().toLong()
        println(owing_amt)
        total_owing_amt.textSize = 20.0f
        total_owing_amt.setTypeface(null,Typeface.BOLD)
        total_owing_amt.layoutParams = lp
        total_owing_amt.id = 2

        total_owing.setTextColor(if(owing_amt >= 0) Color.GREEN else Color.RED)
        total_owing_amt.setTextColor(if(owing_amt >= 0) Color.GREEN else Color.RED)

        sum_layout.addView(total_owing)
        sum_layout.addView(total_owing_amt)

        main_layout.addView(sum_layout)

        // load owers
        val file_name = "owing_list"
        try {
            openFileInput(file_name).use { stream ->
                val text = stream.bufferedReader().use {
                    it.readText()
                }

                for(i in 0 until text.lines().size - 1 step 2){
                    val name = EditText(this)
                    val amt = EditText(this)

                    name.setText(text.lines()[i])
                    amt.setText(text.lines()[i + 1])

                    println(i)
//                    println(text.lines()[i])
//                    println(text.lines()[i + 1].toLong())
                    println(name.text)
                    println(amt.text)

                    createNewOwer(activity_main,name.text,amt.text,2)

                    owing_amt += amt.text.toString().toLong()
                }

                total_owing.setTextColor(if(owing_amt >= 0) Color.GREEN else Color.RED)
                total_owing_amt.text = owing_amt.toString()
                total_owing_amt.setTextColor(if(owing_amt >= 0) Color.GREEN else Color.RED)
            }
        } catch (e: Exception){
            e.printStackTrace()
        }

        // fab
        fab.setOnClickListener { view ->
            createDialog(view)
        }
    }

    // open a dialog of adding new ower
    private fun createDialog(view: View){

        val dialog_view = LayoutInflater.from(this).inflate(R.layout.add_ower,null)

        val dialog_builder = AlertDialog.Builder(this)

        dialog_builder.setTitle("Create new ower")
        dialog_builder.setView(dialog_view)

//            val name = EditText(this)
//            val amt = EditText(this)

        val name = dialog_view.findViewById<EditText>(R.id.ower_name)
        val amt = dialog_view.findViewById<EditText>(R.id.owing_amt)

        val pos_listener = object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface,which: Int){

                when(which){
                    which -> {
                        // check if has the same name ower before
                        val file_name = "owing_list"
                        var same_name = false

                        try{
                            openFileInput(file_name).use{ stream ->
                                val text = stream.bufferedReader().use{
                                    it.readText()
                                }

                                println("jizzz")
                                println(text.lines().size)


                                for(i in 0 until text.lines().size - 1 step 2){
                                    val ower_name = name.text.toString()

                                    println("ower:$ower_name")
                                    println(text.lines()[i])

                                    if(ower_name == text.lines()[i]){
                                        Snackbar.make(view, "This name is already existed!", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show()
                                        println("in used!")
                                        same_name = true
                                        break
                                    }

                                    println("end loop")
                                }
                            }
                        } catch(e: Exception){
                            e.printStackTrace()
                        }

                        if(same_name == false) createNewOwer(view,name.text,amt.text,1)
                    }
                }
            }
        }

        val neg_listener = object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface,which: Int){

                when(which){
                    which -> dialog.dismiss()
                }
            }
        }

        dialog_builder.setPositiveButton("OK",pos_listener)
        dialog_builder.setNegativeButton("Cancel",neg_listener)

        dialog_builder.show()
    }

    // add a new layout for new ower
    private fun createNewOwer(view: View,name: Editable, amt: Editable,type: Int) {
        // new layout added

        if(name.isEmpty()){
            Snackbar.make(view, "Please enter ower's name", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            return
        } else if(amt.isEmpty()){
            Snackbar.make(view, "Please enter owing amount", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            return
        }

        val new_layout = RelativeLayout(this)
        val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT)
        new_layout.layoutParams = params
        new_layout.gravity = Gravity.START
        new_layout.setBackgroundResource(R.drawable.border)

        // name added
        val lp1 = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT)
        lp1.setMargins(30,0,10,0)
        lp1.addRule(RelativeLayout.ALIGN_PARENT_START,RelativeLayout.TRUE)
        lp1.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE)

        val borrow_name = TextView(this)
        borrow_name.text = name.toString()
        borrow_name.textSize = 20.0f
        borrow_name.setTypeface(null,Typeface.BOLD)
        borrow_name.layoutParams = lp1
        borrow_name.id = 11

        // owes added
        val lp2 = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT)
        lp2.setMargins(30,0,10,0)
        lp2.addRule(RelativeLayout.RIGHT_OF,borrow_name.id)
        lp2.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE)

        val owe = TextView(this)
        owe.text = "欠你"
        owe.textSize = 20.0f
        owe.setTypeface(null,Typeface.BOLD)
        owe.setTextColor(Color.RED) //"#FF0000"
        owe.layoutParams = lp2
        owe.id = 12

        // borrow_sum added
        val lp3 = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT)
        lp3.setMargins(30,0,10,0)
        lp3.addRule(RelativeLayout.RIGHT_OF,owe.id)
        lp3.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE)

        val borrow_sum = TextView(this)
        borrow_sum.text = amt.toString()
        borrow_sum.textSize = 20.0f
        borrow_sum.setTypeface(null,Typeface.BOLD)
        borrow_sum.layoutParams = lp3
        borrow_sum.id = 13

        // button added
        val lp4 = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT)
        lp4.setMargins(30,0,10,0)
        lp4.addRule(RelativeLayout.ALIGN_PARENT_END,RelativeLayout.TRUE)
        lp4.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE)

        val popup_btn = Button(this)
        popup_btn.text = "Edit"
        popup_btn.layoutParams = lp4
        popup_btn.id = 14

        // edit ower data
        popup_btn.setOnClickListener{view ->
            val dialog_view = LayoutInflater.from(this).inflate(R.layout.add_ower,null)

            val dialog_builder = AlertDialog.Builder(this)

            dialog_builder.setTitle("Edit ower")
            dialog_builder.setView(dialog_view)

//            val name = EditText(this)
//            val amt = EditText(this)

            val name = dialog_view.findViewById<EditText>(R.id.ower_name)
            val amt = dialog_view.findViewById<EditText>(R.id.owing_amt)

            name.setText(borrow_name.text.toString())
            amt.setText(borrow_sum.text.toString())

            val pos_listener = object: DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface,which: Int){

                    when(which){
                        which ->
                            if(name.text.isEmpty()){
                                Snackbar.make(view, "Please enter ower's name", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show()
                                return
                            } else if(amt.text.isEmpty()){
                                Snackbar.make(view, "Please enter owing amount", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show()
                                return
                            } else {
                                val file_name = "owing_list"
                                var same_name = false

                                openFileInput(file_name).use { stream ->
                                    val text = stream.bufferedReader().use{
                                        it.readText()
                                    }

                                    for(i in 0 until text.lines().size - 1 step 2){
                                        // name already exist
                                        if(name.text.toString() == text.lines()[i]){
                                            Snackbar.make(view, "This name is already existed!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show()
                                            same_name = true
                                        }
                                    }
                                }

                                if(same_name == true) return

                                // into chaging stage

                                openFileInput(file_name).use { stream ->
                                    val text = stream.bufferedReader().use{
                                        it.readText()
                                    }

                                    val output_stream = openFileOutput(file_name,Context.MODE_PRIVATE)

                                    for(i in 0 until text.lines().size - 1 step 2){
                                        val exist_name = text.lines()[i]
                                        val exist_amt = text.lines()[i + 1]
                                        val exist_write = "$exist_name\n$exist_amt\n"

                                        if(borrow_name.text.toString() == exist_name){
                                            // the ower needs to change
                                            val new_name = name.text.toString()
                                            val new_amt = amt.text.toString()
                                            val new_write = "$new_name\n$new_amt\n"

                                            output_stream.write(new_write.toByteArray())
                                        } else {
                                            output_stream.write(exist_write.toByteArray())
                                        }
                                    }

                                    output_stream.close()
                                }

                                owing_amt -= borrow_sum.text.toString().toLong()
                                borrow_name.text = name.text.toString()
                                borrow_sum.text = amt.text.toString()

                                println(borrow_sum.text)

                                owing_amt += borrow_sum.text.toString().toLong()
                                val tot = findViewById<TextView>(1)
                                val tot_amt = findViewById<TextView>(2)

                                tot.setTextColor(if(owing_amt >= 0) Color.GREEN else Color.RED)
                                tot_amt.text = owing_amt.toString()
                                tot_amt.setTextColor(if(owing_amt >= 0) Color.GREEN else Color.RED)
                            }
                    }
                }
            }

            val neg_listener = object: DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface,which: Int){

                    when(which){
                        which -> dialog.dismiss()
                    }
                }
            }

            dialog_builder.setPositiveButton("OK",pos_listener)
            dialog_builder.setNegativeButton("Cancel",neg_listener)

            dialog_builder.show()
        }

        // from dialog
        if(type == 1){
            // change text color if necessary
            owing_amt += amt.toString().toLong()
            val tot = findViewById<TextView>(1)
            val tot_amt = findViewById<TextView>(2)

            tot.setTextColor(if(owing_amt >= 0) Color.GREEN else Color.RED)
            tot_amt.text = owing_amt.toString()
            tot_amt.setTextColor(if(owing_amt >= 0) Color.GREEN else Color.RED)

            // store data
            val file_name = "owing_list"
            val ower_name = name.toString()
            val ower_amt = amt.toString()
            val owe_data = "$ower_name\n$ower_amt\n"

            try{
                val output_stream = openFileOutput(file_name,Context.MODE_PRIVATE or Context.MODE_APPEND)
                output_stream.write(owe_data.toByteArray())
                output_stream.close()
                println(getFilesDir().getAbsolutePath())
                println(Environment.getDataDirectory().absolutePath)
            } catch (e: Exception){
                e.printStackTrace()
            }
        }

        // delete ower
        new_layout.setOnLongClickListener {

            val dialog_builder = AlertDialog.Builder(this)

            dialog_builder.setTitle("Delete this ower?")

            val pos_listener = object: DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface,which: Int){

                    when(which){
                        which -> {
                            val file_name = "owing_list"

                            openFileInput(file_name).use { stream ->
                                val text = stream.bufferedReader().use {
                                    it.readText()
                                }

                                println(text.lines().size)
                                val output_stream = openFileOutput(file_name,Context.MODE_PRIVATE)

                                for(i in 0 until text.lines().size - 1 step 2){
                                    val ower_name = text.lines()[i]
                                    val ower_amt = text.lines()[i + 1]

                                    val ower_write = "$ower_name\n$ower_amt\n"

//                                    println(ower_name)
//                                    println(name.toString())
//                                    println(ower_name == name.toString())

                                    if(ower_name != name.toString()){
                                        println(ower_write)
                                        output_stream.write(ower_write.toByteArray())
                                    } else {
                                        val tot_amt = findViewById<TextView>(2)
                                        owing_amt -= ower_amt.toLong()
                                        tot_amt.setText(owing_amt.toString())
                                    }
                                }
                                output_stream.close()
                            }


                            new_layout.visibility = View.GONE
                        }

                    }
                }
            }

            val neg_listener = object: DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface,which: Int){

                    when(which){
                        which -> dialog.dismiss()
                    }
                }
            }

            dialog_builder.setPositiveButton("OK",pos_listener)
            dialog_builder.setNegativeButton("Cancel",neg_listener)

            dialog_builder.show()

            true
        }

        new_layout.addView(borrow_name)
        new_layout.addView(owe)
        new_layout.addView(borrow_sum)
        new_layout.addView(popup_btn)

        main_layout.addView(new_layout)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}

// TODO:
// 1. deal with overflow
// 2. storage problem