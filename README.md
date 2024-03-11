CS157A final project

Shun Furuya

Jun Jie Lai

Chih-kuo Hsu


—-------------------------------------------------------------------------------------
Eclipse web dynamic project 


Frontend: html,css, javascript
Backend: java
Server: tomcat 8.5
Jar files: mysql-connector-j-8.1.0.jar,PUB.jar

Server  setting:

1. Download the tomcat 8.5 server.

2. Select Preferences Navigate to Server > Runtime Environments
Click Add and point it to the directory where you installed/unzipped Tomcat.

3. Linking Tomcat and the Dynamic Web Project
Choose the Tomcat version you set up and link it to your dynamic web project




Jar files setting:

1.Add mysql-connector-j-8.1.0.jar and PUB.jar to the project classpath

2.In order to run this project, the PUB.jar setup is required ,

The "PUB JAR file" integrates elements like jdbc connection, resultSet, preparedStatement, etc., making it simpler to perform database operations such as addition, querying, or deletion.

1. Check the server.xml file. Setup the global resources in the server.xml file. 


Example:


<Resource driverClassName="com.mysql.cj.jdbc.Driver" maxIdle="60" maxTotal="50" maxWaitMillis="-1" name="SINBON_POR" password="Aa25912663" type="javax.sql.DataSource" url="jdbc:mysql://localhost:3306/CS157A" username="root"/>


2.add context in the <Host></Host>.


Example:


<Host appBase="webapps" autoDeploy="true" name="localhost" unpackWARs="true">

<!-- SingleSignOn valve, share authentication between web applications
     Documentation at: /docs/config/valve.html -->
<!--
<Valve className="org.apache.catalina.authenticator.SingleSignOn" />
-->

<!-- Access log processes all example.
     Documentation at: /docs/config/valve.html
     Note: The pattern used is equivalent to using pattern="common" -->
<Valve className="org.apache.catalina.valves.AccessLogValve" directory="logs" pattern="%h %l %u %t &quot;%r&quot; %s %b" prefix="localhost_access_log" suffix=".txt"/>

  <Context crossContext="true" docBase="cs157A_Final_Project" path="/cs157A_Final_Project" reloadable="true" source="org.eclipse.jst.jee.server:cs157A_Final_Project">
  <ResourceLink global="SINBON_POR" name="qb" type="javax.sql.DataSource"/>
</Context>


</Host>









Then you can use QueryBean object in this project. 

Example:

 
QueryBean b= null;
ArrayList<ArrayList<Object>> a = = new ArrayList<>();
boolean executeSuccess=false;
String querySql="select * from product";


try {
			 b = new QueryBean("qb", false, "utf-8", "utf-8"); //QueryBean object

			a = b.querySQL(search);


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
	    	if (b != null) {
	    		b.setAutoCommitMode(false);
				try {
					b.close();
				} catch (Exception e) {

					e.printStackTrace();
				}
			}
			b = null;
		}




—-------------------------------------------------------------------------------------
Division of Work:

Shun Furuya:
 Main responsibilities: Database design, Front-end design 

Secondary responsibilities: Setting up presentation slides, Final project report preparation 

Jun Jie Lai: 
Main responsibilities: Database design 

Secondary responsibilities: Front-end design 

Chih-kuo Hsu: Main responsibilities: Back-end design, Database testing 

Secondary responsibilities: Front-end design, Writing the readme.txt for the setup process
