# 4.2 Actor lifecycle
We’ve seen that an actor can restart to recover from a failure. But how can we correct the actor state when the actor is restarting? To answer that question, we need to take a closer look at the actor lifecycle. An actor is automatically started by Akka when it is created. The actor will stay in the `Started` state until it’s stopped. From that moment the actor is in the `Terminated` state. When the actor is terminated, it can’t process messages anymore and will be eventually garbage collected. When the actor is in a `Started` state, it can be restarted to reset the internal state of the actor. As we discussed in the previous section, the actor instance is replaced by a fresh actor instance. The restart can happen as many times as necessary.

During the lifecycle of an actor, there are three types of events:
1. The actor is created and started—for simplicity we’ll refer to this as the start event.
2. The actor is restarted on the restart event.
3. The actor is stopped by the stop event.

There are several hooks in place in the `Actor` trait which are called when the events
happen to indicate a lifecycle change. The order in which the hooks occur is guaranteed, although they’re called asynchronously by Akka.

### 4.2.1 Start event
An actor is created and automatically started with the `actorOf` method. Top-level actors are created with the `actorOf` method on the `ActorSystem`. A parent actor creates a child actor using the `actorOf` on its `ActorContext`.

After the instance is created, the actor is going to be started by Akka. The `preStart` hook is called just before the actor is started. To use this trigger, we have to override the `preStart` method.
```
override def preStart() {
  println("preStart")
}
```

This hook can be used to set the initial state of the actor. This should not be done in the constructor, because it isn't guarantied that the complete actor environment is initialized already when executing your constructor code.

### 4.2.2 Stop event
The stop event indicates the end of the actor lifecycle and occurs once, when an actor is stopped. An actor can be stopped using the method `stop` on the `ActorSystem` and `ActorContext` objects, or by sending a `PoisonPill` message to an actor.

The `postStop` hook is called just before the actor is terminated. When the actor is in the `Terminated` state, the actor doesn’t get any new messages to handle. The `postStop` method is the counterpart of the `preStart` hook.
```
override def postStop() {
  println("postStop")
}
```
Normally this hook implements the opposite function of the `preStart`, and releases resources created in the `preStart` method and possibly stores the last state of the actor somewhere outside of the actor in the case that the next actor instance needs it. A stopped actor is disconnected from its `ActorRef`. After the actor is stopped, the ActorRef is redirected to the `deadLetters` `ActorRef` of the actor system, which is a special `ActorRef` that receives all messages that are sent to dead actors.

### 4.2.3 Restart event
When a restart occurs, the method of the crashed `preRestart` actor instance is called. In this hook, the crashed actor instance is able to store its current state, just before it’s replaced by the new actor instance.
```
override def preRestart(reason: Throwable,
                        message: Option[Any]) {
  println("preRestart")
  super.preRestart(reason, message)
}
```

Be careful when overriding this hook. The default implementation of the preRestart method stops all the child actors of the actor and then calls the `postStop` `super.preRestart` default behavior won’t occur.

It’s important to note that a restart doesn’t stop the crashed actor in the same way as the stop methods (described earlier when discussing the `stop` event). As we’ll see later, it’s possible to monitor the death of an actor. A crashed actor instance in a restart doesn’t cause a `Terminated` message to be sent for the crashed actor. The fresh actor instance, during restart, is connected to the same `ActorRef` the crashed actor was using before the fault. A stopped actor is disconnected from its `ActorRef` and redirected to the `deadLetters` `ActorRef` as described by the stop event. What both the stopped actor and the crashed actor have in common is that by default, the `postStop` is called after they’ve been cut off from the actor system.

The `preRestart` method takes two arguments: the reason for the restart and optionally the message that was being processed when the actor crashed. The supervisor can decide what should (or can) be stored to enable state restoration as part of restarting. And of course this can’t be done using local variables, because after restarting, a fresh actor instance will take over processing. One solution for keeping state beyond the death of the crashed actor is for the supervisor to send a message to the actor (will go in its mailbox). (This is done by sending a message to its own `ActorRef`, which is available on the actor instance through the `self` value.) Other options are writing to something outside of the actor, like a database or the file system. This all depends completely on your system and the behavior of the actor.

After the `preStart` hook is called, a new instance of the actor class is created and therefore the constructor of the actor is executed, through the `Props` object. After that, the `postRestart` hook is called on this fresh actor instance.

```
override def postRestart(reason: Throwable) {
  println("postRestart")
  super.postRestart(reason)
}
```
Here too, we start with a warning. The super implementation of the `postRestart` is called because this will trigger the function `preStart` by default. The `super.postRestart` can be omitted if you’re certain that you don’t want the `preStart` to be called when restarting; in most cases though this isn’t going to be the case. The `preStart` and `postStop` are called by default during a restart and they’re called during the start and stop events in the lifecycle, so it makes sense to add code there for initialization and cleanup respectively, killing two birds with one stone. The argument `reason` is the same as received in the `preRestart` method. In the overridden hook, the actor is free to restore itself to some last known correct state, for example by using information stored by the `preRestart` function.

### 4.2.4 Putting the lifecycle pieces together
See LifeCycleHooks.scala and LifeCycleHooksTest.scala

### 4.2.5 Monitoring the lifecycle
The lifecycle of an actor can be monitored. The lifecycle ends when the actor is terminated. An actor is terminated if the supervisor decides to stop the actor, if the `stop` method is used to stop the actor, or if a message is sent `PoisonPill` to the actor which indirectly causes the `stop` method to be called. Since the default implementation of the `preRestart` method stops all the actor’s children with the `stop` methods, these children are also terminated in the case of a restart. The crashed actor instance in a restart is not terminated in this sense. It’s removed from the actor system, but not by using the `stop` method, directly or indirectly. This is because the `ActorRef` will continue to live on after the restart: the actor instance has not been terminated but replaced by a new one. The `ActorContext` provides a `watch` method to monitor the death of an actor and an `unwatch` to de-register as monitor. Once an actor calls the watch method on an actor reference, it becomes the monitor of that actor reference. A `Terminated` message is sent to the monitor actor when the monitored actor is terminated. The `Terminated` message only contains the `ActorRef` of the  actor that died. The fact that the crashed actor instance in a restart is not terminated in the same way as when an actor is stopped now makes sense, because otherwise you’d receive many terminated messages whenever an actor restarts, which would make it impossible to differentiate the final death of an actor from a temporary restart.

See WatchDog.scala, Master.scala, WatchDogTest.scala
