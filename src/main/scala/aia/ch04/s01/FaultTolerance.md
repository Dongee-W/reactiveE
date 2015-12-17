# 4.1 What fault tolerance is (and what it isn’t)

### 4.1.1 Plain old objects and exceptions
### 4.1.2 Let it crash
We already discussed why we don’t want to just graft recovery code into the operational flow, so catching the exception inside an actor where the business logic resides is not an option.

Instead of using one flow to handle both normal code and recovery code, Akka provides two separate flows: one for normal logic and one for fault recovery logic. The normal flow consists of actors that handle normal messages; the recovery flow consists of actors that monitor the actors in the normal flow. Actors that monitor other actors are called supervisors.

Instead of catching exceptions in an actor, we’ll just let the actor crash. The actor code only contains normal processing logic and no error handling or fault recovery logic, so it’s effectively not part of the recovery process, which keeps things much clearer.

Akka has chosen to enforce , meaning that any actor parental supervision that creates actors automatically becomes the supervisor of those actors. A supervisor doesn’t “catch exceptions,” rather it decides what should happen with the crashed actors that it supervises based on the cause of the crash.

supervisor has four options when deciding what to do with the actor:
1. __Restart__: The actor must be recreated from its Props. After it is restarted (or rebooted, if you will), the actor will continue to process messages. Since the rest of the application uses an ActorRef to communicate with the actor, the new actor instance will automatically get the next messages.
2. __Resume__: The same actor instance should continue to process messages; the crash is ignored.
3. __Stop__: The actor must be terminated. It will no longer take part in processing messages.
4. __Escalate__: The supervisor doesn’t know what to do with it and escalates the problem to its parent, which is also a supervisor.
