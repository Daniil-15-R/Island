package com.baskettoria.island

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.tooling.preview.Preview
import com.baskettoria.island.ui.theme.IslandTheme
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.ThreadLocalRandom

abstract class Animal(val unicodeSymbol: String, val initialHealth: Int) {
    var health: Int = initialHealth
    var direction: Int = ThreadLocalRandom.current().nextInt(4)
    var stepsInCurrentDirection: Int = ThreadLocalRandom.current().nextInt(1, 5)
    var satiety: Double = 0.0
    var lastEatenTime: Long = System.currentTimeMillis() // Время последнего приема пищи

    open fun updateDirection() {
        stepsInCurrentDirection--
        if (stepsInCurrentDirection <= 0) {
            direction = ThreadLocalRandom.current().nextInt(4)
            stepsInCurrentDirection = ThreadLocalRandom.current().nextInt(1, 5)
        }
    }

    open fun isAlive(): Boolean = health > 0

    open fun eat(foodValue: Double) {
        satiety += foodValue
        if (satiety > getMaxSatiety()) {
            satiety = getMaxSatiety()
        }
        lastEatenTime = System.currentTimeMillis() // Обновляем время последнего приема пищи
    }

    open fun getMaxSatiety(): Double {
        return when (this) {
            is Wolf -> 8.0
            is Boa -> 3.0
            is Fox -> 2.0
            is Bear -> 80.0
            is Eagle -> 1.0
            is Horse -> 60.0
            is Deer -> 50.0
            is Rabbit -> 0.45
            is Mouse -> 0.01
            is Goat -> 10.0
            is Sheep -> 15.0
            is Boar -> 50.0
            is Buffalo -> 100.0
            is Duck -> 0.15
            is Caterpillar -> 0.0
            else -> 0.0
        }
    }

    open fun decreaseSatiety() {
        satiety -= 0.1
        if (satiety < 0) {
            satiety = 0.0
        }
    }

    open fun die() {
        health = 0
    }

    open fun reproduce(): Animal? {
        return when (this) {
            is Wolf -> Wolf()
            is Boa -> Boa()
            is Fox -> Fox()
            is Bear -> Bear()
            is Eagle -> Eagle()
            is Horse -> Horse()
            is Deer -> Deer()
            is Rabbit -> Rabbit()
            is Mouse -> Mouse()
            is Goat -> Goat()
            is Sheep -> Sheep()
            is Boar -> Boar()
            is Buffalo -> Buffalo()
            is Duck -> Duck()
            is Caterpillar -> Caterpillar()
            is Plants -> Plants()
            else -> null
        }
    }

    open fun canEat(other: Animal): Boolean {
        return false
    }

    open fun canEatPlant(): Boolean {
        return false
    }

    // Проверка, умерло ли животное от голода
    open fun checkStarvation(): Boolean {
        val currentTime = System.currentTimeMillis()
        return satiety <= 0 && currentTime - lastEatenTime >= 5000 // 5 секунд без еды
    }
}

abstract class Predator(unicodeSymbol: String, initialHealth: Int) : Animal(unicodeSymbol, initialHealth)

abstract class Herbivore(unicodeSymbol: String, initialHealth: Int) : Animal(unicodeSymbol, initialHealth)

class Wolf : Predator("🐺", 50) {
    override fun canEat(other: Animal): Boolean {
        return when (other) {
            is Rabbit -> ThreadLocalRandom.current().nextInt(100) < 60
            is Mouse -> ThreadLocalRandom.current().nextInt(100) < 80
            is Goat -> ThreadLocalRandom.current().nextInt(100) < 60
            is Sheep -> ThreadLocalRandom.current().nextInt(100) < 70
            is Boar -> ThreadLocalRandom.current().nextInt(100) < 15
            is Buffalo -> ThreadLocalRandom.current().nextInt(100) < 10
            is Duck -> ThreadLocalRandom.current().nextInt(100) < 40
            else -> false
        }
    }
}

class Boa : Predator("🐍", 50) {
    override fun canEat(other: Animal): Boolean {
        return when (other) {
            is Rabbit -> ThreadLocalRandom.current().nextInt(100) < 20
            is Mouse -> ThreadLocalRandom.current().nextInt(100) < 40
            is Duck -> ThreadLocalRandom.current().nextInt(100) < 10
            else -> false
        }
    }
}

