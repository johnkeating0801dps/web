# Runtime image for a Maven-built WAR using the javax.servlet API.
# Tomcat 9 is compatible with javax.servlet applications.
FROM tomcat:9.0-jdk17-temurin

# Remove Tomcat's default applications.
RUN rm -rf /usr/local/tomcat/webapps/*

# Build the WAR first with: mvn clean package
# Deploy it as the root application at http://localhost:8080/
COPY target/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

# Run Tomcat in the foreground so Docker can manage the process.
CMD ["catalina.sh", "run"]
