package com.example.randomtaskdelegator

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card 
import androidx.compose.material3.CardDefaults 
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api 
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet 
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults 
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState 
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.BottomSheetDefaults 
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign 
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Definição da paleta de cores
private val LightColors = lightColorScheme(
    primary = Color(0xFF673AB7),
    onPrimary = Color.Yellow,     // Cor do ícones sobre a cor primária
    secondary = Color(0xFF8E59EF), // Um roxo um pouco diferente
    onSecondary = Color.Black,   // Cor do texto/ícones sobre a cor secundária
    surface = Color.White,       // Fundo dos cards e elementos
    onSurface = Color.Black,      // Cor do texto sobre a superfície
    onSurfaceVariant = Color.DarkGray, // Cor para texto secundário em superfícies
    background = Color(0xFFF0F0F0) // Cor de fundo para os Cards na tela de sorteio
)

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = LightColors
            ) {
                TaskDelegatorApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class) //ModalBottomSheet
@Composable
fun TaskDelegatorApp() {
    var people by remember { mutableStateOf(listOf<String>()) }
    var tasks by remember { mutableStateOf(listOf<String>()) }
    // AGORA lastDelegation é um Map de String para List<String>
    var lastDelegation by remember { mutableStateOf<Map<String, List<String>>?>(null) }
    val context = LocalContext.current

    // Estado para controlar a exibição da folha inferior
    var showLastDelegationSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true) // Faz com que a folha abra sempre expandida
    val scope = rememberCoroutineScope() // Usado para esconder a folha

    Scaffold{
        paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Column(Modifier.weight(1f)) {
                PeopleTasksCarousel(
                    people = people,
                    tasks = tasks,
                    addPerson = { person -> people = people + person },
                    removePerson = { person -> people = people.filter { it != person } },
                    addTask = { task -> tasks = tasks + task },
                    removeTask = { task -> tasks = tasks.filter { it != task } }
                )
            }

            // Botão "Sortear Tarefas"
            Button(
                onClick = {
                    if (people.isNotEmpty() && tasks.isNotEmpty()) {
                        val shuffledPeople = people.shuffled()
                        val assignments = mutableMapOf<String, MutableList<String>>()

                        // Lógica de distribuição de tarefas
                        for ((index, task) in tasks.withIndex()) {
                            val person = shuffledPeople[index % shuffledPeople.size]
                            assignments.getOrPut(person) { mutableListOf() }.add(task)
                        }

                        lastDelegation = assignments

                        Toast.makeText(context, "Tarefas delegadas!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Adicione pessoas e tarefas para delegar!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                contentPadding = PaddingValues(16.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delegate), // ic_delegate.xml em res/drawable
                        contentDescription = "Delegar",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sortear Tarefas", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            // Botão "Ver último sorteio"
            Button(
                onClick = {
                    showLastDelegationSheet = true // Exibe a folha inferior ao clicar
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                contentPadding = PaddingValues(16.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.sorteio), // ic_sorteio.xml em res/drawable
                        contentDescription = "Ver Sorteio",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ver último sorteio", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        // ModalBottomSheet para exibir o último sorteio
        if (showLastDelegationSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    // Esconde a folha quando o usuário arrasta para baixo ou clica fora
                    showLastDelegationSheet = false
                },
                sheetState = sheetState,
                dragHandle = { BottomSheetDefaults.DragHandle() }, // Mostra uma alça para arrastar a folha
                containerColor = MaterialTheme.colorScheme.surface // Cor de fundo da folha
            ) {
                // Conteúdo da folha inferior
                // Passando o lastDelegation como Map
                LastDelegationScreen(lastDelegation = lastDelegation)
                Spacer(modifier = Modifier.height(32.dp)) // Espaço para respirar na parte inferior
            }
        }
    }
}

@Composable
fun LastDelegationScreen(lastDelegation: Map<String, List<String>>?) {
    Column(
        modifier = Modifier
            .fillMaxWidth() // Preenche a largura da folha
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally // Centraliza o conteúdo horizontalmente
    ) {
        if (lastDelegation.isNullOrEmpty()) {
            Text(
                text = "Ainda não houve nenhum sorteio.",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 32.dp)
            )
        } else {
            Text(
                text = "Último Sorteio:",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
            LazyColumn(
                modifier = Modifier.fillMaxWidth() // Preenche a largura disponível
            ) {
                // Iterar sobre cada entrada (Pessoa -> Lista de Tarefas) no mapa
                items(lastDelegation.entries.toList()) { (personName, tasksAssigned) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(4.dp), // Aumenta um pouco a elevação
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // Cor de fundo do Card
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Nome da Pessoa
                            Text(
                                text = personName,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary // Cor do nome da pessoa
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Lista de tarefas com Checkbox
                            tasksAssigned.forEach { task ->
                                var isChecked by remember { mutableStateOf(false) } // Estado individual para cada checkbox
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = { isChecked = it },
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = task,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PeopleTasksCarousel(
    people: List<String>,
    tasks: List<String>,
    addPerson: (String) -> Unit,
    removePerson: (String) -> Unit,
    addTask: (String) -> Unit,
    removeTask: (String) -> Unit
) {
    var personNameInput by remember { mutableStateOf("") }
    var taskNameInput by remember { mutableStateOf("") }
    val pages = listOf("Pessoas", "Tarefas")

    var pageIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f), shape = MaterialTheme.shapes.medium)
            .border(1.dp, Color.Gray, shape = MaterialTheme.shapes.medium)
            .padding(8.dp)
    ) {
        TabRow(
            selectedTabIndex = pageIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            pages.forEachIndexed { index, title ->
                Tab(
                    selected = pageIndex == index,
                    onClick = { pageIndex = index },
                    icon = {
                        when (index) {
                            0 -> Icon(painter = painterResource(id = R.drawable.ic_person), contentDescription = "Pessoas")
                            1 -> Icon(painter = painterResource(id = R.drawable.ic_task), contentDescription = "Tarefas")
                        }
                    },
                    text = { Text(title) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (pageIndex == 0) {
            Column(modifier = Modifier.fillMaxSize()) {
                LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    items(people) { person ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(person, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                            IconButton(onClick = { removePerson(person) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Remover Pessoa", tint = Color.Red)
                            }
                        }
                        HorizontalDivider()
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = personNameInput,
                    onValueChange = { personNameInput = it },
                    label = { Text("Nome da Pessoa") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedSupportingTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedSupportingTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (personNameInput.isNotBlank()) {
                            addPerson(personNameInput.trim())
                            personNameInput = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Adicionar Pessoa", fontSize = 15.sp, color = Color.White)
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    items(tasks) { task ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(task, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                            IconButton(onClick = { removeTask(task) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Remover Tarefa", tint = Color.Red)
                            }
                        }
                        HorizontalDivider()
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = taskNameInput,
                    onValueChange = { taskNameInput = it },
                    label = { Text("Nome da Tarefa") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedSupportingTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedSupportingTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (taskNameInput.isNotBlank()) {
                            addTask(taskNameInput.trim())
                            taskNameInput = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Adicionar Tarefa", fontSize = 15.sp, color = Color.White)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialTheme(colorScheme = LightColors) {
        TaskDelegatorApp()
    }
}