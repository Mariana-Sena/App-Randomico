package com.example.apprandomico // Use seu package real aqui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val pessoas = mutableListOf<String>()
    private val tarefas = mutableListOf<String>()
    private lateinit var personsAdapter: ArrayAdapter<String>
    private lateinit var tasksAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val edtPerson = findViewById<EditText>(R.id.edtPerson)
        val btnAddPerson = findViewById<ImageButton>(R.id.btnAddPerson)
        val listPersons = findViewById<ListView>(R.id.listPersons)
        val btnRemovePerson = findViewById<ImageButton>(R.id.btnRemovePerson)

        val edtTask = findViewById<EditText>(R.id.edtTask)
        val btnAddTask = findViewById<ImageButton>(R.id.btnAddTask)
        val listTasks = findViewById<ListView>(R.id.listTasks)
        val btnRemoveTask = findViewById<ImageButton>(R.id.btnRemoveTask)

        val btnDelegate = findViewById<Button>(R.id.btnDelegate)
        val txtResult = findViewById<TextView>(R.id.txtResult)

        personsAdapter = object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pessoas) {
            override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getView(position, convertView, parent)
                (view as TextView).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_person, 0, 0, 0)
                return view
            }
        }
        listPersons.adapter = personsAdapter

        tasksAdapter = object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tarefas) {
            override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getView(position, convertView, parent)
                (view as TextView).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_task, 0, 0, 0)
                return view
            }
        }
        listTasks.adapter = tasksAdapter

        btnAddPerson.setOnClickListener {
            val nome = edtPerson.text.toString().trim()
            if (nome.isNotEmpty()) {
                pessoas.add(nome)
                personsAdapter.notifyDataSetChanged()
                edtPerson.text.clear()
            }
        }

        btnRemovePerson.setOnClickListener {
            val pos = listPersons.checkedItemPosition
            if (pos >= 0 && pos < pessoas.size) {
                pessoas.removeAt(pos)
                personsAdapter.notifyDataSetChanged()
                listPersons.clearChoices()
            }
        }

        btnAddTask.setOnClickListener {
            val tarefa = edtTask.text.toString().trim()
            if (tarefa.isNotEmpty()) {
                tarefas.add(tarefa)
                tasksAdapter.notifyDataSetChanged()
                edtTask.text.clear()
            }
        }

        btnRemoveTask.setOnClickListener {
            val pos = listTasks.checkedItemPosition
            if (pos >= 0 && pos < tarefas.size) {
                tarefas.removeAt(pos)
                tasksAdapter.notifyDataSetChanged()
                listTasks.clearChoices()
            }
        }

        btnDelegate.setOnClickListener {
            if (pessoas.isEmpty() || tarefas.isEmpty()) {
                txtResult.text = "Adicione pelo menos uma pessoa e uma tarefa!"
            } else {
                val pessoa = pessoas.random()
                val tarefa = tarefas.random()
                txtResult.text = "$pessoa vai fazer: $tarefa"
            }
        }
    }
}