class Fox : Predator("🦊", 50) {
    override fun canEat(other: Animal): Boolean {
        return when (other) {
            is Rabbit -> ThreadLocalRandom.current().nextInt(100) < 70
            is Mouse -> ThreadLocalRandom.current().nextInt(100) < 90
            is Duck -> ThreadLocalRandom.current().nextInt(100) < 60
            is Caterpillar -> ThreadLocalRandom.current().nextInt(100) < 40
            else -> false
        }
    }
}

class Bear : Predator("🐻", 50) {
    override fun canEat(other: Animal): Boolean {
        return when (other) {
            is Rabbit -> ThreadLocalRandom.current().nextInt(100) < 80
            is Mouse -> ThreadLocalRandom.current().nextInt(100) < 90
            is Goat -> ThreadLocalRandom.current().nextInt(100) < 70
            is Sheep -> ThreadLocalRandom.current().nextInt(100) < 70
            is Boar -> ThreadLocalRandom.current().nextInt(100) < 50
            is Buffalo -> ThreadLocalRandom.current().nextInt(100) < 20
            is Duck -> ThreadLocalRandom.current().nextInt(100) < 10
            else -> false
        }
    }
}

class Eagle : Predator("🦅", 50) {
    override fun canEat(other: Animal): Boolean {
        return when (other) {
            is Rabbit -> ThreadLocalRandom.current().nextInt(100) < 90
            is Mouse -> ThreadLocalRandom.current().nextInt(100) < 90
            is Duck -> ThreadLocalRandom.current().nextInt(100) < 80
            else -> false
        }
    }
}

class Horse : Herbivore("🐎", 50) {
    override fun canEatPlant(): Boolean = true
}

class Deer : Herbivore("🦌", 50) {
    override fun canEatPlant(): Boolean = true
}

class Rabbit : Herbivore("🐇", 50) {
    override fun canEatPlant(): Boolean = true
}

class Mouse : Herbivore("🐁", 50) {
    override fun canEatPlant(): Boolean = true
}

class Goat : Herbivore("🐐", 50) {
    override fun canEatPlant(): Boolean = true
}

class Sheep : Herbivore("🐑", 50) {
    override fun canEatPlant(): Boolean = true
}

class Boar : Herbivore("🐗", 50) {
    override fun canEatPlant(): Boolean = true
}

class Buffalo : Herbivore("🐃", 50) {
    override fun canEatPlant(): Boolean = true
}

class Duck : Herbivore("🦆", 50) {
    override fun canEat(other: Animal): Boolean {
        return when (other) {
            is Caterpillar -> ThreadLocalRandom.current().nextInt(100) < 90
            else -> false
        }
    }

    override fun canEatPlant(): Boolean = true
}

class Caterpillar : Herbivore("🐛", 50) {
    override fun canEatPlant(): Boolean = true
}

class Plants : Animal("🌿", 100) {
    override fun canEatPlant(): Boolean = false
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IslandTheme {
                IslandView()
            }
        }
    }
}

class Island(var width: Int, var height: Int) {
    private val grid = Array(width) { Array(height) { Cell() } }
    private val deadAnimals = mutableMapOf<Class<out Animal>, Long>()

    fun getCell(x: Int, y: Int): Cell = grid[x][y]

    fun generateGrass(probability: Double) {
        for (i in 0 until width) {
            for (j in 0 until height) {
                if (ThreadLocalRandom.current().nextDouble() < probability) {
                    grid[i][j].hasGrass = true
                }
            }
        }
    }

    fun moveAnimals() {
        val newPositions = mutableMapOf<Pair<Int, Int>, MutableList<Animal>>()

        for (i in 0 until width) {
            for (j in 0 until height) {
                val cell = grid[i][j]
                for (animal in cell.animals) {
                    if (animal is Plants) continue

                    val direction = animal.direction
                    val (newX, newY) = moveDirection(i, j, direction)

                    if (newX in 0 until width && newY in 0 until height) {
                        newPositions.getOrPut(newX to newY) { mutableListOf() }.add(animal)
                    }

                    animal.updateDirection()
                }
            }
        }

        for (i in 0 until width) {
            for (j in 0 until height) {
                grid[i][j].animals.removeIf { !it.isAlive() }
            }
        }

        for ((position, animals) in newPositions) {
            val (x, y) = position
            grid[x][y].animals.addAll(animals)
        }
    }

