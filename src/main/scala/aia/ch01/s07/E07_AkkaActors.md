# 1.7 Akka actors
### 1.7.1 ActorSystem
The first thing that every Akka application does is create an ActorSystem. The actor system can create so called top-level actors and it is a common pattern to create only one top level actor for all actors in the application.

We’ve touched on the fact that we will need supporting capabilities for actors, like remoting and a journal for durability. The ActorSystem is also the nexus for these supporting capabilities. Most capabilities are provided as so called Akka extensions, modules which can be configured specifically for the ActorSystem in question. A simple example of a supporting capability is the scheduler, which can send messages to actors periodically.

### 1.7.2 ActorRef, mailbox, and actor
Messages are sent to the actor’s . Every actor has a mailbox. ActorRef It is a lot like a queue. Messages sent to the ActorRef will be temporarily stored in the mailbox, to be processed later, one at a time, in the order they arrived.

### 1.7.3 Dispatchers
Actors are invoked at some point in time by a dispatcher. The dispatcher pushes the messages in the mailbox through the actors, so to speak.

So when you send a message to an actor, all you’re really doing is leaving a message behind in its mailbox. Eventually a dispatcher will push it through the actor.

Actors are lightweight because they run on top of dispatchers.

### 1.7.4 Actors and the network
