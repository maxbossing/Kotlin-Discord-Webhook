package de.maxbossing.webhookbuilder

import java.awt.Color
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.HttpsURLConnection

/**
 * create a [Webhook] Object
 */
fun webhook(webhook: Webhook.() -> Unit) = Webhook().apply(webhook)

/**
 * create a [Webhook] and sends it to the speicified url
 */
fun sendWebhook(url: URL, webhook: Webhook.() -> Unit) = Webhook().apply(webhook).send(url)

/**
 * Shortcut to [URL]
 */
fun url(string: String) = URL(string)

/**
 * Represents a Message to a Webhook
 */
class Webhook {
    /**
     * Username of the Webhook
     */
    private var _name: String? = null

    /**
     * Plaintext content of the Message
     */
    private var _content: String? = null

    /**
     * Avatar of the Webhook
     */
    private var _avatar: String? = null

    /**
     * Embeds added to the Webhook
     */
    private var _embeds: MutableList<Embed> = mutableListOf()

    /**
     * Name of the thread to send the webhook to
     */
    private var _threadName: String? = null

    /**
     * EmbedFlags for this webhook
     */
    private var _embedFlags: MutableList<EmbedFlag> = mutableListOf()

    /**
     * sets the name
     */
    fun name(name: String) {
        _name = name
    }

    /**
     * sets the content
     */
    fun content(content: String) {
        _content = content
    }

    /**
     * sets the avatar url
     */
    fun avatar(avatarUrl: String) {
        _avatar = avatarUrl
    }

    /**
     * adds an embed
     */
    fun embed(embed: Embed.() -> Unit) {
        _embeds += Embed().apply(embed)
    }

    /**
     * sets the thread name
     */
    fun threadName(threadName: String) {
        _threadName = threadName
    }

    /**
     * adds an embed flag
     */
    fun embedFlags(vararg flags: EmbedFlag) {
        _embedFlags += flags
    }

    /**
     * builds the string to send
     */
    fun build(): String {

        val strings = mutableListOf<String>()

        // Username

        if (_name != null)
            if (_name!!.length > 80)
                throw IllegalArgumentException("Allowed maximum length for username is 80")
            else
                strings += "\"username\": \"$_name\""

        // Embeds

        strings += if (_embeds.isEmpty())
            "\"embeds\": null"
        else
            "\"embeds\": [${_embeds.map { it.build() }.joinToString(",")}]"

        // Content

        if (_content != null)
            if (_content!!.length > 2000)
                throw IllegalArgumentException("Content can only be 2000 characters or less")
            else
                strings += "\"content\": \"$_content\""

        if (_embeds.isEmpty() && _content == null)
            throw IllegalArgumentException("Webhooks need content or at least one embed!")

        // Avatar

        if (_avatar != null)
            strings += "\"avatar_url\": \"$_avatar\""

        // Thread

        if (_threadName != null)
            if (_threadName!!.length > 100)
                throw IllegalArgumentException("Thread name can only be 100 characters or less")
            else
                strings += "\"thread_name\": \"$_threadName\""

        // Embed Flags

        if (_embedFlags.isNotEmpty())
            strings += "\"flags\": ${_embedFlags.sumOf { it.value }}"

        if (_embedFlags.contains(EmbedFlag.SUPPRESS_EMBEDS) && _embeds.isNotEmpty())
            throw IllegalArgumentException("EmbedFlag.SUPPRESS_EMBEDS is incompatible with Rich Embeds!")

        return "{ " + strings.joinToString(",") + " }"
    }

    /**
     * sends the webhook
     */
    fun send(url: URL) {
        val content = build()

        val connection = url.openConnection() as HttpsURLConnection
        connection.addRequestProperty("Content-Type", "application/json")
        connection.addRequestProperty("User-Agent", "Kotlin-Discord-Webhook-DSL")
        connection.doOutput = true
        connection.requestMethod = "POST"

        val stream = connection.outputStream
        stream.write(content.encodeToByteArray())
        stream.flush()
        stream.close()

        connection.inputStream.close() //I'm not sure why but it doesn't work without getting the InputStream
        connection.disconnect()
    }
}

/**
 * Represents an Embed of a [Webhook]
 */
class Embed {
    /**
     * title of the embed
     */
    private var _title: String? = null

    /**
     * description of the embed
     */
    private var _description: String? = null

    /**
     * URL of the embed
     */
    private var _url: String? = null

