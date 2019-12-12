# Captain Hook - Walking Notifications With Style

Notifications are the best way to raise awareness to people. Unfortunately, platforms often allow us to receive notifications through email, while we now have tons of ways to receive a notification.

Webhooks are the common sense when it comes to create awareness. Basically everything supports webhooks now so you can plug your endpoint and be aware of something. It's a very nice (and cheap) way of exposing integration functionality.

Yes! You got it right! Captain Hook is responsible for connecting a webhook to your notification platform of choice.

## How To Build

This is a 100% Java application so you can just do the idiomatic `mvn package`.

## How It Works

Captain Hook is tough but allows three different ways of delivering a notification:

- by taking a webhook and producing events that can be subscribed for delivering notifications
- by taking an event and deliver notifications to its subscribers
- by taking the notification as is

