## Overview Meal planner for the week
### Description
Add recipes for each day of the week, get a shopping list to buy, share your week with others, follow or copy other people plan, create your own recipes or get recipes from the web or others plan.

### App Evaluation

- **Category:** Food & Drink
- **Mobile:** Mobile first experience
- **Story:** Allows you to create a week meal plan, create your own recepies, get a shopping list, copy other people plan or recepies
- **Market:** Anyone who wants to organize their meals.
- **Habit:** Users are constantly using the app to follow the recipe or to see the shopping list, each week the user will need to create a meal plan, this meal plan can be changed at anytime
- **Scope:** V1 would allow users to create recepies and add them to the week, V2 would allow users to see other people's plan, copy other people's plan or recepies.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User can create an account
* User can login and logout
* User can create recepies
* User can add recepies for each day of the week
    * User can search for recepies with a search bar
* User can see the shooping list of the week
    * User can add more things to the shoopíng list
* User can search for other user
* User can see other user profile
    * User can see week plan
    * User can see the other user personal recepies
* User can copy recepies from other user
* User can copy other person's meal plan
* User can see recepies from a database and add these recepies to his recepies
* User can filter the search of recepies by:
    * ...

**Optional Nice-to-have Stories**

* User can add a *order food* option to buy food from a restaurant or something like that
* User can follow other users
* User can get updates from other pople's plan
* User can save week plans to be able to use them later
* User can choose to have a private profile, which is not visible for anyone else
* User can click on a random generator button that will create a random week plan with random food.
* User can orginize recepies with tags
    * User can search recepies with tags


### 2. Screen Archetypes

* Login screen
   * User can login
* Registration screen
   * User can create an account
* Week screen
    * User can add recepies for each day of the week
        * User can search for recepies with a search bar
* Recepies screen
    * User can create recepies
* Shooping list screen
    * User can see the shooping list of the week
* Social screen
    * User can search for other user
    * User can see other user profile
* Profile screen
    * User can see other user profile
        * User can see week plan
        * User can see the other user personal recepies
    * User can copy recepies from other user
    * User can copy other person's meal plan


### 3. Navigation

**Tab Navigation** (Tab to Screen)

* your week/shooping list
* your recepies
* search other users 
* Profile screen

**Flow Navigation** (Screen to Screen)

* Login screen
   * Registration screen
   * your week screen
* Your week screen
   * shooping list
   * recepie details screen
   * saved recepies screen
* Saved recepies screen
    * recepie details screen
    * Online recepies screen
        * recepie details screen
* Social screen
    * User profile screen
        * User's week plan screen
        * User's saved recepies screen
* Profile screen

## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="YOUR_WIREFRAME_IMAGE_URL" width=600>

### Link:  https://www.figma.com/file/ydaSJP7CCaMLQ4GeqtKAmZ/Food-planner?node-id=0%3A1 

<img src="https://i.imgur.com/ZORPpzu.png" width=600>

<img src="https://i.imgur.com/rirUkJk.png" width=1100>

<img src="https://i.imgur.com/VkN4yBC.png" width=600>

<img src="https://i.imgur.com/TBOQzAU.png" width=600>

<img src="https://i.imgur.com/kLul7q7.png" width=1100>

<img src="https://i.imgur.com/L61zLH1.png" width=300>