    /**
     * [Footer] of the embed
     */
    private var _footer: Footer? = null

    /**
     * [Thumbnail] of the embed
     */
    private var _thumbnail: Thumbnail? = null

    /**
     * [Image] of the embed
     */
    private var _image: Image? = null

    /**
     * [Author] of the embed
     */
    private var _author: Author? = null

    /**
     * [Fields] of the embed
     */
    private var _fields: MutableList<Field> = mutableListOf()

    /**
     * color of the Embed
     */
    private var _color: Int? = null

    /**
     * timeStamp of the embed
     */
    private var _timeStamp: Date? = null


    /**
     * sets the title
     */
    fun title(title: String) {
        _title = title
    }

    /**
     * sets the description of the embed
     */
    fun description(description: String) {
        _description = description
    }

    /**
     * sets the url of the embed
     */
    fun url(url: String) {
        _url = url
    }

    /**
     * sets the footer of the embed
     */
    fun footer(footer: Footer.() -> Unit) {
        _footer = Footer().apply(footer)
    }

    /**
     * sets the thumbnail of the embed
     */
    fun thumbnail(thumbnail: Thumbnail.() -> Unit) {
        _thumbnail = Thumbnail().apply(thumbnail)
    }

    /**
     * sets the image of the embed
     */
    fun image(image: Image.() -> Unit) {
        _image = Image().apply(image)
    }

    /**
     * sets the author of the embed
     */
    fun author(author: Author.() -> Unit) {
        _author = Author().apply(author)
    }

    /**
     * adds a field to the embed
     */
    fun field(field: Field.() -> Unit) {
        _fields += Field().apply(field)
    }

    /**
     * sets the color of the embed
     * @see EmbedColors
     */
    fun color(color: Int) {
        _color = color
    }

    /**
     * sets the color of the embed
     * @see EmbedColors
     */
    fun color(color: Color) {
        _color = EmbedColors.encode(color)
    }

    /**
     * sets the color of the embed
     * @see EmbedColors
     */
    fun color(r: Int, g: Int, b: Int) {
        _color = EmbedColors.encode(r, g, b)
    }

    /**
     * sets the timestampt of the embed
     */
    fun timeStamp(timeStamp: Date) {
        _timeStamp = timeStamp
    }

    /**
     * serializes the embed to send
     */
    fun build(): String {
        val strings = mutableListOf<String>()

        if (_description == null && _fields.isEmpty())
            throw IllegalArgumentException("Embeds need a description or at least 1 field!")

        if (_title != null)
            if (_title!!.length > 256)
                throw IllegalArgumentException("Embed title cannot be greater than 256")
            else
                strings += "\"title\": \"$_title\""

        if (_description != null)
            if (_description!!.length > 4096)
                throw IllegalArgumentException("Embed description cannot be greater than 4096")
            else
                strings += "\"description\": \"$_description\""

        if (_color != null)
            strings += "\"color\": $_color"

        if (_url != null)
            strings += "\"url\": \"$_url\""

        if (_footer != null)
            strings += "\"footer\": ${_footer!!.build()}"

        if (_thumbnail != null)
            strings += "\"thumbnail\": ${_thumbnail!!.build()}"

        if (_image != null)
            strings += "\"image\": ${_image!!.build()}"

        if (_author != null)
            strings += "\"author\": ${_author!!.build()}"

        if (_fields.isNotEmpty())
            strings += "\"fields\": [${_fields.map { it.build() }.joinToString(",")}]"


        if (_timeStamp != null) {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")

            strings += "\"timestamp\": \"${format.format(_timeStamp)}\""
        }

        return "{ ${strings.joinToString(",")} }"


    }
}

/**
 * Represents an Author of an [Embed]
 */
class Author {
    /**
     * name of the author
     */
    private var _name: String? = null

    /**
     * url of the author
     */
    private var _url: String? = null

    /**
     * icon url of the author
     */
    private var _iconUrl: String? = null

    /**
     * sets the name of the author
     */
    fun name(name: String) {
        _name = name
    }

    /**
     * sets the url of the author
     */
    fun url(url: String) {
        _url = url
    }

    /**
     * sets the icon url of the author
     */
    fun icon(url: String) {
        _iconUrl = url
    }

