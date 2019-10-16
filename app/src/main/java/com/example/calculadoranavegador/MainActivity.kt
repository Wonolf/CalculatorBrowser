@file:Suppress("PLUGIN_WARNING")

package com.example.calculadoranavegador

import android.content.res.Configuration
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.webkit.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : AppCompatActivity() {

    /*································
    ······      VARIABLES       ······
    ································*/

    //TextView to display input and output
    private lateinit var txtInput: TextView

    //WebView to display web content
    private var web: WebView? = null

    //TextView to save the memory value
    var memory: String = ""
    //Check if last key pressed is a number or not
    var lastNumber: Boolean = false

    //Represent the current state is in error or not
    var stateError: Boolean = false

    //If true, we can't place another dot
    var isDot: Boolean = false

    /*································
    ······      FUN ONCREATE    ······
    ································*/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(resources.configuration.orientation==Configuration.ORIENTATION_PORTRAIT){
            txtInput = findViewById(R.id.cExpresion)
        } else {
            webBrowser()
        }
    }

    /*································
    ······       NAVEGADOR      ······
    ································*/

    fun webBrowser() {
        web = findViewById<WebView>(R.id.webView)
        web!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return true
            }
        }
        web!!.loadUrl(putUrl.text.toString())
        web!!.loadUrl("https://www.google.com")
    }

    fun search(view: View){
        var character = putUrl.text
        if (URLUtil.isValidUrl(character.toString())){
            web!!.loadUrl(character.toString())
        } else web!!.loadUrl("https://www.google.com/search?q="+character)
    }

    fun google(view: View){
        web!!.loadUrl("https://www.google.com")
    }

    /*································
    ······      CALCULADORA     ······
    ································*/

    fun onNumber(view: View) {
        if (stateError) {
            //if stateError == true, then replace the error message
            txtInput.text = (view as Button).text
            stateError = false
        } else {
            //if not true then we can append to it
            txtInput.append((view as Button).text)
        }

        //we set the flag
        lastNumber = true
    }

    fun onDot(view: View) {
        if (lastNumber && !isDot && !stateError) {
            txtInput.append(".")
            lastNumber = false
            isDot = true
        }
    }

    fun onOperator(view: View) {
        if (lastNumber && !stateError) {
            txtInput.append((view as Button).text)
            lastNumber = false
            isDot = false
        }
    }

    fun onMemorySave(view: View){
        if (lastNumber && !stateError) {
            memory = txtInput.text.toString()
            Toast.makeText(applicationContext,"Number saved on Memory",Toast.LENGTH_SHORT).show()
        }
    }

    fun onMemoryAdd(view: View){
        if (memory == "") {
            Toast.makeText(applicationContext,"Memory is empty. Can't add",Toast.LENGTH_SHORT).show()
        }

        else if (lastNumber && !stateError) {
            val currentValue: Double = (txtInput.text.toString()).toDouble()

            val addMemory = "$currentValue + $memory"

            val expression = ExpressionBuilder(addMemory).build()

            try {
                // Calculate the result and save the result
                val result = expression.evaluate()
                txtInput.text = result.toString()
                Toast.makeText(applicationContext,"Adding done",Toast.LENGTH_SHORT).show()
            } catch (ex: ArithmeticException) {
                // Display an error message
                Toast.makeText(applicationContext,"Error in Memory Operation. Memory is empty now",Toast.LENGTH_LONG).show()
                memory = ""
            }
        }
    }

    fun onMemorySubtract(view: View){
        if (memory == "") {
            Toast.makeText(applicationContext,"Memory is empty. Can't subtract",Toast.LENGTH_SHORT).show()
        }

        else if (lastNumber && !stateError) {
            val currentValue: Double = (txtInput.text.toString()).toDouble()

            val addMemory = "$currentValue - $memory"

            val expression = ExpressionBuilder(addMemory).build()

            try {
                // Calculate the result and save the result
                val result = expression.evaluate()
                txtInput.text = result.toString()
                Toast.makeText(applicationContext,"Subtracting done",Toast.LENGTH_SHORT).show()
            } catch (ex: ArithmeticException) {
                // Display an error message
                Toast.makeText(applicationContext,"Error in Memory Operation. Memory is empty now",Toast.LENGTH_LONG).show()
                memory = ""
            }
        }
    }

    fun onClear(view: View) {
        this.txtInput.text = ""
        lastNumber = false
        stateError = false
        isDot = false
    }

    fun onDelete(view: View) {
        var txtdelete = txtInput.text.toString()

        if (!TextUtils.isEmpty(txtdelete)) {
            txtdelete = txtdelete.substring(0, txtdelete.length - 1)

            txtInput.text = txtdelete
        }
        lastNumber = false
        stateError = false
        isDot = false
    }

    fun onEqual(view: View) {
        // If the current state is error, nothing to do.
        // If the last input is a number only, solution can be found.
        if (lastNumber && !stateError) {
            // Read the expression
            val txt = txtInput.text.toString()
            // Create an Expression (A class from exp4j library)
            val expression = ExpressionBuilder(txt).build()
            try {
                // Calculate the result and display
                val result = expression.evaluate()
                txtInput.text = result.toString()
                isDot = true // Result contains a dot
            } catch (ex: ArithmeticException) {
                // Display an error message
                txtInput.text = "Error"
                stateError = true
                lastNumber = false
            }
        }


    }
}