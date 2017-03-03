/*^
  ===========================================================================
  TwoBinManager - SDK
  ===========================================================================
  Copyright (C) 2017 Gianluca Costa
  ===========================================================================
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as
  published by the Free Software Foundation, either version 3 of the
  License, or (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public
  License along with this program.  If not, see
  <http://www.gnu.org/licenses/gpl-3.0.html>.
  ===========================================================================
*/

package info.gianlucacosta.twobinmanager.sdk.server.messages.problems

import java.io.{BufferedReader, StringReader}
import java.util.UUID

import info.gianlucacosta.twobinmanager.sdk.server.messages.TwoBinManagerResult
import info.gianlucacosta.twobinpack.core.ProblemBundle
import info.gianlucacosta.twobinpack.io.bundle.ProblemBundleReader

/**
  * Message returning an XML-serialized problem bundle
  *
  * @param requestMessageId The unique identifier of the request message
  * @param problemBundleXml The XML-serialized problem bundle
  */
case class ProblemBundleResult(
                                requestMessageId: UUID,
                                problemBundleXml: String
                              ) extends TwoBinManagerResult {
  /**
    * The problem bundle described by this message
    */
  lazy val problemBundle: ProblemBundle = {
    val problemBundleReader =
      new ProblemBundleReader(
        new BufferedReader(
          new StringReader(
            problemBundleXml
          )
        )
      )


    try {
      problemBundleReader.readProblemBundle()
    } finally {
      problemBundleReader.close()
    }
  }
}
