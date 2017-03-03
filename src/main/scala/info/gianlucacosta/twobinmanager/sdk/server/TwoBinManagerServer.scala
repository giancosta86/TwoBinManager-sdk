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

package info.gianlucacosta.twobinmanager.sdk.server

import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.{ActorNotFound, ActorRef, ActorSystem}
import akka.pattern.{AskTimeoutException, Patterns}
import akka.util.Timeout
import info.gianlucacosta.twobinmanager.sdk.server.messages.problems.{ProblemBundleRequest, ProblemBundleResult}
import info.gianlucacosta.twobinmanager.sdk.server.messages.solutions.{SolutionsUploadRequest, SolutionsUploadResult}
import info.gianlucacosta.twobinmanager.sdk.server.messages.{TwoBinManagerRequest, TwoBinManagerResult}
import info.gianlucacosta.twobinpack.core.{ProblemBundle, Solution}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


object TwoBinManagerServer {

  /**
    * Connection parameters to TwoBinManager's server
    */
  case class ConnectionParams(
                               address: String,
                               port: Int,
                               timeout: Timeout = Timeout(20, TimeUnit.SECONDS)
                             )

  /**
    * Default connection parameters to TwoBinManager's service
    */
  val defaultConnectionParams =
    ConnectionParams(
      "127.0.0.1",
      9001
    )
}


/**
  * Lightweight synchronous API wrapping actual calls
  * to TwoBinManager's server
  *
  * @param localActorSystem The local actor system, used to perform remote calls
  * @param connectionParams The connection parameters to TwoBinManager
  */
class TwoBinManagerServer(
                           localActorSystem: ActorSystem,
                           connectionParams: TwoBinManagerServer.ConnectionParams
                         ) {

  /**
    * Asks TwoBinManager's server for the problem bundle it is currently publishing
    *
    * @return The problem bundle. Any error throws an exception
    */
  def requestProblemBundle(): ProblemBundle = {
    val request =
      ProblemBundleRequest(UUID.randomUUID())

    val resultMessage =
      askForResultMessage[ProblemBundleRequest, ProblemBundleResult](
        problemBundleActorName,
        request
      )

    resultMessage.problemBundle
  }


  /**
    * Uploads solutions to TwoBinManager's server
    *
    * @param solutions The solutions to upload
    */
  def uploadSolutions(solutions: Iterable[Solution]): Unit = {
    val request =
      new SolutionsUploadRequest(
        UUID.randomUUID(),
        solutions
      )

    val resultMessage =
      askForResultMessage[SolutionsUploadRequest, SolutionsUploadResult](
        solutionsUploadActorName,
        request
      )

    resultMessage.errorMessage.foreach(errorMessage =>
      throw new RuntimeException(errorMessage)
    )
  }


  private def askForResultMessage[TRequest <: TwoBinManagerRequest, TResult <: TwoBinManagerResult](
                                                                                                     actorName: String,
                                                                                                     request: TRequest): TResult = {

    val result =
      try {
        val actor =
          getActor(actorName)

        val resultFuture =
          Patterns.ask(
            actor,
            request,
            connectionParams.timeout
          ).asInstanceOf[Future[TResult]]


        Await.result(
          resultFuture,
          Duration.Inf
        )
      } catch {
        case _: ActorNotFound =>
          throw new RuntimeException("Cannot connect to the server. Are the connection parameters correct?")
        case _: AskTimeoutException =>
          throw new RuntimeException("The connection to the server timed out. Please, try again later.")
      }


    if (result.requestMessageId == request.messageId)
      result
    else
      askForResultMessage(
        actorName,
        request
      )
  }


  private val remoteActorPathTemplate =
    "akka.tcp://TwoBinManager@%s:%d/user/%s"


  private val problemBundleActorName =
    "ProblemBundleActor"


  private val solutionsUploadActorName =
    "SolutionsUploadActor"


  private def getActor(actorName: String): ActorRef = {
    val remoteActorPath =
      remoteActorPathTemplate.format(
        connectionParams.address,
        connectionParams.port,
        actorName
      )


    implicit val resolutionTimeout =
      Timeout(1, TimeUnit.MINUTES)


    Await.result(
      localActorSystem.actorSelection(remoteActorPath).resolveOne(),
      Duration.Inf
    )
  }
}
