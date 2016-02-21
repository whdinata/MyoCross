# MyoCross
----------------
Using the combination of [Estimote Beacons](http://estimote.com/) and [Myo Wristband](https://www.myo.com/) we created an effective solution for visually impaired people who want to move around the city. After syncing the Myo Wristband with their phone, they can easily activate pedestrian light controls remotely.

**Team:** [Willy Dinata](https://github.com/whdinata), [Dalimil Hajek](https://github.com/dalimil), [Ali Akbar](https://github.com/aliakbars)


## Inspiration

If a person wants to use a pedestrian crossing, they must press the control box button manually. This can be a challenging task for someone who is visually impaired or/and has reduced mobility.

## What it does

We remove the requirement to physically touch the control box and allow users to control it with a Myo Wristband. At the same time we provide feedback to the wristband user in the form of vibrations based on the proximity of a traffic light and its current state (green/red).

## How we built it

We created an Android app which communicates with the Myo Wristband and with the traffic lights (but there is no need to use the phone - user only needs to launch the app and after syncing with Myo they can put it in their backpack and never use it... user only needs to control the Myo

## Challenges we ran into

We didn't have a physical Pedestrian control box - so we had to create a server which listens to the requests and updates/schedules light switching. We used WebSockets to do this dynamically. Then of course we used the Estimote Beacon which would be part of the same physical control box.

## What's next for MyoCross

We would like to make people involved in city councils around the country aware of this project. We would love to see a real world implementation. We believe that this system could help many people with visual impairment and make their lives significantly easier...

## Built With: Android, Myo, jQuery, JavaScript, Python, Flask, Estimote, Beacon, WebSockets

## Screenshots

![Pedestrian Lights](https://github.com/whdinata/MyoCross/blob/master/screenshots/pedestrian-lights.gif)
![Setup Myo](https://github.com/whdinata/MyoCross/blob/master/screenshots/myo.png)
![No Traffic](https://github.com/whdinata/MyoCross/blob/master/screenshots/no-traffic.png)
![Red Light](https://github.com/whdinata/MyoCross/blob/master/screenshots/red-light.png)
![Green Light](https://github.com/whdinata/MyoCross/blob/master/screenshots/green-light.png)