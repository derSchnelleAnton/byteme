# <code style="color:darkorange">ByteMe<code>
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Linux](https://img.shields.io/badge/Linux-FCC624?style=for-the-badge&logo=linux&logoColor=black)
![Windows](https://img.shields.io/badge/Windows-0078D6?style=for-the-badge&logo=windows&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white)
![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)

This web application serves as a comprehensive platform for restaurants to manage their menu and orders, and for customers to browse menus, place orders, and receive deliveries. It aims to streamline the ordering process for clients and provide powerful administrative tools for restaurant owners.

## Table of Contents
- [Features](#featuresFeatures)
  - [For Clients](#for-clients)
  - [For Restaurant Admins](#for-restaurant-admins)
- [Technology Stack](#technology-stack)
  - [Framework](#framework)
  - [Version Control](#version-control)
  - [CI/CD](#continuous-integrationcontinuous-deployment-cicd)
  - [Deployment Platform](#deployment-platform)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
- [Deployment & CI/CD](#deployment--cicd)
- [Project Structure](#project-structure)
- [Usefull links](#useful-links)
- [Deploying using Docker](#deploying-using-docker)
- [Contributors](#contributors)
- [License](#license)

## Features

### *For Clients*

- Browse Restaurant Menu: Easily view all available menu items with descriptions and pricing.

- Add to Cart: Select desired dishes and add them to a virtual shopping cart.

- User Authentication: Secure login/registration for personalized ordering experience.

- Place Orders: Submit orders for delivery directly through the application.

- Order Tracking: (If applicable) Track the status of placed orders.

### *For Restaurant Admins*

- **Menu Management (CRUD):**

  - Create new menu items.

  - Read and view existing menu details.

  - Update details of current menu items.

  - Delete outdated or unavailable menu items.

  - Order Status Management: Update the status of incoming orders (e.g., `PENDIN, CONFIRMED, INPROGRESS, DELIVERED, . . .`).

- **Dashboard & Business Analysis:**

  - View an overview of daily/weekly/monthly sales.

  - Analyze popular dishes and sales trends.

  - Gain insights into business performance.

## Technology Stack

This application is built using modern and robust technologies to ensure performance, scalability, and maintainability.

### Framework:
ByteMe is built using [***Vaadin***](https://vaadin.com),  A powerful Java framework for building modern web applications with a focus on developer productivity and rich user interfaces.

### Version Control:
![Git](https://img.shields.io/badge/git-%23F05033.svg?style=for-the-badge&logo=git&logoColor=white)
![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)

All source code is managed and version-controlled using [***Git***](https://git-scm.com/), hosted on [***GitHub***](https://github.com) for collaborative development.

### Continuous Integration/Continuous Deployment (CI/CD):
![CircleCI](https://img.shields.io/badge/circle%20ci-%23161616.svg?style=for-the-badge&logo=circleci&logoColor=white)

Automated build, test, and deployment pipelines are configured with [***CircleCI***](https://circleci.com/) to ensure code quality and efficient delivery.

### Deployment Platform:
![Azure](https://img.shields.io/badge/azure-%230072C6.svg?style=for-the-badge&logo=microsoftazure&logoColor=white)

The application is deployed and hosted on [***Microsoft Azure***](https://azure.microsoft.com), leveraging its scalable and reliable cloud infrastructure.

## Getting Started
To get a local copy of this Web App running, follow the following simple steps

### Prerequisites
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJIDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)
![Visual Studio Code](https://img.shields.io/badge/Visual%20Studio%20Code-0078d7.svg?style=for-the-badge&logo=visual-studio-code&logoColor=white)
![Visual Studio](https://img.shields.io/badge/Visual%20Studio-5C2D91.svg?style=for-the-badge&logo=visual-studio&logoColor=white)
- Java Developement Kit ([***JDK***](https://www.oracle.com/at/java/technologies/downloads/)) 11 or higher

- Maven, for more information on Maven visit [Apache Maven](https://maven.apache.org/)

- A compatible IDE (You can download [***IntelliJ community edition***](https://www.jetbrains.com/idea/download) or [***VsCode***](https://code.visualstudio.com/) if you do not have a suitable IDE already.)


### Installation

1. ***clone the repository.***\
   The project can be cloned to a local drive following using Web based UI ( follow instructions [here](https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository) ) or Using tools available in the **IDE** of choice. alternatively, one can use command line tools using the following commands
  - ***Linux / Mac***
    ```bash
    git clone https://github.com/derSchnelleAnton/byteme.git && cd byteme
    ```
  - ***Windows***
    ```bash
      git clone https://github.com/derSchnelleAnton/byteme.git && cd byteme
    ```
2. ***Build the project***\

Open the project in an IDE. You can download [***IntelliJ community edition***](https://www.jetbrains.com/idea/download) or [***VsCode***](https://code.visualstudio.com/) if you do not have a suitable IDE already.
Once opened in the IDE, locate the `Application` class and run the main method (using *"Debug"* if needed).

    For more information on installing in various IDEs, see [how to import Vaadin projects to different IDEs](https://vaadin.com/docs/latest/getting-started/import).


    > **NOTE:** *If you install the Vaadin plugin for IntelliJ, you should instead launch the `Application` class using "Debug using HotswapAgent" to see updates in the Java code immediately reflected in the browser*

    ***Optionaly:*** \
    The project is a standard Maven project. To create a production build, call 

    ```bash
    ./mvnw clean package -Pproduction
    ```
    If you have Maven globally installed, you can replace `./mvnw` with `mvn`. as such
    ```bash
    mvn clean package -Pproduction
    ```
3. ***Run the application:***\

    <code style="color : orangered"> ***NOTE :*** *This step is not neccessary if Application is built using an IDE's GUI tools*</code>

   The above commands will build a JAR file with all the dependencies and front-end resources,ready to be run. The file can be found in the `target` folder after the build completes.
   You then launch the application using
    ```
    java -jar target/byteme-1.0-SNAPSHOT.jar
    ```
   Alternatively,
   The application can be run using the following command
    ```bash
    mvn spring-boot:run
    ```
   The application should now be accessible in your browser at http://localhost:8080.

## Deployment & CI/CD
![CircleCI](https://img.shields.io/badge/circle%20ci-%23161616.svg?style=for-the-badge&logo=circleci&logoColor=white)

The application's deployment process is fully automated:

1. **Code Push:** Developers push code changes to the main branch on GitHub.

2. **CircleCI Trigger:** CircleCI automatically detects the new push and triggers a predefined pipeline.

3. **Build & Test:** The pipeline builds the application, runs all unit and integration tests.

4. **Deployment to Azure:** Upon successful completion of tests, CircleCI deploys the application to the configured Azure environment.
## Project structure

The project follows a standard Maven project structure, with specific directories for source code, resources and tests
```
BYTEME/
├── src/
│   ├── main/
│   │   ├── java/                         # Main Java source code
│   │   │   └── edu/
│   │   │       └── byteme/
│   │   │           │
│   │   │           ├── Application.java          # Main Spring Boot entry point
│   │   │           ├── views/                    # Vaadin UI components and views
│   │   │           ├── services/                 # Business logic and services
│   │   │           ├── data/                     # Data models and repositories
│   │   │           ├── events/                   # broadcasting services
│   │   │           └── security/                 # Security configurations
│   │   │
│   │   └── resources/                    # Application resources (e.g., application.properties, static assets)
│   │       ├── META-INF/
│   │       │   └── resources/
│   │       │       └── frontend/         # Frontend resources for Vaadin (e.g., styles, JS modules)
│   │       │
│   │       └── application.properties    # Spring Boot configuration
│   │
│   └── test/
│       └── java/                         # Test Java source code
│           |── data/                     # Unit tests
│           └── views/                    # Unit tests
│
│
├── pom.xml                               # Maven Project Object Model (POM) file
├── .circleci/
│   └── config.yml                        # CircleCI configuration for CI/CD pipeline
├── .gitignore                            # Git ignore file
└── README.md                             # This README file
```



## Useful links

- Read the documentation at [vaadin.com/docs](https://vaadin.com/docs).

- Follow the tutorial at [vaadin.com/docs/latest/tutorial/overview](https://vaadin.com/docs/latest/tutorial/overview).
- Search UI components and their usage examples at [vaadin.com/docs/latest/components](https://vaadin.com/docs/latest/components).
- View use case applications that demonstrate Vaadin capabilities at [vaadin.com/examples-and-demos](https://vaadin.com/examples-and-demos).
- Build any UI without custom CSS by discovering Vaadin's set of [CSS utility classes](https://vaadin.com/docs/styling/lumo/utility-classes).
- Find a collection of solutions to common use cases at [cookbook.vaadin.com](https://cookbook.vaadin.com/).
- Find add-ons at [vaadin.com/directory](https://vaadin.com/directory).
- Ask questions on [Stack Overflow](https://stackoverflow.com/questions/tagged/vaadin) or join our [Forum](https://vaadin.com/forum).
- Report issues, create pull requests in [GitHub](https://github.com/vaadin).


## Deploying using Docker
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)

To build the Dockerized version of the project, run

```
mvn clean package -Pproduction
docker build . -t byteme:latest
```

Once the Docker image is correctly built, you can test it locally using

```
docker run -p 8080:8080 byteme:latest
```

## License

Distributed under the MIT License. See LICENSE for more information.

## Contributors
- [Adrian Tibereu Petre](https://github.com/darthkojones)
- [Anton Wörndle](https://github.com/derSchnelleAnton)
- [Kahsay Tinsae Ghilay](https://github.com/tinsae-ghilay)
- [Marc Böhme](https://github.com/marc-is-coding)
- [Patricia Füruter](https://github.com/Paciat)

Project Link: https://github.com/derSchnelleAnton/byteme