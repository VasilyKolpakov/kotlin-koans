package iii_conventions

import java.sql.Time
import java.util.*
import kotlin.comparisons.compareValuesBy

data class MyDate(val year: Int, val month: Int, val dayOfMonth: Int) : Comparable<MyDate> {
    override fun compareTo(other: MyDate): Int {
        return compareValuesBy(this, other, { it.year }, { it.month }, { it.dayOfMonth })
    }
}

operator fun MyDate.rangeTo(other: MyDate): DateRange = DateRange(this, other)

operator fun MyDate.plus(other: TimeInterval): MyDate = when (other) {
    TimeInterval.DAY -> this.nextDay()
    TimeInterval.WEEK -> functionalPower(this, 7) { it.nextDay() }
    TimeInterval.YEAR -> this.copy(year = year + 1)
}

operator fun MyDate.plus(other: ScaledTimeInterval): MyDate = functionalPower(this, other.scale) {
    it + other.timeInterval
}

operator fun TimeInterval.times(other: Int) = ScaledTimeInterval(this, other)

class ScaledTimeInterval(val timeInterval: TimeInterval, val scale: Int)

enum class TimeInterval {
    DAY,
    WEEK,
    YEAR
}

class DateRange(override val start: MyDate, override val endInclusive: MyDate) : ClosedRange<MyDate>, Iterable<MyDate> {
    override fun iterator(): Iterator<MyDate> {
        return object : Iterator<MyDate> {
            var current = start
            override fun hasNext(): Boolean = current <= endInclusive

            override fun next(): MyDate {
                if (!hasNext()) {
                    throw NoSuchElementException()
                } else {
                    val ret = current
                    current = current.nextDay()
                    return ret
                }
            }
        }
    }
}

fun <T> functionalPower(init: T, power: Int, f: (T) -> T): T = (1..power).fold(init) { acc, x -> f(acc) }
