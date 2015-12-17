# 3.2 One-way messages
There are a three variations that we’ll look at:
1. __SlilentActor__ -- An actor’s behavior is not directly observable from the outside
2. __SendingActor__ -- An actor sends a message to another actor (or possibly many actors) after it’s done processing the received message.
3. __SideEffectingActor__ -- An actor receives a message and interacts with a normal object in some kind of way.

### 3.2.1 SilentActor examples
See SilentActor.scala and SilentActorTest.scala.

### 3.2.2 SendingActor example
See SendingActor.scala and SendingActorTest.scala.
