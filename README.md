# Captain Hook - Walking Notifications With Style

Notifications are the best way to raise awareness to people. Unfortunately, platforms often allow us to receive notifications only through email, while we now have tons of ways to receive a notification.

On the other side, webhooks became the common sense when it comes to create awareness. Basically everything supports webhooks now so you can plug your endpoint and be aware of something. It's a very nice (and cheap) way of exposing integration functionality.

Yes! You got it right! Captain Hook is responsible for connecting a webhook to your notification platform of choice.

## How To Build

This is a 100% Java application so you can just do the idiomatic `mvn package`.

## How It Works

Captain Hook is tough but allows three different ways of delivering a notification:

- by taking a webhook and producing events that can be subscribed for delivering notifications
- by taking an event and deliver notifications to its subscribers
- by taking the notification as is

When a webhook is captured, it will be analyzed in order to make an event from it. Usually we can't change a webhook's payload so any query parameter sent will be converted to a label. So if the crew receives a webhook with `foo=bar` in the url, the webhook will have a label `foo` with a value `bar`. This will help the crew to differentiate boarded webhooks to produce the events.

Events are a simple structure composed by:

- `type`: what differentiates this event from other events
- `labels` (optional): a set of labels that can be used for filtering if you need to be more specific than just a type
- `title` (optional): the title of this event, it will be used for notification purposes
- `message`: the message that describes this event, used for notifications
- `url` (optional): a url that contains more information about this event, used for notifications as well

A Notification is the result of attaching an Event, to an Address. It contains:

- `event`: contains an `Event` structure
- `destination`: contains the address that should receive the notification

Addresses are composed by a channel name and a target destination. On top of that, Transmitters are responsible for reaching the address to deliver the notification.

For example, the address `my_chat:some-user` will be reached by a Transmitter responsible for the channel `my_chat` that will pass the notification to the target `some-user`. This is totally dependent on the channel type as each platform has its own way of identifying targets (user, chat room, broadcast channel, etc.).

## How To Configure

Captain Hook expects a yaml file containing:

- transmitters
- virtual addresses
- event subscriptions
- webhook mappings

### Sensitive Inputs

Every sensitive configuration, such as passwords and tokens, can be provided through four ways:

- Specifying the value in the configuration file
```
token: token_in_plain_text
# using a key is also a valid way
token:
  value: token_in_plain_text
```
- Specifying an environment variable that contains the value
```
token:
  env: ENVIRONMENT_VARIABLE
```
- Specifying a JVM property that contains the value
```
token:
  property: some.property
```
- Specifying a file that holds the value
```
token:
  file: /path/to/file
```

Why specifying a file? In a Kubernetes/OpenShift environment you might want to use a Secret in order to store sensitive values since Secrets are mounted in a RAM-backed filesystem so the contents are always volatile.

### Transmitters

#### Transmitter Type

Back to the common Transmitter configuration, every configuration must include the key `type` so the Captain can pass the right command to the crew. The supported Transmitters are:

#### Telegram

Telegram Bots are a nice way to deliver notifications, you can create a bot and have a token in less than one minute. All the Captain needs is `telegram` as `type` and a sensitive input as `token`.

```yaml
transmitters:
  my_telegram_bot:
    type: telegram
    token: 999999999:01234567890ABCDEFGHIJ-KLMN
```

#### Pushover

Pushover is a notification service available for almost every platform. Captain knows how incredible it is and it's nice enough to allow you to configure it. All the Captain needs is `pushover` as `type` and a sensitive input as `token`.

```yaml
transmitters:
  pushover:
    type: pushover
    token:
      value: lanrbhgjaiou4872yuijuqy68f7uhiadyg78u3
```

#### Slack

Slack is widely used as a collaboration hub and it supports bots as well. Captain kinda needs to support it so all it needs is `slack` as `type`, a sensitive input as `token` and an optional `username` in case you wanna use your bot name.

```yaml
transmitters:
  ship_workspace:
    type: slack
    username: Peter Bot
    token:
      value: lanrbhgjaiou4872yuijuqy68f7uhiadyg78u3
```

#### HTTP

You can also uses a generic http call to forward the notification. What's the catch here? The payload is not customizable. Remember: Captain only cares about delivering notifications, integration between systems is not the focus on this ship.

In case you wanna use the HTTP Transmitter, let Captain know, besides `http` as `type`:

- `url`: the actual url that will receive the notification
- `headers`: the map containing any additional headers

```yaml
transmitters:
  my_awesome_receiver:
    type: http
    url: https://my.endpoint.integration/{destination}
    headers:
      from: captain-hook
```

If the `url` contains `{destination}`, it will be replaced by the subscriber's id.

And what about the payload? It's a JSON representation of the `Notification` object.

### Virtual Addresses

Virtual addresses can be used to shorten an address or to group multiple ones. If you often type `my_chat:my-user` you can short it to something like `me` and map it to that address.

```yaml
virtual_addresses:
  # the key can have the same name as a transmitter
  pushover: pushover:my-long-pushover-key
  my_telegram: telegram:99999999
  # you can have more than one address pointing to the same virtual address
  test:
  - pushover
  - my_telegram
```

### Subscriptions

To configure subscriptions, you need to inform the event type, the labels that should be present on the event and the destination to send the notification. Only the destination is required.

```yaml
subscriptions:
  # any event of type test will be sent to the virtual address test
- type: test
  destination: test
  # any event with the label from=nexus will be sent to the slack:devs address
- selector:
    from: nexus
  destination: slack:devs
  # any event will be sent to the dump virtual address
- destination: dump
```

### Webhook Mappings

To configure the webhooks, you need to inform how the payload will be converted into an event. The crew allows you to define the values using a Freemarker template. These are the 

```yaml
webhooks:
- selector:
    # the url should contain from=test as a query string
    from: test
  event:
    labels:
      # the /bar payload attribute will be used as the label value
      foo: ${foo}
      # nested attributes are also allowed, everything from Freemarker can be used here
      # just try to stick with short values for labels
      category: ${data.info.category}
    # type and message are required attributes
    type: test
    message: Test event from ${person.contact.phone}
```


