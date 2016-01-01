# 4.3 Supervision
In this section we’re going to look at the details of supervision. We’ll take the log processing example application and show you different types of supervision strategies.In this section we’ll focus on the supervisor hierarchy under the actor `/user` path, which will also be referred to as the user space.

### 4.3.1 Supervisor hierarchy
The supervision hierarchy is fixed for the lifetime of a child. Once the child is created by the parent, it will fall under the supervision of that parent as long as it lives; there’s no such thing as adoption in Akka.

The most dangerous actors (actors that are most likely to crash) should be as low down the hierarchy as possible.

Let’s look at the supervisor hierarchy of the log-processing application as weintended in the previous section.
```
FileFindersSupervisor
         |
    fileFinder
         |
logProcessorsSupervisor
         |
   logProcessor
         |
dbWritersSupervisor
         |
     dbWriter

```

### 4.3.2 Predefined strategies
The top-level actors in an application are created under the `/user` path and supervised by the user guardian. The default supervision strategy for the user guardian user guardian is to restart its children on any Exception except for when it receives internal exceptions which indicate that the actor was killed or when it failed during initialization, at which point it
will stop the actor in question. This strategy is known as the default strategy.

Every actor has a default supervisor strategy, which can be overridden by implementing the `supervisorStrategy` method. There are two predefined strategies available in the `SupervisorStrategy` object: the `defaultStrategy` and the `stoppingStrategy`.
