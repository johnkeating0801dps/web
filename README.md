# Maven Web Application – Docker Deployment

This repository contains a Maven-based Java web application packaged as a WAR and deployed on Apache Tomcat using Docker.

The Docker examples use **Tomcat 9 + Java 17**, suitable for applications using the **`javax.servlet`** API. If your project uses `jakarta.servlet`, use a compatible Tomcat 10.1+ image and update the Dockerfiles accordingly.

## Project files

```text
project-root/
├── pom.xml
├── src/
├── target/                  # Created by Maven; contains the WAR
├── Dockerfile               # Uses a WAR built on your machine
├── Dockerfile.multistage    # Builds the WAR inside Docker
├── .dockerignore
├── docker-compose.yml
└── README.md
```

> Run all commands below from the directory containing `pom.xml` and the Dockerfile.

---

## 1. Prerequisites

Install and start Docker.

Check the installations:

```bash
java -version
mvn -version
docker --version
docker compose version
```

On Arch Linux, Docker can be installed and started with:

```bash
sudo pacman -S docker docker-compose
sudo systemctl enable --now docker
```

Optional: allow your user to run Docker without `sudo`:

```bash
sudo usermod -aG docker "$USER"
```

Log out and log back in after adding the group, then verify:

```bash
docker run hello-world
```

If you do not configure group access, prefix Docker commands with `sudo`.

---

## 2. Build the WAR using Maven (single-stage Dockerfile)

### Step 1 – Clean the previous build

```bash
mvn clean
```

### Step 2 – Compile, test, and package the application

```bash
mvn package
```

Or run both phases in one command:

```bash
mvn clean package
```

Maven creates the WAR under `target/`. Confirm the file:

```bash
ls -lh target/*.war
```

If this command reports that no WAR exists, check that `pom.xml` contains:

```xml
<packaging>war</packaging>
```

Also verify that the project is a web application and that the Maven build completed successfully.

### Step 3 – Build the Docker image

```bash
docker build -t maven-webapp:latest .
```

What the command means:

- `docker build`: builds an image from a Dockerfile.
- `-t maven-webapp:latest`: assigns an image name and tag.
- `.`: sends the current directory as the build context.

### Step 4 – Verify the image

```bash
docker images
```

Look for `maven-webapp` in the repository column.

### Step 5 – Run a container

```bash
docker run -d --name maven-webapp-container -p 8080:8080 maven-webapp:latest
```

Explanation:

- `-d`: runs the container in the background.
- `--name`: assigns a container name.
- `-p 8080:8080`: maps host port 8080 to container port 8080.
- `maven-webapp:latest`: image used to create the container.

### Step 6 – Verify the running container

```bash
docker ps
```

View startup and deployment logs:

```bash
docker logs maven-webapp-container
```

Open the application:

**http://localhost:8080/**

The Dockerfile copies the WAR to `ROOT.war`, so the application is deployed at the root context.

### Step 7 – Stop, start, and remove the container

```bash
docker stop maven-webapp-container
docker start maven-webapp-container
docker rm -f maven-webapp-container
```

Remove the image when it is no longer needed:

```bash
docker rmi maven-webapp:latest
```

---

## 3. Build the WAR inside Docker (multi-stage build)

Use this approach when you want Docker to compile and package the source code without requiring Maven to be installed on the host.

The `Dockerfile.multistage` has two stages:

1. **Build stage:** uses Maven + Java 17 to run `mvn clean package`.
2. **Runtime stage:** uses Tomcat 9 and copies only the generated WAR into the Tomcat image.

Build the image:

```bash
docker build -f Dockerfile.multistage -t maven-webapp:multistage .
```

Run it:

```bash
docker run -d --name maven-webapp-multistage -p 8080:8080 maven-webapp:multistage
```

Open:

**http://localhost:8080/**

If another container or local Tomcat is already using host port 8080, use a different host port, for example:

```bash
docker run -d --name maven-webapp-multistage -p 8081:8080 maven-webapp:multistage
```

Then open **http://localhost:8081/**.

To stop and remove this container:

```bash
docker rm -f maven-webapp-multistage
```

> The example multi-stage build uses `-DskipTests` to keep the build short for a lab exercise. Remove that option from `Dockerfile.multistage` if tests must run as part of the image build.

---

## 4. Run using Docker Compose

The included `docker-compose.yml` builds the default `Dockerfile`, creates a container, and maps port 8080.

Build and start in the background:

```bash
docker compose up -d --build
```

List Compose services:

```bash
docker compose ps
```

View logs:

```bash
docker compose logs -f web
```

Open:

**http://localhost:8080/**

Stop and remove the Compose container and network:

```bash
docker compose down
```

---

## 5. Troubleshooting

### Error: `port is already allocated` or `address already in use`

Another process (often local Tomcat from Eclipse) is already using host port 8080.

Stop local Tomcat in Eclipse, or map a different host port:

```bash
docker run -d --name maven-webapp-container -p 8081:8080 maven-webapp:latest
```

Open `http://localhost:8081/`.

### Container exits immediately or the application does not load

Check:

```bash
docker ps -a
docker logs maven-webapp-container
```

Confirm that the WAR was generated:

```bash
ls -lh target/*.war
```

### HTTP 404 – Not Found

Check the Tomcat logs and confirm that the WAR deployed successfully. Also check the application's URL/context path. This example renames the WAR to `ROOT.war`, which uses `/` as the context path.

### `javax.servlet` classes cannot be found or deployment fails

This example targets Tomcat 9 and the `javax.servlet` namespace. Applications using `jakarta.servlet` generally require Tomcat 10.1+ and compatible dependencies/source code. Do not mix the two servlet namespaces.

### Docker permission denied on Linux

Try the command with `sudo`, or configure Docker group access as described in the prerequisites and log in again.

---

## 6. Important Docker concepts

| Term / instruction | Meaning |
|---|---|
| Dockerfile | Text file containing image build instructions |
| Image | Packaged application/runtime template |
| Container | Running or stopped instance of an image |
| `FROM` | Selects the base image |
| `RUN` | Executes a command while building the image |
| `COPY` | Copies files into the image |
| `EXPOSE` | Documents the port the application listens on |
| `CMD` | Default command executed when the container starts |
| `-p 8080:8080` | Maps host port 8080 to container port 8080 |
| `docker compose` | Defines and runs services using YAML |

## 7. Quick practical-exam command sequence

For the single-stage Dockerfile:

```bash
# 1. Generate WAR
mvn clean package

# 2. Build image
docker build -t maven-webapp .

# 3. List images
docker images

# 4. Run container
docker run -d -p 8080:8080 --name mycontainer maven-webapp

# 5. Check running containers
docker ps

# 6. View logs
docker logs mycontainer

# 7. Stop and remove container
docker stop mycontainer
docker rm mycontainer
```

For Docker Compose:

```bash
docker compose up -d --build
docker compose ps
docker compose logs -f web
docker compose down
```

---

## 8. Notes

- The WAR must be generated successfully before using the single-stage `Dockerfile`.
- The multi-stage Dockerfile builds the WAR inside Docker.
- The default port is 8080; choose another host port if it is occupied.
- `EXPOSE 8080` documents the container port; it does not publish the port by itself. The `-p` option publishes it.
- Never commit passwords, API keys, database credentials, or other secrets into the repository.