    private fun moveDirection(x: Int, y: Int, direction: Int): Pair<Int, Int> {
        return when (direction) {
            0 -> x - 1 to y
            1 -> x + 1 to y
            2 -> x to y - 1
            3 -> x to y + 1
            else -> x to y
        }
    }

    fun populateAnimals() {
        val animals = listOf(
            Wolf::class to 5,
            Boa::class to 5,
            Fox::class to 5,
            Bear::class to 5,
            Eagle::class to 5,
            Horse::class to 5,
            Deer::class to 5,
            Rabbit::class to 5,
            Mouse::class to 5,
            Goat::class to 5,
            Sheep::class to 5,
            Boar::class to 5,
            Buffalo::class to 5,
            Duck::class to 5,
            Caterpillar::class to 5,
            Plants::class to 5
        )

        for ((animalClass, maxCount) in animals) {
            val count = ThreadLocalRandom.current().nextInt(1, maxCount + 1)
            repeat(count) {
                val animal = when (animalClass) {
                    Wolf::class -> Wolf()
                    Boa::class -> Boa()
                    Fox::class -> Fox()
                    Bear::class -> Bear()
                    Eagle::class -> Eagle()
                    Horse::class -> Horse()
                    Deer::class -> Deer()
                    Rabbit::class -> Rabbit()
                    Mouse::class -> Mouse()
                    Goat::class -> Goat()
                    Sheep::class -> Sheep()
                    Boar::class -> Boar()
                    Buffalo::class -> Buffalo()
                    Duck::class -> Duck()
                    Caterpillar::class -> Caterpillar()
                    Plants::class -> Plants()
                    else -> throw IllegalArgumentException("Unknown animal class")
                }
                var x: Int
                var y: Int
                do {
                    x = ThreadLocalRandom.current().nextInt(width)
                    y = ThreadLocalRandom.current().nextInt(height)
                } while (grid[x][y].animals.size >= 5)

                grid[x][y].animals.add(animal)
            }
        }
    }

    fun processEating(): List<String> {
        val messages = mutableListOf<String>()
        for (i in 0 until width) {
            for (j in 0 until height) {
                val cell = grid[i][j]
                val animals = cell.animals.toList()
                for (predator in animals) {
                    for (prey in animals) {
                        if (predator != prey && predator.isAlive() && prey.isAlive() && predator.canEat(prey)) {
                            prey.die()
                            val foodValue = getFoodValue(predator, prey)
                            predator.eat(foodValue)
                            messages.add("${predator.unicodeSymbol} съел ${prey.unicodeSymbol} в клетке ($i, $j) и стал сытым на ${predator.satiety}/${predator.getMaxSatiety()}")
                            deadAnimals[prey::class.java] = System.currentTimeMillis()
                        }
                    }
                }
                for (animal in cell.animals) {
                    if (animal is Herbivore && cell.hasGrass && animal.canEatPlant()) {
                        animal.eat(10.0)
                        cell.hasGrass = false
                        messages.add("${animal.unicodeSymbol} съел траву в клетке ($i, $j) и стал сытым на ${animal.satiety}/${animal.getMaxSatiety()}")
                    }
                }

                // Восстановление травы с вероятностью 10% после того, как её съели
                if (!cell.hasGrass && ThreadLocalRandom.current().nextDouble() < 0.1) {
                    cell.hasGrass = true
                    messages.add("Трава выросла в клетке ($i, $j)")
                }
            }
        }
        return messages
    }