<!--![](https://i.imgur.com/ZORPpzu.png)

![](https://i.imgur.com/rirUkJk.png)

![](https://i.imgur.com/VkN4yBC.png)

![](https://i.imgur.com/TBOQzAU.png)

![](https://i.imgur.com/kLul7q7.png)

![](https://i.imgur.com/L61zLH1.png)-->




## Schema 
[This section will be completed in Unit 9]
### Models
[Add table of models]


**Model: User**
| Property  | Type     | Description                                                              |
| --------- | -------- | ------------------------------------------------------------------------ |
| objectId  | String   | unique user identifier (default field)                                   |
| createdAt | DateTime | date when user is created (default field)                                |
| updatedAt | DateTime | date when user is last updated (default field)                           |
| name      | String   | user name                                                                |
| lastname  | String   | user lastname                                                            |
| isPublic  | Boolean  | indicates if the user's profile is public to appear on the social screen |
| email     | String   | user email                                                               |
| password  | String   | user password                                                            |

**Model: Saved Recepie**
| Property     | Type            | Description                                       |
| ------------ | --------------- | ------------------------------------------------- |
| objectId     | String          | unique recepie identifier (default field)         |
| createdAt    | DateTime        | date when recepie is created (default field)      |
| user         | Pointer to User | user that saved this recepie                      |
| title        | String          | title of the recepie                              |
| imageUrl     | String          | url of the recipe image                           |
| dishType     | String          | The dish type a recipe belongs to                 |
| mealType     | String          | The type of meal a recipe belongs to              |
| cuisineType  | String          | The type of cuisine of the recipe                 |
| totalTime    | String          | approximate time to cook the recipe               |
| calories     | String          | calories of the recepie                           |
| recepieUrl   | String          | Url to the original site of the recepie           |
| instructions | String          | instructions too cook the recepie                 |
| ingredients  | Array           | list of ingredients                               |
| updatedAt    | DateTime        | date when recepie is last updated (default field) |

*Note: the online recipes will be obtained from Edamam API, when the user saves these recipes we add them to this table.*
*Note 2: Edamam API doesn't have instructions, the user should add the instructions manually, but it gives you the original url for the recepie*

**Model: Week**
| Property  | Type               | Description                                       |
| --------- | ------------------ | ------------------------------------------------- |
| objectId  | String             | unique recepie identifier (default field)         |
| createdAt | DateTime           | date when recepie is created (default field)      |
| day       | String             | day of the week                                   |
| user      | Pointer to User    | user that added this recepie to his week          |
| recepie   | Pointer to Recepie | Recepie added to this day of the week             |
| quantity  | Integer            | number of recepies                                |
| updatedAt | DateTime           | date when recepie is last updated (default field) |

**Model: Item**
| Property  | Type     | Description                                       |
| --------- | -------- | ------------------------------------------------- |
| objectId  | String   | unique recepie identifier (default field)         |
| createdAt | DateTime | date when recepie is created (default field)      |
| name      | String   | name of the product                               |
| amount    | String   | amount of the product                             |
| updatedAt | DateTime | date when recepie is last updated (default field) |


**Model: Shooping list**
| Property  | Type            | Description                                       |
| --------- | --------------- | ------------------------------------------------- |
| objectId  | String          | unique recepie identifier (default field)         |
| createdAt | DateTime        | date when recepie is created (default field)      |
| user      | Pointer to User | user that owns this list                          |
| item      | Pointer to Item | Item to buy                                       |
| checked   | Boolean         | Did you already buy the item?                     |
| updatedAt | DateTime        | date when recepie is last updated (default field) |






### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]

* Login screen
    * (Read/GET) User authentication
* Registration screen
    * (Create/POST) Create a new user
* Week screen
    * (Read/GET) Query user week plan
    * (Create/POST) Add new recipes to the user's plan
    * (Update/PUT) Update the number of recepies
    * (Delete) Delete recepies from the user's plan
* Recepies screen
    * (Read/GET) Query all user's saved recepies
    * (Read/GET)(API) Query recepies from Edamam's API
    * (Creat/POST) Save new racepies from Edemam library
    * (Update/PUT) User can edit his saved recepies
    * (Delete) Delete recepies from saved recepies
* Shooping list screen
    * (Read/GET) Query all user shooping list
    * (Creat/POST) Add new items to the shooping list
    * (Update/PUT) Update the item from the shooping list
    * (Delete) remove items from the shooping list
* Social screen
    * (Read/GET) Query users
    * (Read/GET) Query user's week plan
    * (Read/GET) Query user's saved recepies
    * (Update/PUT) User can copy other user's plan
    * (Creat/POST) User can copy other user's saved recepies
* Profile screen
    * (Read/GET) Query username and public profile option
    * (Update/PUT) Update public profile option


## Ideas

* Use Hunter API to verify emails: https://hunter.io/api
* Use Youtube API to reproduce recepies videos: https://developers.google.com/youtube/
* Use kiwilimon API to get recepies or other API like edamam
* Add a button to each ingredient to search the ingredient in a store like walmart, and redirect the user to google with the search
* Use stores APIS to get the price for the products in the shooping list
* Filter recepies
* recommend recipes 
