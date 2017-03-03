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

package info.gianlucacosta.twobinmanager.sdk.server.messages.solutions

import java.io.StringWriter
import java.util.UUID

import info.gianlucacosta.twobinmanager.sdk.server.messages.TwoBinManagerRequest
import info.gianlucacosta.twobinpack.core.Solution
import info.gianlucacosta.twobinpack.io.csv.v2.SolutionCsvWriter2

/**
  * Message uploading solutions to the server
  *
  * @param messageId    The message unique identifier
  * @param solutionsCsv The CSV string (in the format created by TwoBinKernel) of the solution
  */
case class SolutionsUploadRequest(
                                   messageId: UUID,
                                   solutionsCsv: String
                                 ) extends TwoBinManagerRequest {
  def this(messageId: UUID, solutions: Iterable[Solution]) =
    this(
      messageId,
      solutionsCsv = {
        val stringWriter =
          new StringWriter

        val solutionsCsvWriter =
          new SolutionCsvWriter2(
            stringWriter
          )

        try {
          solutions.foreach(solutionsCsvWriter.writeSolution)
        } finally {
          solutionsCsvWriter.close()
        }

        stringWriter.toString
      }
    )
}