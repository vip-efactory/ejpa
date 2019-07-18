# ejpa-spring-boot-starter implement jar
This is a SpringDataJPA-based project. From Controller-->Service-->Repository-->Entity, there is a corresponding template code that provides the following functions:
- CRUD
- Paging and sorting -- the default implementation of JPA
- Multi-field complex conditional query

# About the primary key
- Currently the primary key type for writing dead is Long and is self-growth, and other types are temporarily not supported;

# Plan
- Remove the primary key definition from BaseEntity to move to the definition in the subclass, so the user configurability is larger;
- In the template, because there is no key attribute of id, in BaseController, you can consider using reflection to get;

# Thinking
- Considering whether to use Spring DataJPA's own response entity ResponseEntity, personally think that there is no simple R now simple

# Attention
- Please do not use the code of the master branch directly. It may be very unstable in development. You can use the released branch!
