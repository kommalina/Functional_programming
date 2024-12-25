package com.example

import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

//Актор, вычисляющий интеграл
object ActorIntegral:
    case class Integral(f: Double => Double, l: Double, r: Double, steps: Int, replyTo: ActorRef[Double])

    def apply():Behavior[Integral] = Behaviors.receive{ (context, message) => 
        val h = (message.r-message.l)/message.steps
        val sum = (1 to message.steps).map(i => h*message.f(message.l+i*h)).sum
        val integral = h * sum 
        context.log.info(s"Result: $integral")

        message.replyTo ! sum
        Behaviors.same
    }

// Актор, суммирующий результаты
object ActorSum:
    def apply(total: Int, replyTo: ActorRef[Double]):Behavior[Double] = Behaviors.setup{ context =>
        def counting(sum: Double, count: Int):Behavior[Double] =  Behaviors.receiveMessage{ message =>
            if (count > 0) then
                counting(sum + message, count-1)
            else {
                replyTo ! sum + message
                Behaviors.stopped
            }         
        }  
        counting(0.0, total)
    }

//ActorSystem
object integralSystem:
    case class Integrals(f: Double => Double, l: Double, r: Double, steps: Int, tasks: Int,replyTo: ActorRef[Double])
    
    def apply():Behavior[Integrals]= Behaviors.setup{ context =>
        val integralActors = (0 until 4).map(i => context.spawn(ActorIntegral(), s"integralActor$i")).toSeq
        val numActors = integralActors.length
        
        Behaviors.receiveMessage {  
            case Integrals (f, l, r, steps, tasks, replyTo) =>
                val sumActor = context.spawn(ActorSum(tasks, replyTo), "SumActor")
                val stepSize = (r - l)/tasks
                
                (0 until tasks).foreach { i =>
                    val left = l + i * stepSize
                    val right = left + stepSize
                    integralActors(i % integralActors.length) ! ActorIntegral.Integral(f, left, right, steps/tasks, sumActor)
                }
            Behaviors.same
        }
    }

//Логирование 
object ResultLogger:
    def apply(): Behavior[Double] = Behaviors.receive { (context, result) =>
        context.log.info(s"Result: $result")
        Behaviors.same
    }

//Функция
def f(x: Double): Double = math.pow(x, 2) * (math.log(x) / math.log(10))

@main def main():Unit = {
    val system = ActorSystem(integralSystem(), "integralSystem")
    val resLog = ActorSystem(ResultLogger(), "resultLogger")

    system ! integralSystem.Integrals(f, 1.0, 3.0, 100, 10, resLog)
}


