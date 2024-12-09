# DEMO-TECNICA SENSORES
Programa de demostración de sensores en Android Studio, realizado en la plantilla Empty Activity.
Realizamos la demostración de tres sensores: 

- Sensor Acelerómetro
- Sensor de giroscopio
- Sensor de temperatura

>[!IMPORTANT]
> En el programa debemos declarar un permiso en el *AndroidManifest.xml* para poder usar los sensores en la app. Este permiso será:
>   ``<uses-permission android:name="android.permission.BODY_SENSORS" />``

En la MainActivity tendremos la UI y la lógica de la app. Esta última se basa en las siguientes funciones:

**- AppBase:** Es la composable principal donde se gestiona la lógica del programa y se llama la UI (interfaz).

**- UI:** Es la interfaz de usuario donde podremos ver las tarjetas y los datos que generan los sensores.

**- comprobarDisponibilidadSensores:** Donde comprobamos si el dispositivo cuenta con los sensores que queremos usar en el programa.

**- validarCambioSensor:** Aqui se recibe la lista de valores que registra el sensor y los filtra para identificar si registra también el ruido.

**- TarjetaSensorMultiplesValores:** Aqui se muestra los datos de sensores de movimiento.

**- TarjetaSesnsorUnicoValor:** Aqui se muestra los datos del sensor de temperatura.


## Presentación del trabajo 
[LINK](https://view.genially.com/674afd5bcac198c8983dc871/presentation-presentacion-educacion-superior)