    fun processReproduction(): List<String> {
        val messages = mutableListOf<String>()
        val currentTime = System.currentTimeMillis()
        val animalsToReproduce = deadAnimals.filter { (_, deathTime) ->
            currentTime - deathTime >= 3000 // 3 секунды
        }.keys

        for (animalClass in animalsToReproduce) {
            val x = ThreadLocalRandom.current().nextInt(width)
            val y = ThreadLocalRandom.current().nextInt(height)
            val cell = grid[x][y]

            // Проверяем, что в клетке есть свободное место для нового животного
            if (cell.animals.size < 5) {
                val newAnimal = when (animalClass) {
                    Wolf::class -> Wolf()
                    Boa::class -> Boa()
                    Fox::class -> Fox()
                    Bear::class -> Bear()
                    Eagle::class -> Eagle()
                    Horse::class -> Horse()
                    Deer::class -> Deer()
                    Rabbit::class -> Rabbit()
                    Mouse::class -> Mouse()
                    Goat::class -> Goat()
                    Sheep::class -> Sheep()
                    Boar::class -> Boar()
                    Buffalo::class -> Buffalo()
                    Duck::class -> Duck()
                    Caterpillar::class -> Caterpillar()
                    Plants::class -> Plants()
                    else -> null
                }
                if (newAnimal != null) {
                    cell.animals.add(newAnimal)
                    messages.add("${newAnimal.unicodeSymbol} родился в клетке ($x, $y)")
                    deadAnimals.remove(animalClass) // Удаляем запись о смерти, чтобы не рождались повторно
                }
            }
        }
        return messages
    }

    fun processStarvation(): List<String> {
        val messages = mutableListOf<String>()
        for (i in 0 until width) {
            for (j in 0 until height) {
                val cell = grid[i][j]
                val animals = cell.animals.toList()
                for (animal in animals) {
                    if (animal.checkStarvation()) {
                        animal.die()
                        messages.add("${animal.unicodeSymbol} умер от голода в клетке ($i, $j)")
                    }
                }
            }
        }
        return messages
    }

    private fun getFoodValue(predator: Animal, prey: Animal): Double {
        return when (predator) {
            is Wolf -> when (prey) {
                is Rabbit -> 60.0
                is Mouse -> 80.0
                is Goat -> 60.0
                is Sheep -> 70.0
                is Boar -> 15.0
                is Buffalo -> 10.0
                is Duck -> 40.0
                else -> 0.0
            }
            is Boa -> when (prey) {
                is Rabbit -> 20.0
                is Mouse -> 40.0
                is Duck -> 10.0
                else -> 0.0
            }
            is Fox -> when (prey) {
                is Rabbit -> 70.0
                is Mouse -> 90.0
                is Duck -> 60.0
                is Caterpillar -> 40.0
                else -> 0.0
            }
            is Bear -> when (prey) {
                is Rabbit -> 80.0
                is Mouse -> 90.0
                is Goat -> 70.0
                is Sheep -> 70.0
                is Boar -> 50.0
                is Buffalo -> 20.0
                is Duck -> 10.0
                else -> 0.0
            }
            is Eagle -> when (prey) {
                is Rabbit -> 90.0
                is Mouse -> 90.0
                is Duck -> 80.0
                else -> 0.0
            }
            else -> 0.0
        }
    }

    fun decreaseSatietyForAllAnimals() {
        for (i in 0 until width) {
            for (j in 0 until height) {
                val cell = grid[i][j]
                for (animal in cell.animals) {
                    animal.decreaseSatiety()
                }
            }
        }
    }
}

class Cell {
    var hasGrass: Boolean = false
    val animals = mutableListOf<Animal>()
}

data class AnimalPosition(
    val x: Float,
    val y: Float,
    val targetX: Float,
    val targetY: Float
)

