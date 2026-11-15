package ru.pkgh.lib

import java.util.Locale

fun wattsToKilowatts(watts: Double): Double = watts / 1000.0
fun kilowattsToWatts(kw: Double): Double = kw * 1000.0
fun calculatePowerWatts(voltage: Double, current: Double): Double = voltage * current

fun calculateEnergyCost(powerWatts: Double, hours: Double, tariffPerKwh: Double): Double {
    val powerKw = wattsToKilowatts(powerWatts)
    return powerKw * hours * tariffPerKwh
}

fun isValidElectricValue(value: Double): Boolean = value >= 0.0

fun describeConsumption(powerWatts: Double): String = when {
    powerWatts < 100.0 -> "низкое потребление"
    powerWatts < 1000.0 -> "умеренное потребление"
    powerWatts < 2500.0 -> "высокое потребление"
    else -> "очень высокое потребление"
}

fun formatPower(
    powerWatts: Double,
    inKilowatts: Boolean = false,
    digits: Int = 2,
    showCategory: Boolean = false
): String {
    val value = if (inKilowatts) wattsToKilowatts(powerWatts) else powerWatts
    val unit = if (inKilowatts) "кВт" else "Вт"
    val formattedNumber = String.format(Locale.US, "%.${digits}f", value)
    val categoryPart = if (showCategory) " (${describeConsumption(powerWatts)})" else ""

    return "$formattedNumber $unit$categoryPart"
}

fun powerStats(vararg powersWatts: Double): Triple<Double, Double, Double> {
    if (powersWatts.isEmpty()) {
        return Triple(0.0, 0.0, 0.0)
    }
    return Triple(
        powersWatts.sum(),
        powersWatts.max(),
        powersWatts.average()
    )
}

fun printHeader(title: String) {
    println(title)
}

fun readPositiveDouble(prompt: String): Double {
    while (true) {
        print(prompt)
        val input = readLine()?.trim()?.toDoubleOrNull()
        if (input == null) {
            println("  Ошибка: введите число.")
            continue
        }
        if (!isValidElectricValue(input)) {
            println("  Ошибка: значение не может быть отрицательным.")
            continue
        }
        return input
    }
}

fun main() {
    printHeader("Расчёт энергопотребления (Вариант 9)")

    val voltage = 220.0
    val i1 = readPositiveDouble("Введите ток 1-го прибора (А): ")
    val i2 = readPositiveDouble("Введите ток 2-го прибора (А): ")
    val i3 = readPositiveDouble("Введите ток 3-го прибора (А): ")

    val p1 = calculatePowerWatts(voltage, i1)
    val p2 = calculatePowerWatts(voltage, i2)
    val p3 = calculatePowerWatts(voltage, i3)

    println()
    printHeader("Демонстрация форматирования")
    println("Прибор 1 (по умолчанию):  ${formatPower(p1)}")
    println("Прибор 2 (с категорией):  ${formatPower(p2, showCategory = true)}")
    println("Прибор 3 (в кВт, 3 зн.):  ${formatPower(p3, inKilowatts = true, digits = 3)}")
    println("Прибор 1 (кВт + катег.):  ${formatPower(p1, inKilowatts = true, digits = 2, showCategory = true)}")

    println()
    printHeader("Общая статистика по приборам (vararg)")
    val (totalPower, maxPower, avgPower) = powerStats(p1, p2, p3)
    println("Суммарная мощность:     ${formatPower(totalPower, inKilowatts = true)}")
    println("Самый мощный прибор:    ${formatPower(maxPower, showCategory = true)}")
    println("Средняя мощность:       ${formatPower(avgPower, digits = 1)}")

    val (emptyTotal, _, _) = powerStats()
    println("Проверка пустого вызова: ${formatPower(emptyTotal)}")

    println()
    printHeader("Расчёт стоимости работы")
    val hours = 5.0
    val tariff = 6.19
    val cost = calculateEnergyCost(totalPower, hours, tariff)
    println("За $hours ч работы при тарифе $tariff руб/кВт·ч: ${String.format(Locale.US, "%.2f", cost)} руб.")
}
