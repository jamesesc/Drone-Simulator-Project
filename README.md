## Drone Simulator Project
https://github.com/jamesesc/Drone-Simulator-Project

--Sixth Deliverable--

Oisin Perkins-Gilbert: The UI that James made was looking really sharp, and I especially love how the User can move and zoom around in the drone simulation (that looks difficult). Luckily for me, there was still work to do, as the MenuBar had some buttons that needed functionality, and I still needed UI for something to Query the database. By far the hardest part of this, again, was finding the time as this week was a pretty big holiday week and I had plans with family all the way in Oregon.

James Escudero: For this deliverable, I had help out on Oisin UI that he made.
I had enhanced and upgraded the look and feel of our UI system. 
Since I had had more available time this week,
I definitely had put greater amount of time and work onto to polishing and refining our project.
When working on the UI, there was a learning curve in learning JavaFx, but it wasn't that horrible,
since it was quite similar to swing in a way.
For this incoming week before the submission,
I'm mostly going back and refine the codebase in terms of refactoring to follow OO,
DRY, and other important concepts that we learned so far.
The core functionality of displaying, generating and storing
anomalies is quite complete. 

Mankirat Mann: I was really busy this week, but I created the dark themed Leaflet map HTML and fixed some database issues, including a clearDatabase() method. I spend a lot of time and tried integrating the map into the simulation, but it kept showing different errors or something in the code would break, so I am trying a different approach right now which would hopefully work.

--Fifth Deliverable--

Oisin Perkins-Gilbert:
I think the biggest thing I've had to deal with this deliverable was being generally confused on what to do
(A lot of the tasks I've done this deliverable is just miscellaneous stuff).
Up until this point, we've been able to largely work on our own on our own separate parts
(encapsulation means we don't really need to worry about the internals of what the other person's classes are doing).
I think as we go forward, we're going to need to work together more in person in order to get things done.
Luckily, I've been keeping myself busy with my GUI classes so far.

James Escudero: For this deliverable, no major issues occurred.
Moving forward closer to the end of the project,
I personally just need to adjust and tweak the telemetry generator to be more fluid and smooth.
Additionally,
with the confirmation of full front end and back end connection is in the most part fully functional is good.
With that, I now need to just connect and ensure the database is fully connected as well with everything.
Lastly, the final implementation for the next deliverable would be implementing the charging system of the drone.
With that, beside bug and polish statements, that would be getting closer to the complete finish of the project.

Mankirat Mann: This week I focused on getting the anomaly system fully connected to the database. I fixed issues with the class versions, updated the save logic, and made sure each anomaly stores the correct telemetry and drone information. I also spent time debugging and cleaning up the backend so everything works smoothly from detection to saving the data.

--Fourth Deliverable--

Oisin Perkins-Gilbert: The most difficult thing I did this week was making this GUI, getting it just right,
and then tearing it apart to make something completely different like Frankenstein's monster.
Overall, it was still worth it, as the layout that James came up with was better in pretty much every regard.
I still need to pretty it up and make it more efficient though, so it's definitely a work in progress.
Also juggling my time is pretty hard, I'm currently typing this from work
(unfortunately, I am a part-time worker and a full-time student).

James Escudero: James Escudero: For this week deliverable, there were 2 things that were quite challenging.
One of these difficulties was the time management as I was drained from 2 midterms I had.
Not only that, there were other responsibilities and events that I had to attend to.
The other hard difficulties were refactoring the codebase to be more organized and modular
(especially for DroneMonitorApp).
I realized that the class was getting to big where it was handling many responsibilities at once.
After this realization,
it was too late as I was struggling to conceptually and implementing the class to be more modular.
However, after some thinking, I was able to do so.
For the following weeks, we need to really start implementing and connecting the front end with the back end.


Mankirat Mann: This week was hard because of midterms, ICPC, and other responsibilities.
While working on the map, I ran into problems some problems because it loads, but it doesn't load correctly.
I made some progress with the GUI and drone display, but it still needs work.

--Third Deliverable--

Oisin Perkins-Gilbert: I'd say the most difficult thing about this week's deliverable has been having to go back and fix something I thought I already finished, or make it better and more efficient. I'd often do something one way, then find another way to do it a different way, and pray that it wouldn't cause any bugs trying to make it work.

James Escudero: For this third deliverable, the only issues that I want to discuss my team is the whole idea of 
what data should droneMonitor be sending over to the front end.
Additionally, I’m curious if it is okay for the front end 
to use the drone methods like getBattery, or even the telemetry getters method.
Moreover, this deliverable for me was 
mostly progressing more on connecting the frontend to the backend (droneMonitor).
This is especially true as I 
refactored and implemented more structure in the droneMonitor to handle the overall logic for the simulation.

Mankirat Mann: I think the most difficult part this week was dealing with dependency and Jdbc driver issues.
For some reason, I had to re-add the things into the project.
But overall, things went smoothly after.

--Second Deliverable--

Oisin Perkins-Gilbert: Still, the biggest issue I've had with this project has been fitting in the time to do it,
as I've had Halloween plans, my job, and other classes during the time I could've spent working on this. Besides that,
most of this deliverable has been spent with me googling how to do things in JavaFX, going through documentation and
stack overflow forums.

James Escudero: For this 2nd iteration, there weren't any major issues.
The only thing that we needed as a team to 
discuss is just the technical procedures and structure of how we want our drone to operate in the simulation, 
particularly like the telemetry generator.
Additionally, I was able to figure out this long and painful issue in 
developing a realistic velocity generator.
In a very simple and short form, I was able to design where we take 
account of the current drone velocity, where the velocity closer to the original would have a higher chance to be 
generated than the ones that's like +50 away.

Mankirat Mann: For this second deliverable, I worked on learning the basics of JavaFX to get ready for building the
drone project’s user interface. I also looked into Scene Builder and FXML to see how design and code work together. 
This week was really busy with other classes and other personal things, so I didn’t have much time, but I still learned 
more about how to make the UI interactive for next time.


--First Deliverable--

Oisin Perkins-Gilbert: The biggest issue I've had with this project has been trying to find the time to fit 10 hours 
of project work. I think the most difficult class I had to implement was the one that checks for teleportation, as I
had to brush up on my physics and use actual formulas. Making JUnit tests also took a very long time, and probably 
accounted for half if not more of the time I spent developing this.


James Escudero: For this first iteration, it was quite new and confusing. I would say most of my difficulty was 
actually creating the user stores and doing all the required tasks we needed to do with youtrack. After doing this 
first iteration, I'm still a bit unsure about creating user stories in itself. Moving on to the development side of
our project, there wasn't any huge headache to it. I just develop the very basic and bare bone of 3 classes that we
have. I had all the classes off the UML, which was quite helpful. 

Mankirat Mann: For this iteration, my main issue was setting up the sqlite database for storing the drone anomalies. 
I spent a good amount of time troubleshooting the JDBC driver in IntelliJ, and I had issues with putting the jar file
in IntelliJ. And learning the SQL syntax was a little confusing at first because I had only done it on Python before,
but I am slowly getting used to it.
