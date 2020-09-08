# ejpa-spring-boot-starter Implement jar
This is a project based on SpringDataJPA. From Controller-> Service-> Repository-> Entity, there are corresponding template codes. The functions provided are:
- CRUD
- Pagination and sorting-the default implementation of JPA
- Multi-field complex condition query
- Internationalization--i18n
- Implement front-end constraint checking on entity attributes


# Specific use cases
- https://github.com/vip-efactory/ejpa-example/blob/master/README.md

# V4.0.0 Plan
- Bless redis cache configuration;
- Blessing records of requests;
- Increased handling of the underlying database exception;
- Desensitization of sensitive information

# Attention
- Please do not use the code of the master branch directly, it may be very unstable in development, you can use the released branch!


# V3.0.0 Upgrade content：
- Advanced query supports more complex query methods
- Achieve basic internationalization information
- Implement front-end constraint checking on entity attributes

# V2.0.0 Upgrade content：
- Upgraded the dependent version to the latest version of 2019-12-9;
- Optimized the response class R of the request;
- The implementation of the primary key is not defined in BaseEntity, and is transplanted into subclasses for greater flexibility;
- Fixed potential bugs in version comparisons in tool classes.


# Regarding advanced search, 3.0 supports advanced search in the following scenarios:
- Single condition:
    For example: A = 3, A> = 3, A is null, etc.
   
- Multiple conditions:
    -- All defaults or relationships:A=4 || B=7 || C > 8
    -- All conditions and relationships:A=4 && B=7 && C > 8
    -- And Or mixed relationship:
        A=4 && B=7 || C > 8
    -- Semi-hybrid relationship:
        A=4 && B =7 && (C =9 || C =11)
        A=4 || B =7 || (C =9 && D =11)
    -- Full hybrid relationship:
        (A = 4 || B = 7) && (C =9 || C =11 )
        (A = 4 && B = 7) || (C =9 && D =11 )
