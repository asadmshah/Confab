# Confab

A helper library for using the [Nearby Connections API](https://developers.google.com/nearby/connections/overview).
This library "enables your app to easily discover other devices on a local network, connect, and exchange messages in real-time".

## Usage

The Nearby Connections API requires a `service_id` value in your manifest:
```xml
<application>
  <!-- Required for Nearby Connections API -->
  <meta-data android:name="com.google.android.gms.nearby.connection.SERVICE_ID"
            android:value="@string/service_id" />
  <activity>
      ...
  </activity>
</application>
```

### Advertising

To establish connections, one device must promote itself as an `Advertiser`. Other `DiscovererEndpoint`
will then attempt to find it and request access to it.

```java
// Same value given in manifest
String serviceId = getContext().getString(R.string.service_id);

// if null device name is used (ie: LGE Nexus 5)
String name = "My endpoint name"

Advertiser advertiser = new Advertiser(getContext(), serviceId, name);
advertiser.addCallbackListener(new AdvertiserCallbacks() {...});

// use start() for advertising indefinitely.
advertiser.start(TIME_TO_ADVERTISE_FOR_IN_MILLISECONDS)
```

The `Advertiser` will not actively go searching for `DiscovererEndpoints`. Once a `DiscovererEndpoint`
finds the desired `Advertiser` it will attempt to make a connection with it. The endpoint requesting
a connection will be revealed through `AdvertiserCallbacks.onDiscovererEndpointConnectionRequest`:

```java
@Override
public void onDiscovererEndpointConnectionRequest(DiscovererEndpoint endpoint, byte[] data) {
    this.endpoint = endpoint;
    this.endpoint.addCallbackListener(this);
    this.endpoint.acceptConnectionRequest("This is me accepting".getBytes());
}
```

Use `acceptConnectionRequest` to accept the connection. The method takes an optional `byte[]` payload.
The interface to listen to messages/status updates for a `DiscovererEndpoint` is `DiscovererEndpointCallbacks`.

Use `rejectConnectionRequest` to block anymore connection requests from a specific endpoint. This method
will not do anything with endpoints that are already connected.


### Discovering

A `Discoverer` will look for `AdvertiserEndpoints` to connect to:

```java
// Same value given in manifest
String serviceId = getContext().getString(R.string.service_id);

// if null device name is used (ie: LGE Nexus 5)
String name = "My endpoint name"

Discoverer discoverer = new Discoverer(getContext(), serviceId, name);
discoverer.addCallbackListener(new DiscovererCallbacks() {...});

// use start() for discovering indefinitely
discoverer.start(TIME_TO_DISCOVER_FOR_IN_MILLISECONDS)
```

Once an `AdvertiserEndpoint` is found using the `serviceId` you provided, it will be revealed through
`DiscovererCallbacks.onAdvertiserEndpointFound`. You can then send a connection request to it:

```java
@Override
public void onAdvertiserEndpointFound(AdvertiserEndpoint endpoint) {
    this.endpoint = endpoint;
    this.endpoint.addCallbackListener(this);
    this.endpoint.sendConnectionRequest("This is my connection request".getBytes());
}
```

Use `sendConnectionRequest` to send a connection request. The method takes an optional `byte[]` payload.
The interface to listen to messages/status updates for a `AdvertiserEndpoint` is `AdvertiserEndpointCallbacks`.

### Sending Messages

You have two options to send a message, sending a reliable or unreliable message. 

Reliable messages are guaranteed to be received in the order they were sent, and the system retries 
sending the message until the connection ends. __Reliable messages can be up to 4096 bytes long.__

To send a reliable message use:
```java
endpoint.sendReliableMessage("Your message".getBytes());
```

Unreliable messages allows the system to deliver messages quickly, but the messages may 
be lost or sent out of order. __Unreliable messages can be up to 1168 bytes long.__

To send an unreliable message use:
```java
endpoint.sendUnreliableMessage("Your message".getBytes());
```

## License
    Copyright 2015 Asad Shah

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.