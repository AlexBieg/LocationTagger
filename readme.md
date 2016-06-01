# Location Tagger

### Team
Tianai Zhao, Tyler Bauer, Alex Bieg, Tin Ho

### Project Description
Location Tagger is an application that allows users to create virtual points in a geographical location
that others can view and interact with. You can think of it like writing with chalk on the ground to let people
know there is a party happening on Saturday, only now all you need is a smart phone.

### User Stories
The user stories for the Location Tagger are:

* As a student at UW, I want to know different event information so that I can go and attend those events.
* As a RSO on campus, I want to post digital flyer and related information, so that I can attract more people to my events and activities.
* As a student at UW, I want to share event information with my friends, so that we can go together.
* As a RSO on campus, I want to see the history of my own posts, so that I can have some records to keep.
* As a RSO on campus, I want to remove my event tag a day after the event just like the chalk drawing on the ground, so that people don't get unnecessary information.
* As a student at UW, I want to see all the event tags within half mile, so that I can check out different events.
* As a student at UW, I want to see the location of the event on a map, so that I can easily find the place of the event.

### Main Flow of Events
There are 3 main screens to the application.

1. #### Map
The map screen shows the users current location as well as all of the tags around them. Tapping on
a point will show it's title. Tapping on the tile will bring the user to a tag detail screen.
The user can add a new tag by clicking the floating plus button in the bottom right.
The tabs at the bottom allow the user to switch between the main screens.

2. #### Augmented Reality
The augmented reality screens overlays near by tags onto a camera preview. Any tags within a half mile
will be shown when the user is looking in their direction. Tags that are farther away appear smaller.
The tabs at the bottom function the same as the maps and the floating plus button adds a new tag.

3. #### List
The list shows all of the tags in order of their date soonest to farthest. Tapping on a tag will take
the user to a single tag screen. The user is also able to view just their tags by tapping the appropriate button.

#### Other Screens
1. #### Log In
The log in screen creates a profile for the user if they do not already have one as well as lets existing
users log in. This allows us to track user data across session and devices. We are also able to find only
the tags one user created.

2. #### Create New Tag
This screen allows a user to create an entirely new tag. The user can specify a title, description, and date.
The location of the tag is determined by the user's current location. Once a tag's date has passed it will
be removed from the service.  

3. #### Individual Tag Screen
This screen contains the same information as the Create New Tag screen, but in a format better suited for
reading. It also includes a share button so that users can easily let others know about events. 

4. #### User's Tag List Screen
This screen contains a list of tags that the current user created, so that user is able to see the history of their own tags.
