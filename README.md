# Traffic Light Controller - Patrick Woodrum

![Intersection Preview](int_preview.png)

This application will function as a traffic controller visual with minimal aesthetics. It will consist of an intersection, four traffic lights, and the logic to make them function as they should in the real world with added buttons for total control. 

This project will be used to showcase my abilities with both backend and frontend coding, using Java and Vue respectively.

Six scenarios have been provided alongside a background descriptor. I will be completing as many as possible while adding my own personal touch. I intend to make this a work I will be proud of, as I strive to do with every project.

Upon completion or submission the following questions will be answered:

1. How would you evaluate your work? What went well? What would you do differently?
    **For 2-3 days of work I believe it turned out well. The backend code logic ended up working as intended from the start with minimal changes required. Slight tweaks of the scheduler were required to keep up with the traffic light states
    but overall my thought process went smoothly. Looking back, I would have done more with the frontend ui at the start of the project. I spent a lot of time refactoring, reorganizing, and changing the look of the ui which in turn required
    more frontend->backend tweaks. Once I got the layout to look how I envisioned the entire process was much easier to plot out.**
2. What was an insight you gained or something you learned while working on this?
    **Cyclable tasks are powerful and fun, but require a high level of attention to detail. If your timings are off, data is being sent improperly, or a method call is out of place it can throw a real wrench in the entire task. Majority of my
    "bug fixing" for this project centered around the cycling and the issues that were raised with color states and timings. I also learned a good bit about ScheduledFuture and how it can be useful for state management.**
3. If you were to add another test challenge to this, what would it be? Why?
    **I enjoyed this project and how it was open ended enough to allow me freedom of design. To take this challenge a step further I would implement a car system. Now that traffic light cycling and timing has been nailed down, 
    I would like to see a "car recognition" system be put in place, similar to how real-life traffic stops have pressure plates in the ground to determine when a light should change as opposed to hard timers. It could pose a real, 
    but fun challenge to have the lights recognize when a car has been stopped and for how long to know when it should cycle itself.**
