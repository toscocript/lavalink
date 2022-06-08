/*
 *  Copyright (c) 2021 Freya Arbjerg and contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 */

package lavalink.server.info

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import java.io.IOException
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Properties

/**
 * Created by napster on 25.06.18.
 * - Edited by davidffa on 07.05.21.
 * <p>
 * Provides access to the values of the property file generated by whatever git info plugin we're using
 * <p>
 * Requires a generated git.properties, which can be achieved with the gradle git plugin
 */
@Component
class GitRepoState {
  companion object {
    private val log = LoggerFactory.getLogger(GitRepoState::class.java)
  }

  final val branch: String
  private val commitId: String
  final val commitIdAbbrev: String
  private val commitUserName: String
  private val commitUserEmail: String
  private val commitMessageFull: String
  private val commitMessageShort: String
  final val commitTime: Long //epoch seconds
  final var loaded = false

  init {
    val properties = Properties()
    try {
      properties.load(GitRepoState::class.java.classLoader.getResourceAsStream("git.properties"))
      loaded = true
    } catch (e: NullPointerException) {
      log.trace("Failed to load git repo information. Did you build with the git gradle plugin? Is the git.properties file present?")
    } catch (e: IOException) {
      log.info("Failed to load git repo information due to suspicious IOException", e)
    }

    branch = properties.getOrDefault("git.branch", "").toString()
    commitId = properties.getOrDefault("git.commit.id", "").toString()
    commitIdAbbrev = properties.getOrDefault("git.commit.id.abbrev", "").toString()
    commitUserName = properties.getOrDefault("git.commit.user.name", "").toString()
    commitUserEmail = properties.getOrDefault("git.commit.user.email", "").toString()
    commitMessageFull = properties.getOrDefault("git.commit.message.full", "").toString()
    commitMessageShort = properties.getOrDefault("git.commit.message.short", "").toString()
    val time = properties["git.commit.time"].toString()
    commitTime = if (time == "null") {
      0
    } else {
      // https://github.com/n0mer/gradle-git-properties/issues/71
      val dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")
      OffsetDateTime.from(dtf.parse(time)).toEpochSecond()
    }
  }
}