@Composable
fun IslandView() {
    var islandWidth by remember { mutableStateOf(20) }
    var islandHeight by remember { mutableStateOf(10) }

    val island = remember(islandWidth, islandHeight) {
        Island(islandWidth, islandHeight).apply {
            generateGrass(0.5)
            populateAnimals()
        }
    }

    val textMeasurer = rememberTextMeasurer()
    val textStyle = TextStyle(fontSize = 12.sp, textAlign = TextAlign.Center)

    val animalPositions = remember { mutableStateMapOf<Animal, AnimalPosition>() }
    var messages by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        val scheduler = Executors.newScheduledThreadPool(1)
        val executor = Executors.newFixedThreadPool(4)

        scheduler.scheduleAtFixedRate({
            executor.submit {
                for (i in 0 until island.width) {
                    for (j in 0 until island.height) {
                        val cell = island.getCell(i, j)
                        if (!cell.hasGrass && ThreadLocalRandom.current().nextDouble() < 0.1) {
                            cell.hasGrass = true
                        }
                    }
                }
            }

            executor.submit {
                island.moveAnimals()
                val newMessages = island.processEating()
                val reproductionMessages = island.processReproduction()
                messages = newMessages + reproductionMessages

                for (i in 0 until island.width) {
                    for (j in 0 until island.height) {
                        val cell = island.getCell(i, j)
                        for (animal in cell.animals) {
                            val currentPosition = animalPositions[animal]
                            if (currentPosition != null) {
                                animalPositions[animal] = currentPosition.copy(
                                    targetX = (i.coerceIn(0, island.width - 1)).toFloat(),
                                    targetY = (j.coerceIn(0, island.height - 1)).toFloat()
                                )
                            } else {
                                animalPositions[animal] = AnimalPosition(
                                    x = (i.coerceIn(0, island.width - 1)).toFloat(),
                                    y = (j.coerceIn(0, island.height - 1)).toFloat(),
                                    targetX = (i.coerceIn(0, island.width - 1)).toFloat(),
                                    targetY = (j.coerceIn(0, island.height - 1)).toFloat()
                                )
                            }
                        }
                    }
                }
            }

            // Задача для вывода статистики
            executor.submit {
                println("Статистика: ${messages.joinToString("\n")}")
            }
        }, 0, 10, TimeUnit.SECONDS) // Увеличили время передвижения до 10 секунд
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Отображение сообщений
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp) // Уменьшили высоту до 100.dp
                .padding(8.dp)
        ) {
            items(messages) { message ->
                Text(
                    text = message,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    style = TextStyle(fontSize = 14.sp)
                )
            }
        }

        val animatedPositions = animalPositions.mapValues { (_, position) ->
            val animatedX = animateFloatAsState(
                targetValue = position.targetX,
                animationSpec = tween(durationMillis = 5000) // Увеличили время анимации до 10 секунд
            ).value

            val animatedY = animateFloatAsState(
                targetValue = position.targetY,
                animationSpec = tween(durationMillis = 5000)
            ).value

            Offset(animatedX, animatedY)
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellWidth = size.width / island.width.toFloat()
            val cellHeight = size.height / island.height.toFloat()

            for (i in 0 until island.width) {
                for (j in 0 until island.height) {
                    val cell = island.getCell(i, j)
                    val color = if (cell.hasGrass) Color.Green else Color.Gray

                    drawRect(
                        color = color,
                        topLeft = Offset(i * cellWidth, j * cellHeight),
                        size = androidx.compose.ui.geometry.Size(cellWidth, cellHeight)
                    )

                    if (cell.hasGrass) {
                        val textLayoutResult = textMeasurer.measure(
                            text = "🌿",
                            style = textStyle
                        )
                        val offsetX = (cellWidth - textLayoutResult.size.width) / 2
                        val offsetY = (cellHeight - textLayoutResult.size.height) / 2

                        val symbolX = (i * cellWidth + offsetX).coerceIn(
                            0f,
                            size.width - textLayoutResult.size.width
                        )
                        val symbolY = (j * cellHeight + offsetY).coerceIn(
                            0f,
                            size.height - textLayoutResult.size.height
                        )

                        drawText(
                            textLayoutResult = textLayoutResult,
                            topLeft = Offset(symbolX, symbolY)
                        )
                    }

                    cell.animals.forEachIndexed { index, animal ->
                        val position = animatedPositions[animal] ?: Offset(i.toFloat(), j.toFloat())

                        val textLayoutResult = textMeasurer.measure(
                            text = animal.unicodeSymbol,
                            style = textStyle
                        )
                        val offsetX = (cellWidth - textLayoutResult.size.width) / 2
                        val offsetY = (cellHeight - textLayoutResult.size.height) / 2

                        val symbolX = (position.x * cellWidth + offsetX).coerceIn(
                            0f,
                            size.width - textLayoutResult.size.width
                        )
                        val symbolY = (position.y * cellHeight + offsetY).coerceIn(
                            0f,
                            size.height - textLayoutResult.size.height
                        )

                        drawText(
                            textLayoutResult = textLayoutResult,
                            topLeft = Offset(symbolX, symbolY)
                        )
                    }

                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(i * cellWidth, j * cellHeight),
                        size = androidx.compose.ui.geometry.Size(cellWidth, cellHeight),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    IslandTheme {
        IslandView()
    }
}
