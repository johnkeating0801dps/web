# Student Web App — Maven WAR project

This is a Java Servlet/JSP web application for **Apache Tomcat 9** (javax.servlet / Servlet 4).

Eclipse:
1. File → Import → Maven → Existing Maven Projects.
2. Select this folder and finish.
3. Right-click project → Maven → Update Project.
4. Run As → Maven build… → Goals: `clean package`.
5. The WAR is `target/student-web.war`.
6. Add Tomcat 9 in Eclipse Servers, then add this project and start the server.

Open `http://localhost:8080/student-web/` and submit a name.
Requires JDK 17, Maven, and Tomcat 9. The servlet API is `provided` because Tomcat supplies it.



for docker web


FROM tomcat:9.0-jdk17-temurin

# Remove default Tomcat applications
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy WAR file into Tomcat
COPY target/*.war /usr/local/tomcat/webapps/ROOT.war

# Expose Tomcat port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]

