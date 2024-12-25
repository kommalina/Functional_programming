package com.example

import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import scala.util.Random

//Актор, суммирующий значения
object ActorServer:
    type Addable = Int | Double         //Определим тип суммируемого значения как Int или Double
    case class AddMessage(a:Addable, b:Addable, replyTo:ActorRef[Addable]) 
    def apply():Behavior[AddMessage] = Behaviors.receive{
        (context, message)=>
            //Pattern-matching для разных случаев и отправка сообщения назад
            message.replyTo ! {(message.a, message.b) match
                case (a:Int, b:Int) => a + b
                case (a:Double, b:Double) => a + b
                case (a: Int, b: Double) => a + b
                case (a: Double, b: Int) => a + b
            }
        Behaviors.same //Поведение после обработки сообщения не меняется
    }

object ActorClient:
    def apply(server: ActorRef[ActorServer.AddMessage]):Behavior[ActorServer.Addable] = Behaviors.setup{ context =>
        def generationAndSending():Unit = {
            val a = Random.between(0,100)
            val b = Random.between(0,100)
            server ! ActorServer.AddMessage(a, b, context.self)
        }
        generationAndSending()
        Behaviors.receiveMessage{message => 
            context.log.info(s"Result $message") 
            generationAndSending()
            Behaviors.same 
        }
    }

//ActorSystem
object AddingSystem:
    //Поведение при инициализации ActorSystem
    def apply():Behavior[ActorServer.Addable] = Behaviors.setup{ (context)=>
        val server = context.spawn(ActorServer(), "server")     //Создание актора server 
        val client = context.spawn(ActorClient(server), "client")   //Создание актора client
        Behaviors.empty
    }
@main def AddingMain():Unit =
    val system = ActorSystem(AddingSystem(),"system")
