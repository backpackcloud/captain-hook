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

- `name`: what differentiates this event from other events
- `labels` (optional): a set of labels that can be used for filtering if you need to be more specific than just a type
- `title` (optional): the title of this event, it will be used for notification purposes
- `message`: the message that describes this event, used for notifications
- `url` (optional): a url that contains more information about this event, used for notifications as well

A Notification is the result of attaching an Event, to an Address. It contains:

- `title` (optional): the title of this event, it will be used for notification purposes
- `message`: the message that describes this event, used for notifications
- `url` (optional): a url that contains more information about this event, used for notifications as well
- `destination`: contains the address that should receive the notification
- `priority`: the priority of this notification (`low`, `normal` or `high`)

Addresses are composed by a channel name and a target destination. On top of that, Transmitters are responsible for reaching the address to deliver the notification.

For example, the address `my_chat:some-user` will be reached by a Transmitter responsible for the channel `my_chat` that will pass the notification to the target `some-user`. This is totally dependent on the channel type as each platform has its own way of identifying targets (user, chat room, broadcast channel, etc.).

## How To Configure

Captain Hook expects a yaml file containing:

- transmitters
- virtual addresses
- event subscriptions
- webhook mappings

The file can be passed by either:

- `-Dconfig.file=/path/to/file` JVM parameter
- `CONFIG_FILE=/path/to/file` environment variable

### Sensitive Parameters

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

Back to the common Transmitter configuration, every configuration must include the key `type` so the Captain can pass the right command to the crew. You can have as much as Transmitters you need as long as they have different names. The only name restriction is `virtual` which is used internally to represent a virtual address (more on that later) and thus should not be used to create a Transmitter.

#### Telegram

Telegram Bots are a nice way to deliver notifications, you can create a bot and have a token in less than one minute. All the Captain needs is `telegram` as `type`, a sensitive input as `token` and, optionally, a Freemarker template for how to structure the message.

```yaml
transmitters:
  my_telegram_bot:
    type: telegram
    token: 999999999:01234567890ABCDEFGHIJ-KLMN
    template: |
      ${(title)!}
      ${message}
      ${(url)!}
```

The variables that can be used in the template are: `title`, `message`, `url`, `destination` and `priority`. All related to the notification.
 
#### Pushover

Pushover is a notification service available for almost every platform. Captain knows how incredible it is and it's nice enough to support it. All the Captain needs is `pushover` as `type` and a sensitive input as `token`.

```yaml
transmitters:
  pushover:
    # remember: type is required, even if it is the same name as the transmitter
    type: pushover
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
  # any event of name test will be sent to the virtual address test
- name: test
  destination: test
  # any event with the label from=nexus will be sent to the telegram:devs address
- selector:
    from: nexus
  destination: telegram:devs
  # any event will be sent to the dump virtual address
- destination: dump
```

### Webhook Mappings

To configure the webhooks, you need to inform how the payload will be converted into an event. The crew allows you to define the values using a Freemarker template. This is the structure of a webhook mapping:

```yaml
webhooks:
- selector:
    # the url should contain from=test as a query string
    # or the header "from" should contain the value "test"
    from: test
  event:
    labels:
      # the /bar payload attribute will be used as the label value
      foo: ${foo}
      # nested attributes are also allowed, everything from Freemarker can be used here
      # just try to stick with short values for labels
      category: ${data.info.category}
    # name and message are required attributes
    name: test
    message: Test event from ${person.contact.phone}
```

The `selector` will be used to check if the webhook should be processed by this mapping. The `event` is the structure of the event that will be produced. Note that if you don't define a selector, every webhook will be processed by the same mapping.

#### Webhook Examples

Bellow are some examples of how to use Captain Hook with known tools for leverage notifications.

##### Gitlab

```yaml
webhooks:
- selector:
    # GitLab sends this header so we can grab it on the selector
    X-Gitlab-Event: Push Hook
  event:
    # use anything for labels that makes easy to make good filtering
    labels:
      source: gitlab
      type: push
      project: ${project.name}
      ref: ${ref}
      user: ${user_username}
    name: gitlab.push
    title: New push - ${project.name}
    url: ${project.web_url}
    message: |
      ${total_commits_count} commits pushed by ${user_name}
```

This example will raise an event every time a GitLab push hook is received. Since the labels provide information about the project, branch and user, we could have subscriptions for those specific labels as well:

```yaml
- selector:
    source: gitlab
  # virtual address
  destination: pushover_gitlab
- selector:
    source: gitlab
    type: push
    project: captain_hook
    ref: refs/heads/master
  # virtual address
  destination: captain_hook_team
```

With this example, every event from gitlab will notify the `pushover_gitlab` address but, if the event is also for a push on the `master` branch of the `captain_hook` project, the event will also notify the `captain_hook_team` virtual address.