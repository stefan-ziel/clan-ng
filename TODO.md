##3rd Phase (Integration with other Frameworks, i.e. clan/activity/spring/etc)

- ~~Tests for DroolsSend.~~
- ~~Use BR------ for rule names in drl files~~
- ~~use raise(int) instead of drools throws~~
- ~~Create pvclan module with domain and drools rules~~
- ~~Join bo-logic and domain modules~~
- ~~Put MetaData inside DroolsConnection instead of dispatch not implemented~~
- ~~Implement session factory bean~~
- ~~Modularize projects (OSGi/ServiceLoader)~~
- ~~Check drools environment (guvnor)~~
- ~~jpa generic view select~~
- ~~Code module refactoring~~
- Implement separate drools interactions: Decompose and compose phases

##2nd Phase (Annotation conversion module):
###TODO
- ~~DTO Annotation description~~
  * ~~@Label~~
  * ~~@Table Destiny~~
  * ~~@Validations (Using hibernate validator)~~
  * ~~@MapToClass~~

- ~~Convert HistDto object to aspectj~~
- ~~Unify helper package~~
- ~~Divide clan-ng in multiple modules (domain, bo-logic, web, where-parser, ???)~~
- ~~Move Mapping logic to drools~~
- ~~Implement validator~~
- ~~Exception handling on XML and XSD problems~~
- ~~Implement Exception Handling, Exception Throwing on drools files.~~
- ~~Validations for Minimum attributes present.~~
- ~~Create separate module for Business Logic~~ 
- ~~Reimplement rerouter for direct calls to NormalCommunicator~~
- Explicit Transaction support (Protocol changes)
  * Attribute on Tx Tag (Commit, rollback, startTransaction, ???)

##1st Phase (Prototype Skeleton)
- ~~Install Drools~~
- ~~Install Spring~~