# Dummy Crud Application

The Dummy CRUD Application is a straightforward  application built using the WebFlux framework and DynamoDB database. It serves as an educational example of how to create a CRUD (Create, Read, Update, Delete) system for managing two primary entities: Person and Post. This application showcases the fundamental operations that can be performed on these entities.

# Getting started:
### 1. Clone the Repository

```bash
git clone https://github.com/arup-dsi/dummy-crud.git
cd dummy-crud 
```
### 2. Start Docker Compose
Before running the application, you need to start Docker Compose to set up the necessary containers.

```bash
docker-compose up -d
```
Now, when dynamoDB is up, go to http://localhost:8001/ and create two tables
named **Person** and **Post**.
For the **Person** table, set Hash Attribute Name (it's type as string) as **Id** and complete the table creation.
For the **Post** table, set Hash Attribute Name (it's type is also a string) as **PostId** and create the table creation.

### 3. Build and Run the Application
Once the containers are up and running, you can build and run the WebFlux CRUD application.


## Operations performed on the two entities:
### 1. Person

- **Create**: Users can create a new `Person` by providing relevant information such as name.
- **Fetch by personId**: It is possible to retrieve a `Person` by specifying their unique `personId`.
- **Delete by personId**: Users can delete a `Person` record by providing the `personId`.
- **Update**: Users can update the attributes of an existing `Person` entity. For updating, the  `Person`  must exist in the database with a valid `personId`.
- **Fetch All**: The application supports fetching a list of all `Persons` stored in the database.
  All above mentioned functionalities are implemented in the **PersonController** class.

### 2. Post

- **Create**: Existing `Person` with valid IDs can create new `Post`. Posts are associated with the person who created them.
- **Fetch by postId**: Users can retrieve a `Post` by specifying its unique `postId`.
- **Delete by postId**: Users can delete a `Post` by providing its `postId`.
- **Find Posts by personId**: Users can find all the `Post`'s created by a specific `Person` by specifying their `personId`.
  All above mentioned functionalities are implemented in the **PostController** class.