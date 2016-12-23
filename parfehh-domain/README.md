parfehh-domain
================
A set of classes describing a typical domain for a suite of integration tests.

1. A TestSeries consists of many TestCases
2. A TestCase consists of
   * some PreConditions
   * some TestSteps consisting of a list of Actions on its part
   * some Efects
   * some PostConditions
   
All of these objects have an id and a unique name. In parfehh-generator the id 
is needed to identify the protected regions within the generated code an should not 
 change during the project.
 The unique name is used in parfehh-generator to give the object an indentifier for
 generated methods which must in deed be unique within a class. The difference to 
 the id is that the name can be changed during the project. Typically, the name 
 describing a post condition for example results from a an entry in a test tool 
 or an Excel sheet and should be allowed to be changed of course.  
   