    /**
     * serializes to be ready to send
     */
    fun build(): String {
        if (_name == null)
            throw IllegalArgumentException("Author cannot be null!")

        if (_name!!.length > 256)
            throw IllegalArgumentException("Author name cannot be greater than 256")

        return "{ \"name\": \"$_name\" ${if (_url != null) ", \"author_url\": \"$_url\"" else ""} ${if (_iconUrl != null) ", \"icon_url\": \"$_iconUrl\"" else ""} }"
    }

}

/**
 * Represents a Field of an [Embed]
 */
class Field {
    /**
     * name of the field
     */
    private var _name: String? = null

    /**
     * value of the field
     */
    private var _value: String? = null

    /**
     * whether the field should be inline
     */
    var inline: Boolean = false

    /**
     * sets the name of the field
     */
    fun name(name: String) {
        _name = name
    }

    /**
     * sets the value of the field
     */
    fun value(value: String) {
        _value = value
    }

    /**
     * serializes to be ready to send
     */
    fun build(): String {
        if (_name == null)
            throw IllegalArgumentException("Name cannot be null!")

        if (_value == null)
            throw IllegalArgumentException("Value cannot be null!")

        if (_name!!.length > 256)
            throw IllegalArgumentException("Field name length cannot be greater than 256")

        if (_value!!.length > 1024)
            throw IllegalArgumentException("Field value length cannot be greater than 1024")

        return "{\"name\":\"$_name\", \"value\":\"$_value\", \"inline\":$inline}"
    }


}

/**
 * Represents the footer of an [Embed]
 */
class Footer {
    /**
     * Text in the footer
     */
    private var _text: String? = null

    /**
     * icon of the footer
     */
    private var _icon: String? = null

    /**
     * sets the text of the footer
     */
    fun text(text: String) {
        _text = text
    }

    /**
     * sets the icon of the footer
     */
    fun icon(iconUrl: String) {
        _icon = iconUrl
    }

    /**
     * serializes to be ready to send
     */
    fun build(): String {
        if (_text == null)
            throw IllegalArgumentException("Footer text cannot be null!")

        if (_text!!.length > 2048)
            throw IllegalArgumentException("Footer text length cannot be greater than 2048")

        return "{ \"text\": \"$_text\" ${if (_icon != null) ", \"icon_url\": \"$_icon\"" else ""} }"
    }

}

/**
 * Represents the Thumbnail of an [Embed]
 */
class Thumbnail {
    /**
     * url of the thumbnail
     */
    private var _url: String? = null

    /**
     * sets the url of the thumbnail
     */
    fun url(url: String) {
        _url = url
    }

    /**
     * serializes to be ready to send
     */
    fun build(): String {
        if (_url == null)
            throw IllegalArgumentException("Thumbnail url cannot be null")
        return "{ \"url\": \"$_url\" }"
    }
}

/**
 * Represents an image of an [Embed]
 */
class Image {

    /**
     * url of the image
     */
    private var _url: String? = null

    /**
     * sets the url of the image
     */
    fun url(url: String) {
        _url = url
    }

    /**
     * serializes to be ready to send
     */
    fun build(): String {
        if (_url == null)
            throw IllegalArgumentException("Image URL Cannot be null")
        return "{ \"url\": \"$_url\" }"
    }
}

/**
 * Constants and functions for colors in [Embed]s
 */
object EmbedColors {
    const val BLACK = 0
    const val BLUE = 255
    const val GREEN = 65280
    const val RED = 16711680
    const val MAGENTA = 16711935
    const val BROWN = 16733440
    const val LIGHT_GRAY = 11184810
    const val DARK_GRAY = 5592405
    const val LIGHT_BLUE = 5592575
    const val LIGHT_GREEN = 5635925
    const val LIGHT_RED = 16733525
    const val LIGHT_MAGENTA = 16733695
    const val YELLOW = 16777045
    const val WHITE = 16777215

    /**
     * encodes rgb vallues into discord format
     */
    fun encode(r: Int, g: Int, b: Int): Int = ((r and 0x0ff) shl 16) or ((g and 0x0ff) shl 8) or (b and 0x0ff)

    /**
     * encodes a [Color] into discord format
     */
    fun encode(color: Color): Int = encode(color.red, color.green, color.blue)
}

/**
 * Embed flags
 */
enum class EmbedFlag(val value: Int) {
    /**
     * Hides link embeds
     *
     * Cannot be used with embeds
     */
    SUPPRESS_EMBEDS(4),

    /**
     * If the messages contains mentions, users will not be notified
     */
    SUPPRESS_NOTIFICATIONS(4096)
}