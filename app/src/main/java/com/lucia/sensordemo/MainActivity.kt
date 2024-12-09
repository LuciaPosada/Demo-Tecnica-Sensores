package com.lucia.sensordemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sensores
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val giroscopio = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        val sensorTemperatura = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

        setContent {
            AppBase(sensorManager, acelerometro, giroscopio, sensorTemperatura)
        }
    }
}

/**
 * Comprueba si los sensores estan disponibles
 * @param acelerometro Sensor de tipo acelerómetro, o `null` si no está disponible
 * @param giroscopio Sensor de tipo giroscopio, o `null` si no está disponible
 * @param sensorTemperatura Sensor de tipo temperatura, o `null` si no está disponible
 */
fun comprobarDisponibilidadSensores(acelerometro: Sensor?, giroscopio: Sensor?, sensorTemperatura: Sensor?){

    if(acelerometro==null){
        Log.d("Acelerometro","No Disponible")
    }else{
        Log.d("Acelerometro","Disponible")
    }

    if(sensorTemperatura==null){
        Log.d("STemperatura","No Disponible")
    }else{
        Log.d("STemperatura","Disponible")
    }

    if(giroscopio==null){
        Log.d("Giroscopio","No Disponible")
    }else{
        Log.d("Giroscopio","Disponible")
    }
}

/**
 * Composable principal que gestiona la lógica de los sensores y muestra la interfaz de usuario
 * @param sensorManager Instancia del SensorManager para manejar los sensores del dispositivo
 * @param acelerometro Sensor de tipo acelerómetro, o `null` si no está disponible
 * @param giroscopio Sensor de tipo giroscopio, o `null` si no está disponible
 * @param sensorTemperatura Sensor de tipo sensor de temperatura, o `null` si no está disponible
 */
@Composable
fun AppBase(sensorManager: SensorManager, acelerometro: Sensor?, giroscopio: Sensor?, sensorTemperatura: Sensor?) {
    var valoresAcelerometro by remember { mutableStateOf(listOf(0f, 0f, 0f)) }
    var valoresGiroscopio by remember { mutableStateOf(listOf(0f, 0f, 0f)) }
    var valorTemperatura by remember { mutableStateOf(0.0f) }

    val humbralDeActualizacion = 0.5f

    DisposableEffect(Unit) { // maneja los listeners
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    when (it.sensor.type) {
                        Sensor.TYPE_ACCELEROMETER -> valoresAcelerometro = validarCambioSensor(it.values.toList(), humbralDeActualizacion)
                        Sensor.TYPE_GYROSCOPE -> valoresGiroscopio = validarCambioSensor(it.values.toList(), humbralDeActualizacion)
                        Sensor.TYPE_AMBIENT_TEMPERATURE -> valorTemperatura = it.values[0]
                    }
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        // Registro de los listeners
        acelerometro?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_NORMAL) }
        giroscopio?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_NORMAL) }
        sensorTemperatura?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_NORMAL) }

        // Eliminacion de los listeners al finalizar
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }
    UI(valoresAcelerometro, valoresGiroscopio, valorTemperatura)
}

/**
 * Filtra los valores de un sensor para reportar únicamente los cambios significativos
 * @param valores Lista de valores registrados por el sensor
 * @param humbral Valor mínimo de cambio requerido para considerar una actualización
 * @return Lista de valores filtrados, estableciendo en `0f` aquellos que no superen el umbral
 */
fun validarCambioSensor(valores: List<Float>, humbral: Float): List<Float> {
    return valores.map { if (Math.abs(it) >= humbral) it else 0f }
}

/**
 * Interfaz de usuario principal que muestra los datos capturados por los sensores
 * @param valoresAcelerometro Lista con las mediciones del acelerómetro
 * @param valoresGiroscopio Lista con las mediciones del giroscopio
 * @param valorTemperatura Valor del contador de la temperatura
 */
@Composable
fun UI(valoresAcelerometro: List<Float>, valoresGiroscopio: List<Float>, valorTemperatura: Float) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Demo Técnica", fontSize = 25.sp, modifier = Modifier.padding(16.dp))

        TarjetaSensorMultiplesValores(
            tituloSensor = "Acelerómetro",
            valoresSensor = valoresAcelerometro
        )

        Spacer(modifier = Modifier.height(16.dp))

        TarjetaSensorMultiplesValores(
            tituloSensor = "Giroscopio",
            valoresSensor = valoresGiroscopio
        )

        Spacer(modifier = Modifier.height(16.dp))

        TarjetaSensorUnicoValor(
            tituloSensor = "Sensor Temperatura",
            valorSensor = valorTemperatura
        )

        Spacer(modifier = Modifier.height(16.dp))

    }
}

/**
 * Tarjeta para mostrar los datos de sensores basados en ejes (x, y, z)
 * @param tituloSensor Nombre del sensor
 * @param valoresSensor Lista de las mediciones
 */
@Composable
fun TarjetaSensorMultiplesValores(tituloSensor: String, valoresSensor: List<Float>) {

    val ejes = listOf("x","y","z") // nombres de los ejes

    Card(
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = tituloSensor, fontSize = 20.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp)) // espacio
            valoresSensor.forEachIndexed { index, value ->
                Text(text = "Eje ${ejes.getOrNull(index)}: ${"%.2f".format(value)}", fontSize = 16.sp)
            }
        }
    }
}

/**
 * Tarjeta para mostrar datos de sensores con un único valor
 * @param tituloSensor Nombre del sensor
 * @param valorSensor Valor registrado por el sensor
 */
@Composable
fun TarjetaSensorUnicoValor(tituloSensor: String, valorSensor: Float) {

    Card(
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = tituloSensor, fontSize = 20.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp)) // espacio
            if(tituloSensor.equals("Sensor Temperatura")){
                Text(text = "Temperatura: ${"%.2f".format(valorSensor)} Cª", fontSize = 16.sp)
            }
        }
    }
}