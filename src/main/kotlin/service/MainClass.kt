package service

import YeelightDevice
import com.mollin.yapi.enumeration.YeelightEffect
import de.androidpit.colorthief.ColorThief
import fr.yoanndiquelou.jeelight.model.Light
import fr.yoanndiquelou.jeelight.ssdp.SSDPClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.awt.*
import java.awt.image.BufferedImage
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import kotlin.math.abs

var l = Light()

fun main() {
    println("Hello, World!")

//    service.discoverLights()


    var device = YeelightDevice("192.168.0.40") //40 166
    device.setEffect(YeelightEffect.SMOOTH)
    device.setDuration(350)

    device.setPower(true)


    var currentTime = System.currentTimeMillis()
    Thread {
        var currentColor = intArrayOf(0, 0, 0)
        val screenRect = getScreenRect()
        var color: IntArray?

        while (true) {


            val screenBitmap = Robot().createScreenCapture(screenRect)
//            val screenBitmap = service.getScreenCompressed(screenRect!!)


            color = ColorThief.getColor(screenBitmap)
            if (color == null)
                continue
//            val color = service.getFilteredColor(screenBitmap)


            if (areNotSimilarColors(color, currentColor)) {
                println("${color[0]}, ${color[1]}, ${color[2]}")


//                Thread(Runnable {
                println("change!")
                GlobalScope.launch {
                    try {
                        device.setRGB(color[0], color[1], color[2])
                    } catch (e: Exception) {
                        Thread.sleep(450)
                        device = YeelightDevice("192.168.0.40")
                    }
                }

//                }).start()

//                Thread(Runnable {
//                    try {
//                        val hsb = floatArrayOf(0f, 0f, 0f)
//                        Color.RGBtoHSB(color[0], color[1], color[2], hsb)
//                        device.setBrightness((hsb[2] * 255).toInt())
//
//                    } catch (ignored: Exception) {
//                        ignored.printStackTrace()
//                    }
//                }).start()


                currentColor = color
                Thread.sleep(450)
            }

            println("time spend = ${System.currentTimeMillis() - currentTime} ms")
//            println("${color[0]}, ${color[1]}, ${color[2]}")

            currentTime = System.currentTimeMillis()
            Thread.sleep(300)
        }
    }.apply { priority = Thread.MIN_PRIORITY }.start()

}

fun discoverLights() {
    val devices: ArrayList<Light> = ArrayList()
    val client = SSDPClient(1, "wifi_bulb", 1982)
    client.addListener(object : PropertyChangeListener {
        override fun propertyChange(evt: PropertyChangeEvent) {

            if (SSDPClient.ADD == evt.getPropertyName()) {
                val l: Light = evt.getNewValue() as Light
                devices.add(l)
                println("device found - ${l.model} ${l.ip} ${l.isPower} ${l.rgb}")

//                if (service.getL.model.contains("strip6")) {
//                    val eLight = EasyLight(service.getL)
//                }
            }
        }
    })

    client.startDiscovering()
}

fun getScreenCompressed(screenRect: Rectangle): BufferedImage {
    val robot = Robot()
    val divider = 500
    val bufferedImage =
        BufferedImage(screenRect.width / divider, screenRect.height / divider, BufferedImage.TYPE_INT_RGB)
    for (x in 0..bufferedImage.width)
        for (y in 0..bufferedImage.height) {
            println("$x $y")
            val pixelColor = robot.getPixelColor(x * divider, y * divider)
            try {
                bufferedImage.setRGB(pixelColor.red, pixelColor.green, pixelColor.blue)
            } catch (ignoerd: Exception) {

            }
        }

    return bufferedImage
}

private fun getFilteredColor(screenBitmap: BufferedImage): IntArray {
    val colors = ColorThief.getPalette(screenBitmap, 5)
    for (c in colors) {
        val hsb = floatArrayOf(0f, 0f, 0f)
        Color.RGBtoHSB(c[0], c[1], c[2], hsb)

        val s = hsb[1]
        val b = hsb[2]
        if (s > 0.4) {
            println(b)
            return c
        }
    }
    return colors[0]
}

fun areNotSimilarColors(color: IntArray, currentColor: IntArray): Boolean {
    val hsb1 = floatArrayOf(0f, 0f, 0f)
    Color.RGBtoHSB(color[0], color[1], color[2], hsb1)

    val hsb2 = floatArrayOf(0f, 0f, 0f)
    Color.RGBtoHSB(currentColor[0], currentColor[1], currentColor[2], hsb2)

//    println("----")
//
//    println(abs(hsb1[0] - hsb2[0]))
//    println(abs(hsb1[1] - hsb2[1]))
//    println(abs(hsb1[2] - hsb2[2]))

    if (abs(hsb1[0] - hsb2[0]) > 0.04)
        return true
    if (abs(hsb1[1] - hsb2[1]) > 0.1)
        return true
    if (abs(hsb1[2] - hsb2[2]) > 0.1)
        return true
    return false
}

fun getScreenRect(): Rectangle? {
    val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
    val heightCrop = (screenSize.height * 0.3).toInt()
    val widthCrop = (screenSize.width * 0.25).toInt()
    return Rectangle(widthCrop, heightCrop, (screenSize.width - 2 * widthCrop), (screenSize.height - 2 * heightCrop))
}