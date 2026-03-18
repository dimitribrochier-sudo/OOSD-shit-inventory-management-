HI.

We are going to be using the MVC Layered Architecture to help scaling the project.

Config will hold global changes such as DB connections, themes, baseframes/windows.

Controllers are the action doers, like pressing a button or closing an app or adding a new user by entering in an empty field

Models will hold the objects and its attributes as well as the getters and setters

repository handles the database level of things, like prepared statements and stuff

resources and resources.images hold whatever pic,sound,links we want to hold for frontend

services handles the business logics, like add product LIKE AND INDIVIDUAL AMOUNT.

utils can hold usermade exceptions

views will hold only UI elements and as such is pretty bwig.

So the app will be like, launch the views, views hold buttons who are connected to controllers who then will either run a service , which can either be a repo handle
or a user exception. the models are the actors and yeah thaaz about the structure


TODOLIST = Review code to understand it,
            fix some buttons,
            flow wack
            database sales, have timed event
            User edit needs restructure(switching position)
            Complete SalesScreen <--todo**
            Rework Dashboard, maybe have a screen? to improve flow, for later (planning for not planning)
            Work on limit crud ====> kabitch
            error handlin ==> everyone hehe
        ============================    =================================================================
            NEW SCREENS YIPPEE
            ReportScreen.
            OrderScreen.========>Brochalchemly
            Receipt <------We know know(a list of truncated receipt, which opens up to the gemini image sent in group)¬¬¬¬¬¬¬¬¬>Smrith
            Analytics for revenue (DO DA GRAPH)===>katomic
            supplier's products(Holds suppliers product) <copy for product and supplier.



            ordertrackin(manager special)
            saleTracking(manager special)





NEW ROLES(
    Manager(overseers employers and suppliers, report)
    Admin(god)
    employee(track products and manage orders add a report.)
    Supplier's product(Price listyes ) need tables in tbd
)


Screens

LoginPage 1
Dashboard 2 (Rework)
User(holds people) 3
Customer 4
Product 5
Supplier (holds people)6 (when supplier delete, fk in product is null)

Sales 8
Order 9
Tracking of products 10 (when products status (on arrival, received, damaged, return)
Tracking of orders 11 (when products status (on arrival, received, damaged, return)
Alerts 12 (show low stock of items, or missed deadlines or whatever)
inv transactions 13

Report screens 15 (shows information of what happens in the inventory


