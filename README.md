# Kotlin Discord Webhook
[ ![Latest version](https://img.shields.io/maven-central/v/de.maxbossing/kotlin-discord-webhook?color=pink&label=latest%20version&style=for-the-badge) ](https://repo1.maven.org/maven2/de/maxbossing/kotlin-discord-webhook)
> Small library to build and send Discord Webhooks

# ToDo
* [ ] File attachments
* [ ] Multiple Images if URL is set

# Usage
1. Import the library into your project

Gradle (kts)
```kotlin
dependencies {
    implementation("de.maxbossing:kotlin-discord-webhook:VERSION")
}
```
Gradle (goovy)
```groovy
dependencies {
    implementation 'de.maxbossing:kotlin-discord-webhook:VERSION'
}
```
Maven
```xml
<dependency>
    <groupId>de.maxbossing</groupId>
    <artifactID>kotlin-discord-webhook</artifactID>
    <version>VERSION</version>
</dependency>
```

2. Use the DSL to create a Webhook
```kotlin

val hook = webhook {
    name("This is the webhooks name")
    //If you just want to send a message, this does fine
    content("This is a message")
    // But if this is not enough, you can create rich embeds 
    embed {
        title("This is the embeds title!")
        description("Again, for simple embeds this is fine")
        
        field {
            name("But if you need more")
            value("You can do that easily")
        }
        
        thumbnail {
            // Just put an Image url here and it will be put as the thumbnail
            url("")
        }
        
        footer {
            text("And now, to conclude this great embed, How easy was that?")
        }
        
        // Just set tthe author so it looks even more classy
        author {
            name("The easiest embed builder")
            icon("some funny icon")
        }
    }
}
```
3. and now just send it
```kotlin
hook.send(url("You webhook url"))
```
4. That will display this great thing:  
![img](assets/webhook.png)

> [!INFO]  
> If you do not need to reuse the webhook, you can use the shortcut sendWebhook("url") {  } 
>

# License
This Product and its source code is released under the [GNU Lesser General Public License v2.1](LICENSE.md)