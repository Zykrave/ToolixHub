package com.zykrave.toolixhub.util

import com.zykrave.toolixhub.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

data class GitHubRelease(
    val tagName: String,
    val releaseName: String?,
    val htmlUrl: String?
)

object GitHubReleaseChecker {

    suspend fun getLatestRelease(): Result<GitHubRelease> = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            val url = URL("https://api.github.com/repos/Zykrave/ToolixHub/releases/latest")
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10_000
                readTimeout = 10_000
                setRequestProperty("Accept", "application/vnd.github+json")
                setRequestProperty("User-Agent", "ToolixHub/${BuildConfig.VERSION_NAME}")
            }

            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext Result.failure(IOException("HTTP error code: $responseCode"))
            }

            val jsonText = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(jsonText)

            if (!json.has("tag_name") || json.isNull("tag_name")) {
                return@withContext Result.failure(IllegalArgumentException("Missing tag_name in release response"))
            }

            val tagName = json.getString("tag_name")
            if (tagName.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("Empty tag_name in release response"))
            }

            val releaseName = if (json.has("name") && !json.isNull("name")) json.getString("name") else null
            val htmlUrl = if (json.has("html_url") && !json.isNull("html_url")) json.getString("html_url") else null

            Result.success(
                GitHubRelease(
                    tagName = tagName,
                    releaseName = releaseName,
                    htmlUrl = htmlUrl
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            connection?.disconnect()
        }
    }

    fun isNewerVersion(
        latestVersion: String,
        currentVersion: String
    ): Boolean {
        val latestParts = parseVersion(latestVersion) ?: return false
        val currentParts = parseVersion(currentVersion) ?: return false

        val maxLength = maxOf(latestParts.size, currentParts.size)
        for (i in 0 until maxLength) {
            val latestNum = latestParts.getOrElse(i) { 0 }
            val currentNum = currentParts.getOrElse(i) { 0 }
            if (latestNum > currentNum) return true
            if (latestNum < currentNum) return false
        }
        return false
    }

    private fun parseVersion(version: String): List<Int>? {
        val trimmed = version.trim()
        if (!trimmed.matches(Regex("^[vV]?\\d+(\\.\\d+)*$"))) return null
        val clean = trimmed.removePrefix("v").removePrefix("V")
        return clean.split(".").map { it.toIntOrNull() ?: return null }
    }
}
