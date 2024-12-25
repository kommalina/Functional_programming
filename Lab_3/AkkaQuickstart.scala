//#full-example
package com.example


import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.example.GreeterMain.SayHello

//#greeter-actor
object Greeter {
  final case class Greet(whom: String, replyTo: ActorRef[Greeted])    //Сообщение, содержащее значение типа string и ссфлку на актор, который получает ответ
  final case class Greeted(whom: String, from: ActorRef[Greet])       //Сообщение, содержащее значение типа string и ссылку на актор, который отправил сообщение 

  //Описание поведения актора при приеме сообщения
  def apply(): Behavior[Greet] = Behaviors.receive { (context, message) =>
    context.log.info("Hello {}!", message.whom)   //Логирование
    //#greeter-send-messages
    message.replyTo ! Greeted(message.whom, context.self)   //Отправка сообщения отправителю 
    //#greeter-send-messages
    Behaviors.same    //Поведение после обработки сообщения не меняется
  }
}
//#greeter-actor

//#greeter-bot
object GreeterBot {

  //Описание поведения актора
  def apply(max: Int): Behavior[Greeter.Greeted] = {
    bot(0, max)
  }

  //Рекурсивная обработка сообщений
  private def bot(greetingCounter: Int, max: Int): Behavior[Greeter.Greeted] =
    Behaviors.receive { (context, message) =>
      val n = greetingCounter + 1
      context.log.info("Greeting {} for {}", n, message.whom) //Логирование 
      if (n == max) {                                         //Если счетчик достиг max 
        Behaviors.stopped                                     //ТО оастановить актор
      } else {                                                
        message.from ! Greeter.Greet(message.whom, context.self)  //Иначе отправить сообщения Greeter
        bot(n, max)     //Вызов с обновленным счетчиком 
      }
    }
}
//#greeter-bot

//#greeter-main
object GreeterMain {

  final case class SayHello(name: String)   //Сообщение, содержащее имя 

  //Описание поведения актора 
  def apply(): Behavior[SayHello] =
    Behaviors.setup { context =>
      //#create-actors
      val greeter = context.spawn(Greeter(), "greeter")   //Создание актора Greeter 
      //#create-actors

      Behaviors.receiveMessage { message =>               //Обработка сообщения 
        //#create-actors
        val replyTo = context.spawn(GreeterBot(max = 3), message.name)  //Создание актора GreeterBot
        //#create-actors
        greeter ! Greeter.Greet(message.name, replyTo)    //Отправка сообщения Greeter 
        Behaviors.same                                    //Поведение после обработки сообщения не меняется
      }
    }
}
//#greeter-main

//#main-class
object AkkaQuickstart extends App {
  //#actor-system
  val greeterMain: ActorSystem[GreeterMain.SayHello] = ActorSystem(GreeterMain(), "AkkaQuickStart") //Создание сущности ActorSystem
  //#actor-system

  //#main-send-messages
  greeterMain ! SayHello("Charles")   //Отправка сообщения 
  //#main-send-messages
}
//#main-class
//#full-example