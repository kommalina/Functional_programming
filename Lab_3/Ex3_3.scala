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
    def apply(total: Int, finaly: ActorRef[Double]):Behavior[Double] = Behaviors.setup{ contex =>
        def counting(count: Int, sum: Double):Behavior[Double] =  Behaviors.receiveMessage{ message =>
            val newSum = sum + message
            if (count > 1) then
                counting(newSum, count-1)
            else 
                context.log.info(s"Final sum: $curSum")
                finaly ! curSum
                Behaviors.stopped
        counting(0.0, total)
    }
}

//ActorSystem
object integralSystem:
    case class Integrals(f: Double => Double, l: Double, r: Double, steps: Int, t: Int, replyTo: ActorRef[Double])
    
    def apply():Behavior[Integrals]= Behaviors.setup{ context =>
        val actors = Seq(context.spawn(ActorIntegral(), "Actor0"),
                        context.spawn(ActorIntegral(), "Actor1"), 
                        context.spawn(ActorIntegral(), "Actor2"),
                        context.spawn(ActorIntegral(), "Actor3"))
        val sum = context.spawn(ActorSum(4, context.self), "Sum")
        Behaviors.receiveMessage { message =>
            val stepSize = (message.r - message.l) / message.parts
            (0 until message.parts).foreach { i =>
            val left = message.l + i * stepSize
            val right = left + stepSize
            actors(i) ! ActorIntegral.Integral(message.f, left, right, message.steps / message.parts, sum)

            Behaviors.same
        }
    }
}

object ResultLogger:
    def apply(): Behavior[Double] = Behaviors.receive { (context, result) =>
        context.log.info(s"Result: $result")
        Behaviors.same
    }

@main def main():Unit = {
    val system = ActorSystem(integralSystem(), "integralSystem")
    val log = ActorSystem(ResultLogger(), "resultLogger")

    def f(x: Double): Double = math.pow(x, 2) * (math.log(x) / math.log(10))

    system ! integralSystem.Integrals(f, 1.4, 3.0, 100, 4, log)
}

