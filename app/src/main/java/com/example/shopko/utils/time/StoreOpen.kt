import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun isStoreOpenNow(workingHours: String): Boolean {
    val formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    val now = LocalTime.now()

    val parts = workingHours.replace("h", "").split("–").map { it.trim() }
    if (parts.size != 2) return false

    return try {
        val start = LocalTime.parse(parts[0], formatter)
        val end = LocalTime.parse(parts[1], formatter)

        if (end.isAfter(start)) {
            now.isAfter(start) && now.isBefore(end)
        } else {

            now.isAfter(start) || now.isBefore(end)
        }
    } catch (e: Exception) {
        false
    }
}
