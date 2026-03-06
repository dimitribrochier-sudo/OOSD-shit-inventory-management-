HI.

We are going to be using the MVC Layered Architecture to help scaling the project.

Config will hold global changes such as DB connections, themes, baseframes/windows.

Controllers are the action doers, like pressing a button or closing an app or adding a new user by entering in an empty field

Models will hold the objects and its attributes as well as the getters and setters

repository handles the database level of things, like prepared statements and stuff

resources and resources.images hold whatever pic,sound,links we want to hold for frontend

services handles the business logics, like add product.

utils can hold usermade exceptions

views will hold only UI elements and as such is pretty bwig.

So the app will be like, launch the views, views hold buttons who are connected to controllers who then will either run a service , which can either be a repo handle
or a user exception. the models are the actors and yeah thaaz about the structure
